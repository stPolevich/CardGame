package com.example.CardGame.mapper

import com.example.CardGame.dto.PlayerDto
import com.example.CardGame.entity.Player
import org.springframework.stereotype.Component

@Component
class PlayerMapper {
    fun toDto(player: Player): PlayerDto {
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