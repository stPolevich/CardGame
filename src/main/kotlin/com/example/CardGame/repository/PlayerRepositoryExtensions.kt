package com.example.CardGame.repository

import com.example.CardGame.entity.Player
import jakarta.persistence.EntityNotFoundException

fun PlayerRepository.findByIdOrThrow(id: Long): Player {
    return findById(id).orElseThrow {
        EntityNotFoundException("Player not found with id: $id")
    }
}

fun PlayerRepository.findByGameSessionIdAndUserIdOrThrow(gameSessionId: Long, userId: Long): Player {
    return findByGameSessionIdAndUserId(gameSessionId, userId)
        ?: throw EntityNotFoundException("Player not found in game session: $gameSessionId for user: $userId")
}