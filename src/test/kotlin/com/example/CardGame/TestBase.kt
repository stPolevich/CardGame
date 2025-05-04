package com.example.CardGame

import com.example.CardGame.entity.GameSession
import com.example.CardGame.entity.Player
import com.example.CardGame.entity.User
import com.example.CardGame.enums.GameStatus

abstract class TestBase {
    protected fun createTestGameSession() = GameSession(
        status = GameStatus.IN_PROGRESS,
        maxPlayers = 4,
        targetScore = 30
    )

    protected fun createTestUser(
        username: String = "testUser",
        password: String = "password",
        name: String = "Test User"
    ) = User(
        username = username,
        password = password,
        name = name
    )

    protected fun createTestPlayer(
        user: User,
        gameSession: GameSession,
        score: Int = 0,
        turnOrder: Int = 0
    ) = Player(
        user = user,
        gameSession = gameSession,
        score = score,
        isBlocked = false,
        turnOrder = turnOrder
    )
}