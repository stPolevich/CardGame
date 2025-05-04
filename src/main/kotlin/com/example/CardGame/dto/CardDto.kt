package com.example.CardGame.dto

import com.example.CardGame.enums.ActionType
import com.example.CardGame.enums.CardType

data class CardDto(
    val id: Long,
    val name: String,
    val type: CardType,
    val value: Int,
    val actionType: ActionType?
)