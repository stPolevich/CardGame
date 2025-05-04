package com.example.CardGame.dto.request

data class MakeTurnRequest(
    val playerId: Long,
    val targetPlayerId: Long? = null
)