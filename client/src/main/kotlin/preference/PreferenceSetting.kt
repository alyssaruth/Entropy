package preference

import settings.Setting
import util.ClientGlobals.preferenceStore
import util.CpuStrategies.STRATEGY_BASIC

const val TWO_COLOURS = "twocolour"
const val FOUR_COLOURS = "fourcolour"
const val DECK_DESIGN_CLASSIC = "classic"
const val DECK_DESIGN_ALTERNATE = "alternate"
const val JOKER_DESIGN_CLASSIC = "classic"
const val JOKER_DESIGN_DEVELOPERS = "developers"

const val GAME_SPEED_SLOW = 1500
const val GAME_SPEED_MEDIUM = 1000
const val GAME_SPEED_FAST = 500

fun <T : Any> getPreference(setting: Setting<T>) = preferenceStore.get(setting)

object PreferenceSetting {
    // Gameplay
    @JvmField val GameMode = Setting("gameMode", game.GameMode.Entropy.name)
    @JvmField val PlayWithHandicap = Setting("playWithHandicap", false)
    @JvmField val HandicapAmount = Setting("handicapAmount", 1)
    @JvmField val PlayBlind = Setting("playBlind", false)
    @JvmField val StartingCards = Setting("startingCards", 5)
    @JvmField val JokerQuantity = Setting("jokerQuantity", 2)
    @JvmField val JokerValue = Setting("jokerValue", 2)
    @JvmField val NegativeJacks = Setting("negativeJacks", false)
    @JvmField val IncludeMoons = Setting("includeMoons", false)
    @JvmField val IncludeStars = Setting("includeStars", false)
    @JvmField val CardReveal = Setting("cardReveal", false)

    // Players
    @JvmField val PlayerName = Setting("playerName", "Player")
    @JvmField val OpponentOneName = Setting("opponentOneName", "Mark")
    @JvmField val OpponentTwoName = Setting("opponentTwoName", "David")
    @JvmField val OpponentThreeName = Setting("opponentThreeName", "Tom")
    @JvmField val OpponentOneStrategy = Setting("opponentOneStrategy", STRATEGY_BASIC)
    @JvmField val OpponentTwoStrategy = Setting("opponentTwoStrategy", STRATEGY_BASIC)
    @JvmField val OpponentThreeStrategy = Setting("opponentThreeStrategy", STRATEGY_BASIC)
    @JvmField val OpponentTwoEnabled = Setting("opponentTwoEnabled", false)
    @JvmField val OpponentThreeEnabled = Setting("opponentThreeEnabled", false)
    @JvmField val ApiStrategies = Setting("apiStrategies", "")

    // Appearance
    @JvmField val DeckDesign = Setting("deckDesign", DECK_DESIGN_CLASSIC)
    @JvmField val JokerDesign = Setting("jokerDesign", JOKER_DESIGN_CLASSIC)
    @JvmField val NumberOfColours = Setting("numberOfColours", TWO_COLOURS)
    @JvmField val CardBacks = Setting("cardBacks", "backBlue")
    @JvmField val LookAndFeel = Setting("lookAndFeel", "Metal")

    // Misc
    @JvmField val SaveReplays = Setting("saveReplays", false)
    @JvmField val ReplayDirectory = Setting("replayDirectory", System.getProperty("user.dir"))
    @JvmField val OpenReplayOnFirstRound = Setting("openReplayOnFirstRound", false)
    @JvmField val GameSpeed = Setting("gameSpeed", GAME_SPEED_MEDIUM)
    @JvmField val AutoSave = Setting("autoSave", false)
    @JvmField val AutoStartNextRound = Setting("autoStartNextRound", false)
    @JvmField val AutoStartSeconds = Setting("autoStartSeconds", 2)
    @JvmField val PopUpRooms = Setting("popUpRooms", true)
    @JvmField val CheckForUpdates = Setting("checkForUpdates", true)

    // Replay table preferences
    @JvmField val IncludeGameModeColumn = Setting("includeGameModeColumn", true)
    @JvmField val IncludeRoundsColumn = Setting("includeRoundsColumn", false)
    @JvmField val IncludePlayersColumn = Setting("includePlayersColumn", true)
    @JvmField val IncludeCardsColumn = Setting("includeCardsColumn", false)
    @JvmField val IncludeRoomNameColumn = Setting("includeRoomNameColumn", false)

    // Invisible preferences
    @JvmField val ReplayViewerHeight = Setting("rvheight", 475)
    @JvmField val ReplayViewerWidth = Setting("rvwidth", 875)
}
