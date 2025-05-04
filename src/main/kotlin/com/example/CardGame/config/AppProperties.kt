package com.example.CardGame.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app")
class AppProperties {
    var validation: ValidationProperties = ValidationProperties()
}

class ValidationProperties {
    var game: GameValidationProperties = GameValidationProperties()
    var card: CardValidationProperties = CardValidationProperties()
}

class GameValidationProperties {
    var maxPlayers: Int = 4
    var targetScore: Int = 30
}

class CardValidationProperties {
    var minValue: Int = 0
    var maxValue: Int = 10
}