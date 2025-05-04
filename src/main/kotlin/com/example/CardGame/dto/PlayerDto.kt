package com.example.CardGame.dto

import com.example.CardGame.entity.Player

data class PlayerDto(
    val id: Long,
    val userId: Long,
    val name: String,
    val score: Int,
    val isBlocked: Boolean,
    val gameSessionId: Long,
    val cardsInHand: List<CardDto> = emptyList()
) {
    companion object {
        fun fromEntity(player: Player): PlayerDto {
            return PlayerDto(
                id = player.id,
                userId = player.user.id,
                name = player.user.name,
                score = player.score,
                isBlocked = player.isBlocked,
                gameSessionId = player.gameSession.id
            )
        }
    }
}