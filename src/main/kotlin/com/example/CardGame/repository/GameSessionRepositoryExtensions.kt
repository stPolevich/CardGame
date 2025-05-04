package com.example.CardGame.repository

import com.example.CardGame.entity.GameSession
import jakarta.persistence.EntityNotFoundException

fun GameSessionRepository.findByIdOrThrow(id: Long): GameSession {
    return findById(id).orElseThrow { 
        EntityNotFoundException("Game session not found with id: $id") 
    }
}