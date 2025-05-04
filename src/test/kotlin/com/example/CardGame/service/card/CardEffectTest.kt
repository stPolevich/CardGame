package com.example.CardGame.service.card

import com.example.CardGame.entity.Card
import com.example.CardGame.entity.GameSession
import com.example.CardGame.entity.Player
import com.example.CardGame.entity.User
import com.example.CardGame.enums.CardType
import com.example.CardGame.enums.GameStatus
import com.example.CardGame.enums.ActionType
import com.example.CardGame.service.сard.CardEffectFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class CardEffectTest {
    @Autowired
    private lateinit var cardEffectFactory: CardEffectFactory

    @Test
    fun `points card should increase player score`() {
        val gameSession = GameSession(
            status = GameStatus.IN_PROGRESS,
            maxPlayers = 4,
            targetScore = 30
        )

        val user = User(
            username = "testUser",
            password = "password",
            name = "Test User"
        )

        val player = Player(
            user = user,
            gameSession = gameSession,
            score = 0,
            isBlocked = false,
            turnOrder = 0
        )

        val card = Card(
            name = "Points",
            type = CardType.POINTS,
            value = 5
        )

        // Выполняем тест
        val effect = cardEffectFactory.createEffect(card)
        val result = effect.apply(card, gameSession, player, null)

        // Проверяем результаты
        assertThat(player.score).isEqualTo(5)
        assertThat(result.scoreChange).isEqualTo(5)
    }

    @Test
    fun `steal card should transfer points between players`() {
        val gameSession = GameSession(
            status = GameStatus.IN_PROGRESS,
            maxPlayers = 4,
            targetScore = 30
        )

        val user1 = User(
            username = "player1",
            password = "password",
            name = "Player 1"
        )

        val user2 = User(
            username = "player2",
            password = "password",
            name = "Player 2"
        )

        val activePlayer = Player(
            user = user1,
            gameSession = gameSession,
            score = 0,
            isBlocked = false,
            turnOrder = 0
        )

        val targetPlayer = Player(
            user = user2,
            gameSession = gameSession,
            score = 10,
            isBlocked = false,
            turnOrder = 1
        )

        val card = Card(
            name = "Steal",
            type = CardType.ACTION,
            value = 5,
            actionType = ActionType.STEAL
        )

        val effect = cardEffectFactory.createEffect(card)
        val result = effect.apply(card, gameSession, activePlayer, targetPlayer)

        assertThat(activePlayer.score).isEqualTo(5)
        assertThat(targetPlayer.score).isEqualTo(5)
        assertThat(result.scoreChange).isEqualTo(5)
        assertThat(result.affectedPlayer).isEqualTo(targetPlayer)
    }

    @Test
    fun `block card should prevent player from making next turn`() {
        val gameSession = GameSession(
            status = GameStatus.IN_PROGRESS,
            maxPlayers = 4,
            targetScore = 30
        )

        val user1 = User(
            username = "player1",
            password = "password",
            name = "Player 1"
        )

        val user2 = User(
            username = "player2",
            password = "password",
            name = "Player 2"
        )

        val activePlayer = Player(
            user = user1,
            gameSession = gameSession,
            score = 0,
            isBlocked = false,
            turnOrder = 0
        )

        val targetPlayer = Player(
            user = user2,
            gameSession = gameSession,
            score = 0,
            isBlocked = false,
            turnOrder = 1
        )

        val card = Card(
            name = "Block",
            type = CardType.ACTION,
            value = 1,
            actionType = ActionType.BLOCK
        )

        val effect = cardEffectFactory.createEffect(card)
        val result = effect.apply(card, gameSession, activePlayer, targetPlayer)

        assertThat(targetPlayer.isBlocked).isTrue()
        assertThat(result.skipNextTurn).isTrue()
        assertThat(result.affectedPlayer).isEqualTo(targetPlayer)
    }

    @Test
    fun `double down card should double player score up to target score`() {
        val gameSession = GameSession(
            status = GameStatus.IN_PROGRESS,
            maxPlayers = 4,
            targetScore = 30
        )

        val user = User(
            username = "player1",
            password = "password",
            name = "Player 1"
        )

        val player = Player(
            user = user,
            gameSession = gameSession,
            score = 12,
            isBlocked = false,
            turnOrder = 0
        )

        val card = Card(
            name = "DoubleDown",
            type = CardType.ACTION,
            value = 2,
            actionType = ActionType.DOUBLEDOWN
        )

        val effect = cardEffectFactory.createEffect(card)
        val result = effect.apply(card, gameSession, player, null)

        assertThat(player.score).isEqualTo(24)
        assertThat(result.scoreChange).isEqualTo(12)
    }
}