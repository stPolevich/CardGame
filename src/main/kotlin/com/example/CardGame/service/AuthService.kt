package com.example.CardGame.service

import com.example.CardGame.dto.request.LoginRequest
import com.example.CardGame.dto.request.RegisterRequest
import com.example.CardGame.dto.response.AuthResponse
import com.example.CardGame.entity.User
import com.example.CardGame.repository.UserRepository
import com.example.CardGame.security.JwtTokenProvider
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider,
    private val authenticationManager: AuthenticationManager
) {

    @Transactional
    fun register(request: RegisterRequest): AuthResponse {
        require(!userRepository.existsByUsername(request.username)) {
            "Username already exists"
        }

        val user = userRepository.save(
            User(
                username = request.username,
                password = passwordEncoder.encode(request.password),
                name = request.name
            )
        )

        val token = jwtTokenProvider.generateToken(user.username)
        return AuthResponse(token, user.id, user.username)
    }

    fun login(request: LoginRequest): AuthResponse {
        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(request.username, request.password)
        )

        val user = userRepository.findByUsername(request.username)
            ?: throw IllegalArgumentException("User not found")

        val token = jwtTokenProvider.generateToken(authentication.name)
        return AuthResponse(token, user.id, user.username)
    }
}