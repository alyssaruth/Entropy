package testCore

import game.GameMode
import game.GameSettings
import http.dto.JoinRoomResponse
import http.dto.OnlineMessage
import http.dto.RoomStateResponse
import http.dto.RoomSummary
import java.util.UUID

fun makeOnlineMessage(
    colour: String = "red",
    username: String = "Alyssa",
    text: String = "wot dat",
) = OnlineMessage(colour, username, text)

fun makeGameSettings(
    mode: GameMode = GameMode.Entropy,
    jokerQuantity: Int = 0,
    jokerValue: Int = 0,
    includeMoons: Boolean = false,
    includeStars: Boolean = false,
    negativeJacks: Boolean = false,
    cardReveal: Boolean = false,
    illegalAllowed: Boolean = true,
) =
    GameSettings(
        mode,
        jokerQuantity,
        jokerValue,
        includeMoons,
        includeStars,
        negativeJacks,
        cardReveal,
        illegalAllowed,
    )

fun makeRoomSummary(
    id: UUID = UUID.randomUUID(),
    name: String = "Barium I",
    gameSettings: GameSettings = makeGameSettings(),
    capacity: Int = 4,
    players: Int = 2,
    observers: Int = 1,
) = RoomSummary(id, name, gameSettings, capacity, players, observers)

fun makeJoinRoomResponse(
    chatHistory: List<OnlineMessage> = listOf(makeOnlineMessage()),
    players: Map<Int, String> = mapOf(1 to "Alyssa"),
    formerPlayers: Map<Int, String> = emptyMap(),
) = JoinRoomResponse(chatHistory, players, formerPlayers)

fun makeRoomStateResponse(
    players: Map<Int, String> = mapOf(1 to "Alyssa"),
    formerPlayers: Map<Int, String> = emptyMap(),
) = RoomStateResponse(players, formerPlayers)
