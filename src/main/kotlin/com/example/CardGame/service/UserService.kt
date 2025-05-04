package com.example.CardGame.service

import com.example.CardGame.dto.request.RegisterRequest
import com.example.CardGame.dto.response.UserResponse
import com.example.CardGame.entity.User
import com.example.CardGame.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    fun register(request: RegisterRequest): UserResponse {
        if (userRepository.existsByUsername(request.username)) {
            throw IllegalArgumentException("Username already exists")
        }

        val user = userRepository.save(
            User(
                username = request.username,
                password = passwordEncoder.encode(request.password),
                name = request.name
            )
        )

        return UserResponse(
            id = user.id,
            username = user.username,
            name = user.name
        )
    }
}