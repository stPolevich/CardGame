package com.example.CardGame.entity

import com.example.CardGame.enums.ActionType
import com.example.CardGame.enums.CardType
import jakarta.persistence.*
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank


@Entity
@Table(name = "cards")
data class Card(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long = 0,

        @Column(nullable = false)
        val name: String,

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        val type: CardType,

        @Column(nullable = false)
        val value: Int,

        @Enumerated(EnumType.STRING)
        val actionType: ActionType? = null
)

