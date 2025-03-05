package testCore

import game.GameMode
import game.GameSettings
import http.dto.OnlineMessage
import http.dto.RoomSummary

fun makeOnlineMessage(
    colour: String = "red",
    username: String = "Alyssa",
    text: String = "wot dat"
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
    name: String = "Barium I",
    gameSettings: GameSettings = makeGameSettings(),
    capacity: Int = 4,
    players: Int = 2,
    observers: Int = 1,
) = RoomSummary(name, gameSettings, capacity, players, observers)
