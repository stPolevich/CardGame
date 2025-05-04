package com.example.CardGame.repository

import com.example.CardGame.entity.User
import jakarta.persistence.EntityNotFoundException

fun UserRepository.findByIdOrThrow(id: Long): User {
    return findById(id).orElseThrow {
        EntityNotFoundException("User not found with id: $id")
    }
}

fun UserRepository.findByUsernameOrThrow(username: String): User {
    return findByUsername(username)
        ?: throw EntityNotFoundException("User not found with username: $username")
}