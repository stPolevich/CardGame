package com.example.CardGame.dto.response

data class DrawCardResponse(
    val cardType: String,
    val cardId: Long?,
    val message: String?,
    val turnResult: TurnResponse? // если ход завершён сразу
)