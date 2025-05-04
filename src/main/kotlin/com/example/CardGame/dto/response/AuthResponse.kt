package com.example.CardGame.dto.response

data class AuthResponse(
    val token: String,
    val userId: Long,
    val username: String
)