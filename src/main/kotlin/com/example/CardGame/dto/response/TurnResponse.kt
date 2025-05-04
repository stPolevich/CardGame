package com.example.CardGame.dto.response

import com.example.CardGame.dto.PlayerDto
import com.example.CardGame.entity.Player
import com.example.CardGame.enums.CardType
import java.time.LocalDateTime

data class TurnResponse(
    val cardName: String,
    val cardType: CardType,
    val scoreChange: Int,
    val affectedPlayer: PlayerDto?,
    val nextPlayerId: Long,
    val timestamp: LocalDateTime
)