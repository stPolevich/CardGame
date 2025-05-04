package com.example.CardGame.service

import com.example.CardGame.config.AppProperties
import com.example.CardGame.dto.request.ApplyStealRequest
import com.example.CardGame.dto.request.DrawCardRequest
import com.example.CardGame.dto.request.MakeTurnRequest
import com.example.CardGame.dto.response.DrawCardResponse
import com.example.CardGame.dto.response.GameSessionResponse
import com.example.CardGame.dto.response.TurnResponse
import com.example.CardGame.entity.*
import com.example.CardGame.enums.*
import com.example.CardGame.exceptions.GameException
import com.example.CardGame.mapper.PlayerMapper
import com.example.CardGame.repository.*
import com.example.CardGame.service.сard.CardEffectFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime


@Service
class GameSessionService(
    private val gameSessionRepository: GameSessionRepository,
    private val playerRepository: PlayerRepository,
    private val userRepository: UserRepository,
    private val cardRepository: CardRepository,
    private val turnRepository: TurnRepository,
    private val playerMapper: PlayerMapper,
    private val appProperties: AppProperties
) {
    @Transactional
    fun createGame(createdByUserId: Long): GameSessionResponse {
        val user = userRepository.findByIdOrThrow(createdByUserId)
        val game = GameSession(
            status = GameStatus.WAITING_FOR_PLAYERS,
            maxPlayers = appProperties.validation.game.maxPlayers,
            targetScore = appProperties.validation.game.targetScore
        )
        val savedGame = gameSessionRepository.save(game)
        playerRepository.save(
            Player(
                user = user,
                gameSession = savedGame,
                score = 0,
                isBlocked = false,
                turnOrder = 0
            )
        )
        return toGameSessionResponse(savedGame)
    }

    @Transactional
    fun joinGame(gameId: Long, userId: Long): GameSessionResponse {
        val game = gameSessionRepository.findByIdOrThrow(gameId)
        validateGameJoin(game, userId)
        val user = userRepository.findByIdOrThrow(userId)
        val turnOrder = game.players.size
        playerRepository.save(
            Player(user = user, gameSession = game, score = 0, isBlocked = false, turnOrder = turnOrder)
        )
        return toGameSessionResponse(game)
    }

    @Transactional
    fun startGame(gameId: Long, startedByUserId: Long): GameSessionResponse {
        val game = gameSessionRepository.findByIdOrThrow(gameId)
        validateGameStart(game, startedByUserId)
        try {
            initializeDeck(game)
            game.status = GameStatus.IN_PROGRESS
            game.currentTurnPlayerId = game.players.first().id
            val savedGame = gameSessionRepository.save(game)
            return toGameSessionResponse(savedGame)
        } catch (e: Exception) {
            throw IllegalStateException("Failed to start game: ${e.message}")
        }
    }

    private fun initializeDeck(game: GameSession) {
        val templates = cardRepository.findAll()
        val deck = mutableListOf<Long>()
        templates.forEach { template ->
            deck.add(template.id)
        }
        deck.shuffle()
        game.deck = deck
    }

    @Transactional
    fun makeTurn(gameId: Long, request: MakeTurnRequest): TurnResponse {
        val game = gameSessionRepository.findByIdOrThrow(gameId)
        val player = playerRepository.findByIdOrThrow(request.playerId)
        validateTurn(game, player)
        val card = drawCard(game)
        val turnResult = processCard(game, player, card, request.targetPlayerId)
        game.deck.removeAt(0)
        gameSessionRepository.save(game)
        updateGameState(game, player, turnResult)
        val savedTurn = saveTurn(game, player, card, turnResult)
        return toTurnResponse(savedTurn, turnResult.nextPlayerId)
    }

    @Transactional
    fun drawCard(gameId: Long, request: DrawCardRequest): DrawCardResponse {
        val game = gameSessionRepository.findByIdOrThrow(gameId)
        val player = playerRepository.findByIdOrThrow(request.playerId)
        validateTurn(game, player)
        val card = drawCard(game)
        return when (card.type) {
            CardType.POINTS, CardType.ACTION -> {
                if (card.actionType == ActionType.STEAL) {
                    // Не завершаем ход, ждём цель
                    DrawCardResponse(
                        cardType = "STEAL",
                        cardId = card.id,
                        message = "Выберите игрока, у которого хотите украсть очки",
                        turnResult = null
                    )
                } else {
                    val turnResult = processCard(game, player, card, null)
                    // Удаляем карту из колоды (сброс)
                    game.deck.removeAt(0)
                    gameSessionRepository.save(game)
                    updateGameState(game, player, turnResult)
                    DrawCardResponse(
                        cardType = card.type.name,
                        cardId = card.id,
                        message = null,
                        turnResult = toTurnResponse(saveTurn(game, player, card, turnResult), turnResult.nextPlayerId)
                    )
                }
            }
        }
    }

    @Transactional
    fun applySteal(gameId: Long, request: ApplyStealRequest): TurnResponse {
        val game = gameSessionRepository.findByIdOrThrow(gameId)
        val player = playerRepository.findByIdOrThrow(request.playerId)
        validateTurn(game, player)
        val card = cardRepository.findByIdOrThrow(request.cardId)
        val targetPlayer = playerRepository.findByIdOrThrow(request.targetPlayerId)
        if (card.actionType != ActionType.STEAL) {
            throw GameException("Некорректная карта для действия Steal")
        }
        if (player.id == targetPlayer.id) {
            throw GameException("Нельзя украсть очки у самого себя")
        }
        val turnResult = processActionCard(game, player, card, targetPlayer)
        // Удаляем карту из колоды (по id)
        game.deck.remove(card.id)
        gameSessionRepository.save(game)
        updateGameState(game, player, turnResult)
        return toTurnResponse(saveTurn(game, player, card, turnResult), turnResult.nextPlayerId)
    }

    fun getGameSession(gameId: Long): GameSessionResponse {
        return toGameSessionResponse(gameSessionRepository.findByIdOrThrow(gameId))
    }

    // --- Private methods ---

    private fun drawCard(game: GameSession): Card {
        val cardId = game.deck.firstOrNull() ?: throw GameException("No cards left in the deck")
        return cardRepository.findById(cardId)
            .orElseThrow { GameException("Card not found") }
    }

        private fun processCard(
            game: GameSession,
            player: Player,
            card: Card,
            targetPlayerId: Long?
        ): TurnResult {
            val targetPlayer = targetPlayerId?.let { playerRepository.findByIdOrThrow(it) }

            return when (card.type) {
                CardType.POINTS -> processPointsCard(player, card)
                CardType.ACTION -> processActionCard(game, player, card, targetPlayer)
            }
        }

        private fun processPointsCard(player: Player, card: Card): TurnResult {
            player.score += card.value
            return TurnResult(
                scoreChange = card.value,
                affectedPlayer = null,
                nextPlayerId = null
            )
        }

        private fun processActionCard(
            game: GameSession,
            player: Player,
            card: Card,
            targetPlayer: Player?
        ): TurnResult {
            return when (card.actionType) {
                ActionType.BLOCK -> {
                    targetPlayer?.isBlocked = true
                    TurnResult(
                        scoreChange = 0,
                        affectedPlayer = targetPlayer,
                        nextPlayerId = null,
                        skipNextTurn = true
                    )
                }
                ActionType.STEAL -> {
                    val stolen = minOf(card.value, targetPlayer?.score ?: 0)
                    targetPlayer?.score = (targetPlayer?.score ?: 0) - stolen
                    player.score += stolen
                    TurnResult(
                        scoreChange = stolen,
                        affectedPlayer = targetPlayer,
                        nextPlayerId = null
                    )
                }
                ActionType.DOUBLEDOWN -> {
                    val newScore = minOf(player.score * 2, game.targetScore)
                    val scoreChange = newScore - player.score
                    player.score = newScore
                    TurnResult(
                        scoreChange = scoreChange,
                        affectedPlayer = null,
                        nextPlayerId = null
                    )
                }
                null -> throw GameException("Action card must have an action type")
            }
        }

        private fun updateGameState(game: GameSession, player: Player, result: TurnResult) {
            if (player.score >= game.targetScore) {
                game.status = GameStatus.FINISHED
                game.winnerId = player.id
            }
            game.currentTurnPlayerId = result.nextPlayerId ?: getNextPlayerId(game, player)
            gameSessionRepository.save(game)
        }

        private fun getNextPlayerId(game: GameSession, currentPlayer: Player, skip: Int = 0): Long {
            val players = game.players.filter { it.isActive }.sortedBy { it.turnOrder }
            val currentIndex = players.indexOfFirst { it.id == currentPlayer.id }
            var nextIndex = (currentIndex + 1) % players.size

            // Пропуск хода, если игрок заблокирован
            var skipsLeft = skip
            while (skipsLeft > 0) {
                nextIndex = (nextIndex + 1) % players.size
                skipsLeft--
            }

            // Если следующий игрок был заблокирован, пропускаем его и снимаем блокировку
            while (players[nextIndex].isBlocked) {
                players[nextIndex].isBlocked = false
                nextIndex = (nextIndex + 1) % players.size
            }

            return players[nextIndex].id
        }

        @Transactional(readOnly = true)
        fun getGameHistory(gameId: Long): List<TurnResponse> {
            return turnRepository.findByGameSessionIdOrderByTimestampDesc(gameId)
                .map { turn ->
                    TurnResponse(
                        cardName = turn.card.name,
                        cardType = turn.card.type,
                        scoreChange = turn.scoreChange,
                        affectedPlayer = turn.affectedPlayer?.let { playerMapper.toDto(it) },
                        nextPlayerId = turn.player.id, // Или другая логика
                        timestamp = turn.timestamp
                    )
                }
        }

        private fun saveTurn(
            game: GameSession,
            player: Player,
            card: Card,
            result: TurnResult
        ): Turn {
            return turnRepository.save(Turn(
                gameSession = game,
                player = player,
                card = card,
                affectedPlayer = result.affectedPlayer,
                scoreChange = result.scoreChange,
                timestamp = LocalDateTime.now()
            ))
        }

        // Validation methods
        private fun validateGameJoin(game: GameSession, userId: Long) {
            require(game.status == GameStatus.WAITING_FOR_PLAYERS) { "Game already started" }
            require(game.players.size < 4) { "Game is full" }
            require(game.players.none { it.user.id == userId }) { "User already joined" }
            require(game.players.size < game.maxPlayers) { "Maximum number of players reached" }

        }

        private fun validateGameStart(game: GameSession, userId: Long) {
            require(game.status == GameStatus.WAITING_FOR_PLAYERS) {
                "Game cannot be started: wrong status (${game.status})"
            }
            require(game.players.size >= 2) {
                "Game cannot be started: not enough players (${game.players.size})"
            }
            require(game.players.any { it.user.id == userId }) {
                "Game cannot be started: user $userId is not a player"
            }
        }

        private fun validateTurn(game: GameSession, player: Player) {
            require(game.status == GameStatus.IN_PROGRESS) { "Game is not in progress" }
            require(!player.isBlocked) { "Player is blocked" }
            require(game.currentTurnPlayerId == player.id) { "Not your turn" }
        }

        // Mappers
        private fun toGameSessionResponse(game: GameSession): GameSessionResponse {
            return GameSessionResponse(
                id = game.id,
                status = game.status,
                players = game.players.map { playerMapper.toDto(it) },
                currentTurnPlayerId = game.currentTurnPlayerId,
                winnerId = game.winnerId,
                createdAt = game.createdAt,
                cardsInDeck = game.deck.count()
            )
        }

        private fun toTurnResponse(turn: Turn, nextPlayerId: Long?): TurnResponse {
            return TurnResponse(
                cardName = turn.card.name,
                cardType = turn.card.type,
                scoreChange = turn.scoreChange,
                affectedPlayer = turn.affectedPlayer?.let { playerMapper.toDto(it) },
                nextPlayerId = nextPlayerId ?: getNextPlayerId(turn.gameSession, turn.player),
                timestamp = turn.timestamp
            )
        }



        private data class TurnResult(
            val scoreChange: Int,
            val affectedPlayer: Player?,
            val nextPlayerId: Long?,
            val skipNextTurn: Boolean = false
        )
    }

