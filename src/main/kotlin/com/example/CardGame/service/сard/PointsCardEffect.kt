package com.example.CardGame.service.сard

import com.example.CardGame.entity.Card
import com.example.CardGame.entity.GameSession
import com.example.CardGame.entity.Player
import com.example.CardGame.enums.CardType
import org.springframework.stereotype.Component

@Component
class PointsCardEffect : CardEffect {
    override fun apply(card: Card, gameSession: GameSession, activePlayer: Player, targetPlayer: Player?): CardEffectResult {
        activePlayer.score += card.value
        return CardEffectResult(
            scoreChange = card.value,
            message = "Player ${activePlayer.user.username} gained ${card.value} points"
        )
    }

    override fun validate(card: Card, gameSession: GameSession, activePlayer: Player, targetPlayer: Player?) {
        require(card.type == CardType.POINTS) { "Invalid card type for PointsCardEffect" }
    }
}