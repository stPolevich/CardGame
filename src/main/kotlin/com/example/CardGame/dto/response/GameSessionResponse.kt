package com.example.CardGame.dto.response

import com.example.CardGame.dto.PlayerDto
import com.example.CardGame.entity.GameSession
import com.example.CardGame.enums.GameStatus
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

data class GameSessionResponse(
 @Schema(description = "ID игровой сессии")
 val id: Long,

 @Schema(description = "Статус игровой сессии")
 val status: GameStatus,

 @Schema(description = "ID игрока, который делает ход")
 val currentTurnPlayerId: Long?,

 @Schema(description = "ID победителя")
 val winnerId: Long?,

 @Schema(description = "Список игроков")
 val players: List<PlayerDto>,

 @Schema(description = "Дата создания игровой сессии")
 val createdAt: LocalDateTime,

 @Schema(description = "Количество карт в колоде")
 val cardsInDeck: Int
) {
 companion object {
  fun fromEntity(game: GameSession, cardsCount: Int): GameSessionResponse {
   return GameSessionResponse(
    id = game.id,
    status = game.status,
    currentTurnPlayerId = game.currentTurnPlayerId,
    winnerId = game.winnerId,
    players = game.players.map { PlayerDto.fromEntity(it) },
    createdAt = game.createdAt,
    cardsInDeck = cardsCount
   )
  }
 }
}