package com.example.CardGame.validation

import com.example.CardGame.config.AppProperties
import com.example.CardGame.entity.GameSession
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class GameSessionValidator : ConstraintValidator<ValidGameSession, GameSession> {

    @Autowired
    private lateinit var appProperties: AppProperties

    override fun initialize(constraintAnnotation: ValidGameSession) {
        // Пустая реализация
    }

    override fun isValid(gameSession: GameSession, context: ConstraintValidatorContext): Boolean {
        context.disableDefaultConstraintViolation()

        if (gameSession.players.size > appProperties.validation.game.maxPlayers) {
            context.buildConstraintViolationWithTemplate(
                "Number of players exceeds maximum allowed (${appProperties.validation.game.maxPlayers})"
            ).addConstraintViolation()
            return false
        }

        return true
    }
}