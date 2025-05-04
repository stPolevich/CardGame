package com.example.CardGame.dto.request

data class ApplyStealRequest(
    val playerId: Long,
    val cardId: Long,
    val targetPlayerId: Long
)