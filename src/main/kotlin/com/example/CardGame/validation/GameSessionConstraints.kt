package com.example.CardGame.validation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [GameSessionValidator::class])
annotation class ValidGameSession(
    val message: String = "Invalid game session configuration",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)