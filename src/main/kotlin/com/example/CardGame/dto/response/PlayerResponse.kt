package com.example.CardGame.dto.response

data class PlayerResponse(
    val id: Long,
    val userId: Long,
    val name: String,
    val score: Int,
    val isBlocked: Boolean
)