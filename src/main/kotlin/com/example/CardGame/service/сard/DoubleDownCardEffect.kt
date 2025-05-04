package com.example.CardGame.service.сard

import com.example.CardGame.entity.Card
import com.example.CardGame.entity.GameSession
import com.example.CardGame.entity.Player
import com.example.CardGame.enums.ActionType
import org.springframework.stereotype.Component

@Component
class DoubleDownCardEffect : CardEffect {
    override fun apply(card: Card, gameSession: GameSession, activePlayer: Player, targetPlayer: Player?): CardEffectResult {
        val originalScore = activePlayer.score
        val newScore = minOf(originalScore * 2, gameSession.targetScore)
        val scoreIncrease = newScore - originalScore
        activePlayer.score = newScore
        return CardEffectResult(
            scoreChange = scoreIncrease,
            message = "Player ${activePlayer.user.username} doubled their score to $newScore"
        )
    }

    override fun validate(card: Card, gameSession: GameSession, activePlayer: Player, targetPlayer: Player?) {
        require(card.actionType == ActionType.DOUBLEDOWN) { "Invalid action type for DoubleDownCardEffect" }
    }
}