package com.example.CardGame.repository

import com.example.CardGame.entity.Card
import jakarta.persistence.EntityNotFoundException

fun CardRepository.findByIdOrThrow(id: Long): Card {
    return findById(id).orElseThrow {
        EntityNotFoundException("Game session not found with id: $id")
    }
}