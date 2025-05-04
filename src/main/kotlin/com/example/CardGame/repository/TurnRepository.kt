package com.example.CardGame.repository

import com.example.CardGame.entity.Turn
import org.springframework.data.jpa.repository.JpaRepository

interface TurnRepository : JpaRepository<Turn, Long> {
    fun findByGameSessionIdOrderByTimestampDesc(gameSessionId: Long): List<Turn>
}