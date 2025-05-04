package com.example.CardGame.controller

import com.example.CardGame.dto.request.LoginRequest
import com.example.CardGame.dto.request.RegisterRequest
import com.example.CardGame.dto.response.AuthResponse
import com.example.CardGame.service.AuthService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "API для аутентификации и регистрации")
class AuthController(private val authService: AuthService) {

    @Operation(
        summary = "Регистрация нового пользователя",
        description = "Создает нового пользователя и возвращает JWT токен"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Успешная регистрация",
        content = [Content(schema = Schema(implementation = AuthResponse::class))]
    )
    @PostMapping("/register")
    fun register(@RequestBody request: RegisterRequest): ResponseEntity<AuthResponse> {
        return ResponseEntity.ok(authService.register(request))
    }

    @Operation(
        summary = "Вход в систему",
        description = "Аутентифицирует пользователя и возвращает JWT токен"
    )
    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<AuthResponse> {
        return ResponseEntity.ok(authService.login(request))
    }
}