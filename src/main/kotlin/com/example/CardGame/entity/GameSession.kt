package com.example.CardGame.entity

import com.example.CardGame.enums.GameStatus
import com.example.CardGame.validation.ValidGameSession
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "game_sessions")
data class GameSession(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long = 0,

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        var status: GameStatus,

        @Column(name = "current_turn_player_id")
        var currentTurnPlayerId: Long? = null,

        @Column(name = "winner_id")
        var winnerId: Long? = null,

        @Column(nullable = false)
        val createdAt: LocalDateTime = LocalDateTime.now(),

        @OneToMany(mappedBy = "gameSession", cascade = [CascadeType.ALL], orphanRemoval = true)
        val players: MutableList<Player> = mutableListOf(),

        @ElementCollection
        @CollectionTable(name = "game_decks", joinColumns = [JoinColumn(name = "game_session_id")])
        @OrderColumn(name = "position")
        @Column(name = "card_id")
        var deck: MutableList<Long> = mutableListOf(),

        @OneToMany(mappedBy = "gameSession", cascade = [CascadeType.ALL], orphanRemoval = true)
        val turns: MutableList<Turn> = mutableListOf(),

        @Column(name = "max_players", nullable = false)
        val maxPlayers: Int = 4,

        @Column(name = "target_score", nullable = false)
        val targetScore: Int = 30
) {
        fun canJoin(): Boolean = players.size < maxPlayers && status == GameStatus.WAITING_FOR_PLAYERS

        fun canStart(): Boolean = players.size >= 2 && status == GameStatus.WAITING_FOR_PLAYERS

        fun isFinished(): Boolean = status == GameStatus.FINISHED
}
