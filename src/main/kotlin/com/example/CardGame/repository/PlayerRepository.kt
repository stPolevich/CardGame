package com.example.CardGame.repository

import com.example.CardGame.entity.Player
import org.springframework.data.jpa.repository.JpaRepository

interface PlayerRepository : JpaRepository<Player, Long> {
    fun findByGameSessionIdAndUserId(gameSessionId: Long, userId: Long): Player?
    fun findByGameSessionId(gameSessionId: Long): List<Player>
    fun countByGameSessionId(gameSessionId: Long): Long
}