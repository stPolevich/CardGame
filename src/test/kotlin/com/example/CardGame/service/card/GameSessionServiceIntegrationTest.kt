package com.example.CardGame.service.card

import com.example.CardGame.service.GameSessionService
import com.example.CardGame.dto.request.ApplyStealRequest
import com.example.CardGame.dto.request.DrawCardRequest
import com.example.CardGame.enums.ActionType
import com.example.CardGame.entity.Card
import com.example.CardGame.enums.CardType
import com.example.CardGame.repository.CardRepository
import com.example.CardGame.repository.GameSessionRepository
import com.example.CardGame.repository.PlayerRepository
import com.example.CardGame.repository.UserRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionTemplate

@SpringBootTest
class GameSessionServiceIntegrationTest @Autowired constructor(
    private val gameSessionService: GameSessionService,
    private val gameSessionRepository: GameSessionRepository,
    private val playerRepository: PlayerRepository,
    private val userRepository: UserRepository,
    private val cardRepository: CardRepository,
    private val transactionTemplate: TransactionTemplate
) {

    private val username1 = "testuser1"
    private val username2 = "testuser2"

    @BeforeEach
    @Transactional
    fun setupUsers() {
        // Создаём двух пользователей для тестов (если их нет)
        userRepository.saveIfNotExists(username1, "Test User 1")
        userRepository.saveIfNotExists(username2, "Test User 2")
    }

    @Test
    @Transactional
    fun shouldCreateGameAndJoinPlayers() {
        val user1 = userRepository.findByUsername(username1)!!
        val user2 = userRepository.findByUsername(username2)!!

        // Создаём игру
        val gameResponse = gameSessionService.createGame(user1.id)
        val gameId = gameResponse.id

        // Присоединяем второго игрока
        gameSessionService.joinGame(gameId, user2.id)

        // Получаем игроков через репозиторий, а не через game.players (ленивая загрузка!)
        val players = playerRepository.findByGameSessionId(gameId)

        // Проверяем, что оба игрока присутствуют в партии
        val player1 = players.find { it.user.username == username1 }
        val player2 = players.find { it.user.username == username2 }
        assertNotNull(player1, "Player1 not found in game")
        assertNotNull(player2, "Player2 not found in game")
        assertEquals(2, players.size)
    }

    @Test
    fun shouldApplyStealActionCardAndDecreaseTargetScore() {
        val user1 = userRepository.findByUsername(username1)!!
        val user2 = userRepository.findByUsername(username2)!!

        // Создаём карту Steal заранее
        val stealCard = cardRepository.save(
            Card(
                name = "StealTest",
                type = CardType.ACTION,
                value = 3,
                actionType = ActionType.STEAL
            )
        )

        // Создаём игру и двух игроков
        val gameResponse = gameSessionService.createGame(user1.id)
        val gameId = gameResponse.id
        gameSessionService.joinGame(gameId, user2.id)

        // Получаем игроков через репозиторий
        val players = playerRepository.findByGameSessionId(gameId)
        val player1 = players.find { it.user.username == username1 }
        val player2 = players.find { it.user.username == username2 }
        assertNotNull(player1, "Player1 not found in game")
        assertNotNull(player2, "Player2 not found in game")

        // Принудительно выставляем очки для теста
        player1!!.score = 10
        player2!!.score = 5
        playerRepository.save(player1)
        playerRepository.save(player2)

        // Стартуем игру (deck будет инициализирован, включая StealTest)
        gameSessionService.startGame(gameId, user1.id)

        // Перемещаем StealTest в начало deck и выводим deck в рамках транзакции
        transactionTemplate.execute {
            val game = gameSessionRepository.findById(gameId).get()
            // Удаляем StealTest из текущей позиции, если она уже есть в deck
            game.deck.remove(stealCard.id)
            // Добавляем StealTest в начало deck
            game.deck.add(0, stealCard.id)
            gameSessionRepository.save(game)
            // Выводим deck только здесь!
            println("Deck после перестановки: ${game.deck}")
            println("Ожидаемый id StealTest: ${stealCard.id}, первый id в deck: ${game.deck.firstOrNull()}")
        }

        // Игрок 1 вытягивает карту
        val drawResponse = gameSessionService.drawCard(gameId, DrawCardRequest(player1.id))
        println("drawResponse.cardType = ${drawResponse.cardType}, cardId = ${drawResponse.cardId}")

        // Проверяем, что вытянутая карта — именно StealTest
        assertEquals("STEAL", drawResponse.cardType)
        assertEquals(stealCard.id, drawResponse.cardId)

        // Применяем эффект Steal к игроку 2
        val applyStealRequest = ApplyStealRequest(
            playerId = player1.id,
            cardId = stealCard.id,
            targetPlayerId = player2.id
        )
        gameSessionService.applySteal(gameId, applyStealRequest)

        // Проверяем, что у игрока 2 уменьшились очки, а у игрока 1 увеличились на 3
        val updatedPlayer1 = playerRepository.findById(player1.id).get()
        val updatedPlayer2 = playerRepository.findById(player2.id).get()
        println("updatedPlayer1.score = ${updatedPlayer1.score}, updatedPlayer2.score = ${updatedPlayer2.score}")

        assertEquals(13, updatedPlayer1.score) // Было 10, украли 3
        assertEquals(2, updatedPlayer2.score)  // Было 5, украли 3
    }
}

// Вспомогательная функция для создания пользователя, если не существует
private fun UserRepository.saveIfNotExists(username: String, name: String): com.example.CardGame.entity.User {
    return this.findAll().find { it.username == username }
        ?: this.save(com.example.CardGame.entity.User(username = username, password = "password", name = name))
}
