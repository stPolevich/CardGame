package com.example.CardGame.service.сard

import com.example.CardGame.entity.Card
import com.example.CardGame.enums.ActionType
import com.example.CardGame.enums.CardType
import org.springframework.stereotype.Component

@Component
class CardEffectFactory(
    private val pointsCardEffect: PointsCardEffect,
    private val blockCardEffect: BlockCardEffect,
    private val stealCardEffect: StealCardEffect,
    private val doubleDownCardEffect: DoubleDownCardEffect
) {
    fun createEffect(card: Card): CardEffect {
        return when (card.type) {
            CardType.POINTS -> pointsCardEffect
            CardType.ACTION -> when (card.actionType) {
                ActionType.BLOCK -> blockCardEffect
                ActionType.STEAL -> stealCardEffect
                ActionType.DOUBLEDOWN -> doubleDownCardEffect
                null -> throw IllegalStateException("Action card must have an action type")
            }
        }
    }
}