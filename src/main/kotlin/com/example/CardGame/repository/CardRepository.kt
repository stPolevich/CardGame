package com.example.CardGame.repository

import com.example.CardGame.entity.Card
import com.example.CardGame.enums.CardType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface CardRepository : JpaRepository<Card, Long> {

}