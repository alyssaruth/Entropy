package util

import auth.Session
import auth.UserConnection
import game.GameMode
import game.GameSettings
import java.util.*
import room.Room

fun makeSession(
    id: UUID = UUID.randomUUID(),
    name: String = "Alyssa",
    ip: String = "1.2.3.4",
    achievementCount: Int = 4,
    apiVersion: Int = OnlineConstants.API_VERSION,
) = Session(id, name, ip, achievementCount, apiVersion)

fun makeUserConnection(session: Session) = UserConnection(session.name)

fun makeRoom(
    id: UUID = UUID.randomUUID(),
    baseName: String = "Sodium",
    settings: GameSettings = GameSettings(GameMode.Entropy),
    capacity: Int = 2,
    index: Int = 1,
) = Room(id, baseName, settings, capacity, index)
