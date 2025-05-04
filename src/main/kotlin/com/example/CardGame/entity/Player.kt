package com.example.CardGame.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Column
import jakarta.persistence.GenerationType
import jakarta.persistence.ManyToOne
import jakarta.persistence.JoinColumn
import jakarta.persistence.FetchType
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

@Entity
@Table(name = "players")
data class Player(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long = 0,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id", nullable = false)
        val user: User,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "game_session_id", nullable = false)
        val gameSession: GameSession,

        @field:Min(0)
        @Column(nullable = false)
        var score: Int = 0,

        @Column(nullable = false)
        var isBlocked: Boolean = false,

        @field:Min(0)
        @field:Max(3) // максимальный индекс для 4 игроков
        @Column(name = "turn_order", nullable = false)
        val turnOrder: Int,

        @Column(nullable = false)
        var isActive: Boolean = true
)