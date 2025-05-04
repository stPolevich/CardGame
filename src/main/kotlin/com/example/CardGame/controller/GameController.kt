package com.example.CardGame.controller

import com.example.CardGame.dto.request.ApplyStealRequest
import com.example.CardGame.dto.request.CreateGameRequest
import com.example.CardGame.dto.request.DrawCardRequest
import com.example.CardGame.dto.request.MakeTurnRequest
import com.example.CardGame.dto.response.DrawCardResponse
import com.example.CardGame.dto.response.GameSessionResponse
import com.example.CardGame.dto.response.TurnResponse
import com.example.CardGame.service.GameSessionService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/games")
@Tag(name = "Game", description = "API для управления игровыми сессиями")
class GameController(
    private val gameSessionService: GameSessionService
) {
    @Operation(
        summary = "Создание новой игры",
        description = "Создает новую игровую сессию и добавляет создателя как первого игрока"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Игра успешно создана",
            content = [Content(schema = Schema(implementation = GameSessionResponse::class))]
        ),
        ApiResponse(responseCode = "400", description = "Некорректные данные запроса"),
        ApiResponse(responseCode = "401", description = "Не авторизован")
    ])
    @PostMapping
    fun createGame(@RequestBody request: CreateGameRequest): GameSessionResponse {
        return gameSessionService.createGame(request.createdByUserId)
    }

    @Operation(
        summary = "Присоединение к игре",
        description = "Позволяет игроку присоединиться к существующей игре"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Успешное присоединение к игре",
            content = [Content(schema = Schema(implementation = GameSessionResponse::class))]
        ),
        ApiResponse(responseCode = "404", description = "Игра не найдена"),
        ApiResponse(responseCode = "400", description = "Игра уже заполнена или начата")
    ])
    @PostMapping("/{gameId}/join")
    fun joinGame(
        @PathVariable gameId: Long,
        @RequestParam userId: Long
    ): GameSessionResponse {
        return gameSessionService.joinGame(gameId, userId)
    }

    @Operation(
        summary = "Начало игры",
        description = "Запускает игру, если набралось достаточное количество игроков"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Игра успешно начата",
            content = [Content(schema = Schema(implementation = GameSessionResponse::class))]
        ),
        ApiResponse(responseCode = "400", description = "Недостаточно игроков или игра уже начата")
    ])
    @PostMapping("/{gameId}/start")
    fun startGame(
        @PathVariable gameId: Long,
        @RequestParam startedByUserId: Long
    ): GameSessionResponse {
        return gameSessionService.startGame(gameId, startedByUserId)
    }

    @Operation(
        summary = "Сделать ход",
        description = "Позволяет игроку сделать ход, если сейчас его очередь"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Ход успешно выполнен",
            content = [Content(schema = Schema(implementation = TurnResponse::class))]
        ),
        ApiResponse(responseCode = "400", description = "Некорректный ход или не ваша очередь"),
        ApiResponse(responseCode = "404", description = "Игра не найдена")
    ])
    @PostMapping("/{gameId}/turn")
    fun drawCard(
        @PathVariable gameId: Long,
        @RequestBody request: DrawCardRequest
    ): DrawCardResponse {
        return gameSessionService.drawCard(gameId, request)
    }


    @Operation(
        summary = "Применить эффект Steal",
        description = "Позволяет игроку, вытянувшему карту Steal, выбрать соперника и применить к нему эффект кражи очков. " +
                "Этот метод вызывается после того, как игрок вытянул карту Steal через /turn, " +
                "и должен передать id цели для кражи очков."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Эффект Steal успешно применён, ход завершён.",
                content = [Content(schema = Schema(implementation = TurnResponse::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Некорректные данные запроса или действие невозможно (например, не ваша очередь, неверная карта или цель)."
            ),
            ApiResponse(
                responseCode = "404",
                description = "Игра, игрок или карта не найдены."
            )
        ]
    )
    @PostMapping("/{gameId}/apply-steal")
    fun applySteal(
        @PathVariable gameId: Long,
        @RequestBody request: ApplyStealRequest
    ): TurnResponse {
        return gameSessionService.applySteal(gameId, request)
    }



    @Operation(
        summary = "Получить информацию об игре",
        description = "Возвращает текущее состояние игровой сессии"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Информация об игре получена",
            content = [Content(schema = Schema(implementation = GameSessionResponse::class))]
        ),
        ApiResponse(responseCode = "404", description = "Игра не найдена")
    ])
    @GetMapping("/{gameId}")
    fun getGame(@PathVariable gameId: Long): GameSessionResponse {
        return gameSessionService.getGameSession(gameId)
    }

    @Operation(
        summary = "Получить историю ходов",
        description = "Возвращает список всех ходов в игре"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "История ходов получена",
            content = [Content(schema = Schema(implementation = Array<TurnResponse>::class))]
        ),
        ApiResponse(responseCode = "404", description = "Игра не найдена")
    ])
    @GetMapping("/{gameId}/history")
    fun getGameHistory(@PathVariable gameId: Long): List<TurnResponse> {
        return gameSessionService.getGameHistory(gameId)
    }
}