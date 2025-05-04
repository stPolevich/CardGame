package com.example.CardGame

import com.example.CardGame.config.AppProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(AppProperties::class)
class CardGameApplication

fun main(args: Array<String>) {
    runApplication<CardGameApplication>(*args)
}
