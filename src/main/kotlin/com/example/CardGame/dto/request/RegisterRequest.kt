package com.example.CardGame.dto.request

import io.swagger.v3.oas.annotations.media.Schema

data class RegisterRequest(
    @Schema(description = "Имя пользователя (логин)", example = "john_doe")
    val username: String,

    @Schema(description = "Пароль", example = "password123")
    val password: String,

    @Schema(description = "Полное имя", example = "John Doe")
    val name: String
)