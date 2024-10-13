package util

import auth.Session
import game.GameMode
import game.GameSettings
import java.util.*

fun makeSession(
    id: UUID = UUID.randomUUID(),
    name: String = "Alyssa",
    ip: String = "1.2.3.4",
    apiVersion: Int = OnlineConstants.API_VERSION
) = Session(id, name, ip, apiVersion)

fun makeGameSettings(
    mode: GameMode = GameMode.Entropy,
    jokerQuantity: Int = 0,
    jokerValue: Int = 0,
    includeMoons: Boolean = false,
    includeStars: Boolean = false,
    negativeJacks: Boolean = false,
    cardReveal: Boolean = false,
    illegalAllowed: Boolean = true
) =
    GameSettings(
        mode,
        jokerQuantity,
        jokerValue,
        includeMoons,
        includeStars,
        negativeJacks,
        cardReveal,
        illegalAllowed
    )
