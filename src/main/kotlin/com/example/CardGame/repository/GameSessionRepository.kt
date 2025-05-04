package com.example.CardGame.repository

import com.example.CardGame.entity.GameSession
import org.springframework.data.jpa.repository.JpaRepository

interface GameSessionRepository : JpaRepository<GameSession, Long>