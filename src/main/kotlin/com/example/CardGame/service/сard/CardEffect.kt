package com.example.CardGame.service.сard

import com.example.CardGame.entity.Card
import com.example.CardGame.entity.GameSession
import com.example.CardGame.entity.Player

interface CardEffect {
    fun apply(card: Card, gameSession: GameSession, activePlayer: Player, targetPlayer: Player?): CardEffectResult
    fun validate(card: Card, gameSession: GameSession, activePlayer: Player, targetPlayer: Player?)
}

data class CardEffectResult(
    val scoreChange: Int = 0,
    val affectedPlayer: Player? = null,
    val skipNextTurn: Boolean = false,
    val message: String
)