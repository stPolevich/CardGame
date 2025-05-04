package com.example.CardGame.service.сard

import com.example.CardGame.entity.Card
import com.example.CardGame.entity.GameSession
import com.example.CardGame.entity.Player
import com.example.CardGame.enums.ActionType
import org.springframework.stereotype.Component

@Component
class StealCardEffect : CardEffect {
    override fun apply(card: Card, gameSession: GameSession, activePlayer: Player, targetPlayer: Player?): CardEffectResult {
        requireNotNull(targetPlayer) { "Steal card requires a target player" }
        val stolenPoints = minOf(card.value, targetPlayer.score)
        targetPlayer.score -= stolenPoints
        activePlayer.score += stolenPoints
        return CardEffectResult(
            scoreChange = stolenPoints,
            affectedPlayer = targetPlayer,
            message = "Player ${activePlayer.user.username} stole $stolenPoints points from ${targetPlayer.user.username}"
        )
    }

    override fun validate(card: Card, gameSession: GameSession, activePlayer: Player, targetPlayer: Player?) {
        require(card.actionType == ActionType.STEAL) { "Invalid action type for StealCardEffect" }
        requireNotNull(targetPlayer) { "Steal card requires a target player" }
        require(targetPlayer.id != activePlayer.id) { "Cannot steal from yourself" }
    }
}