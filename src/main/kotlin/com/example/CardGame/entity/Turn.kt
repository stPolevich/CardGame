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
import java.time.LocalDateTime

@Entity
@Table(name = "turns")
data class Turn(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_session_id")
    val gameSession: GameSession,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id")
    val player: Player,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id")
    val card: Card,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "affected_player_id")
    val affectedPlayer: Player?,

    @Column(nullable = false)
    val scoreChange: Int,

    @Column(nullable = false)
    val timestamp: LocalDateTime = LocalDateTime.now()
)