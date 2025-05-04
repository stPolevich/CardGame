package com.example.CardGame.service.сard

import com.example.CardGame.entity.Card
import com.example.CardGame.entity.GameSession
import com.example.CardGame.entity.Player
import com.example.CardGame.enums.ActionType
import org.springframework.stereotype.Component

@Component
class BlockCardEffect : CardEffect {
    override fun apply(card: Card, gameSession: GameSession, activePlayer: Player, targetPlayer: Player?): CardEffectResult {
        requireNotNull(targetPlayer) { "Block card requires a target player" }
        targetPlayer.isBlocked = true
        return CardEffectResult(
            affectedPlayer = targetPlayer,
            skipNextTurn = true,
            message = "Player ${targetPlayer.user.username} is blocked for next turn"
        )
    }

    override fun validate(card: Card, gameSession: GameSession, activePlayer: Player, targetPlayer: Player?) {
        require(card.actionType == ActionType.BLOCK) { "Invalid action type for BlockCardEffect" }
        requireNotNull(targetPlayer) { "Block card requires a target player" }
        require(targetPlayer.id != activePlayer.id) { "Cannot block yourself" }
    }
}