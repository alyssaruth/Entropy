
package util;

import java.util.prefs.Preferences;


public interface Registry extends CoreRegistry
{
	public static final String NODE_ONLINE_REPLAY = "entropyReplayOnline";
	
	//Actual preference wrappers
	public static final Preferences achievements = Preferences.userRoot().node("entropyAchievementsNone");
	public static final Preferences savedGame = Preferences.userRoot().node("entropySavedgameNone");
	public static final Preferences prefs = Preferences.userRoot().node("entropyPreferencesTuuug");
	public static final Preferences rewards = Preferences.userRoot().node("entropyRewardsNone");
	public static final Preferences inGameReplay = Preferences.userRoot().node("entropyReplayCurrent");
	public static final Preferences fileReplay = Preferences.userRoot().node("entropyReplayFile");
	public static final Preferences tempReplayStore = Preferences.userRoot().node("entropyTemp");
	
	//prefs
	public static final String PREFERENCES_STRING_REPLAY_DIRECTORY = "replayDirectory";
	public static final String PREFERENCES_STRING_DECK_DIRECTORY = "deckDirectory";
	public static final String PREFERENCES_STRING_JOKER_DIRECTORY = "jokerDirectory";
	public static final String PREFERENCES_STRING_NUMBER_OF_COLOURS = "numberOfColours";
	public static final String PREFERENCES_STRING_CARD_BACKS = "cardBacks";
	public static final String PREFERENCES_STRING_LOOK_AND_FEEL = "lookAndFeel";
	public static final String PREFERENCES_STRING_OPPONENT_THREE_STRATEGY = "opponentThreeStrategy";
	public static final String PREFERENCES_STRING_OPPONENT_TWO_STRATEGY = "opponentTwoStrategy";
	public static final String PREFERENCES_STRING_OPPONENT_ONE_STRATEGY = "opponentOneStrategy";
	public static final String PREFERENCES_STRING_OPPONENT_THREE_NAME = "opponentThreeName";
	public static final String PREFERENCES_STRING_OPPONENT_TWO_NAME = "opponentTwoName";
	public static final String PREFERENCES_STRING_OPPONENT_ONE_NAME = "opponentOneName";
	public static final String PREFERENCES_STRING_PLAYER_NAME = "playerName";
	
	public static final String PREFERENCES_BOOLEAN_SAVE_REPLAYS = "saveReplays";
	public static final String PREFERENCES_BOOLEAN_AUTOSAVE = "autosave";
	public static final String PREFERENCES_BOOLEAN_AUTO_START_NEXT_ROUND = "autoStart";
	public static final String PREFERENCES_BOOLEAN_POP_UP_ROOMS = "popUp";
	public static final String PREFERENCES_BOOLEAN_OPPONENT_THREE_ENABLED = "opponentThreeEnabled";
	public static final String PREFERENCES_BOOLEAN_OPPONENT_TWO_ENABLED = "opponentTwoEnabled";
	public static final String PREFERENCES_BOOLEAN_PLAY_BLIND = "playBlind";
	public static final String PREFERENCES_BOOLEAN_PLAY_WITH_HANDICAP = "playWithHandicap";
	public static final String PREFERENCES_BOOLEAN_INCLUDE_JOKERS = "includeJokers";
	public static final String PREFERENCES_BOOLEAN_INCLUDE_GAME_MODE_COLUMN = "includeMode";
	public static final String PREFERENCES_BOOLEAN_INCLUDE_ROUNDS_COLUMN = "includeRounds";
	public static final String PREFERENCES_BOOLEAN_INCLUDE_PLAYERS_COLUMN = "includePlayers";
	public static final String PREFERENCES_BOOLEAN_INCLUDE_CARDS_COLUMN = "includeCards";
	public static final String PREFERENCES_BOOLEAN_INCLUDE_ROOM_NAME_COLUMN = "includeRoomName";
	public static final String PREFERENCES_BOOLEAN_INCLUDE_STARS = "includeStars";
	public static final String PREFERENCES_BOOLEAN_INCLUDE_MOONS = "includeMoons";
	public static final String PREFERENCES_BOOLEAN_NEGATIVE_JACKS = "negativeJacks";
	public static final String PREFERENCES_BOOLEAN_CARD_REVEAL = "cardReveal";
	public static final String PREFERENCES_BOOLEAN_CHECK_FOR_UPDATES = "checkForUpdates";
	
	public static final String PREFERENCES_INT_REPLAY_DEFAULT = "replayDefault";
	public static final String PREFERENCES_INT_GAME_SPEED = "gameSpeed";
	public static final String PREFERENCES_INT_AUTO_START_SECONDS = "autoStartMillis";
	public static final String PREFERENCES_INT_HANDICAP_AMOUNT = "handicapAmount";
	public static final String PREFERENCES_INT_JOKER_VALUE = "jokerValue";
	public static final String PREFERENCES_INT_JOKER_QUANTITY = "jokerQuantity";
	public static final String PREFERENCES_INT_NUMBER_OF_CARDS = "numberOfCards";
	public static final String PREFERENCES_INT_REPLAY_VIEWER_HEIGHT = "rvheight";
	public static final String PREFERENCES_INT_REPLAY_VIEWER_WIDTH = "rvwidth";
	public static final String PREFERENCES_STRING_GAME_MODE = "gameMode";
	
	public static final String PREFERENCES_XML_API_SETTINGS = "apiSettings";
	public static final String PREFERENCES_TAG_API = "Api";
	public static final String PREFERENCES_ATTR_API_NAME = "ApiName";
	public static final String PREFERENCES_ATTR_PORT_NUMNER = "PortNumber";
	public static final String PREFERENCES_ATTR_MESSAGE_TYPE = "MessageType";
	public static final String PREFERENCES_ATTR_SUPPORTS_ENTROPY = "Entropy";
	public static final String PREFERENCES_ATTR_SUPPORTS_VECTROPY = "Vectropy";
	public static final String PREFERENCES_ATTR_ERROR = "Error";
	
	//replay
	public static final String REPLAY_STRING_OPPONENT_THREE_HAND = "opponentThreeHand";
	public static final String REPLAY_STRING_OPPONENT_TWO_HAND = "opponentTwoHand";
	public static final String REPLAY_STRING_OPPONENT_ONE_HAND = "opponentOneHand";
	public static final String REPLAY_STRING_PLAYER_HAND = "playerHand";
	public static final String REPLAY_STRING_OPPONENT_THREE_NAME = "opponentThreeName";
	public static final String REPLAY_STRING_OPPONENT_TWO_NAME = "opponentTwoName";
	public static final String REPLAY_STRING_OPPONENT_ONE_NAME = "opponentOneName";
	public static final String REPLAY_STRING_PLAYER_NAME = "playerName";
	public static final String REPLAY_STRING_LISTMODEL = "listmodel";
	public static final String REPLAY_STRING_PLAYER_COLOUR = "playerColour";
	public static final String REPLAY_STRING_OPPONENT_ONE_COLOUR = "opponentOneColour";
	public static final String REPLAY_STRING_OPPONENT_TWO_COLOUR = "opponentTwoColour";
	public static final String REPLAY_STRING_OPPONENT_THREE_COLOUR = "opponentThreeColour";
	public static final String REPLAY_STRING_CHAT_CONTENT = "chatContent";
	public static final String REPLAY_STRING_CHAT_COLOUR = "chatColour";
	public static final String REPLAY_STRING_CHAT_USERNAME = "chatUsername";
	public static final String REPLAY_STRING_OPPONENT_ONE_STRATEGY = "opponentOneStrategy";
	public static final String REPLAY_STRING_OPPONENT_TWO_STRATEGY = "opponentTwoStrategy";
	public static final String REPLAY_STRING_OPPONENT_THREE_STRATEGY = "opponentThreeStrategy";
	public static final String REPLAY_STRING_ROOM_NAME = "roomName";
	
	public static final String REPLAY_BOOLEAN_HAS_ACTED_BLIND_THIS_GAME = "hasActedBlindThisGame";
	public static final String REPLAY_BOOLEAN_HAS_VIEWED_HAND_THIS_GAME = "hasViewedHandThisGame";
	public static final String REPLAY_BOOLEAN_PLAY_WITH_HANDICAP = "playWithHandicap";
	public static final String REPLAY_BOOLEAN_PLAY_BLIND = "playBlind";
	public static final String REPLAY_BOOLEAN_OPPONENT_THREE_ENABLED = "opponentThreeEnabled";
	public static final String REPLAY_BOOLEAN_OPPONENT_TWO_ENABLED = "opponentTwoEnabled";
	public static final String REPLAY_BOOLEAN_OPPONENT_ONE_ENABLED = "opponentOneEnabled";
	public static final String REPLAY_BOOLEAN_PLAYER_ENABLED = "playerEnabled";
	public static final String REPLAY_BOOLEAN_INCLUDE_MOONS = "includeMoons";
	public static final String REPLAY_BOOLEAN_INCLUDE_STARS = "includeStars";
	public static final String REPLAY_BOOLEAN_CHEAT_USED = "cheatUsed";
	
	public static final String REPLAY_INT_JOKER_VALUE = "jokerValue";
	public static final String REPLAY_INT_PERSON_TO_START = "PERSON_TO_START";
	public static final String REPLAY_INT_LAST_BID_SUIT_CODE = "LAST_BID_SUIT_CODE";
	public static final String REPLAY_INT_HANDICAP_AMOUNT = "handicapAmount";
	public static final String REPLAY_INT_OPPONENT_THREE_NUMBER_OF_CARDS = "opponentThreeNumberOfCards";
	public static final String REPLAY_INT_OPPONENT_TWO_NUMBER_OF_CARDS = "opponentTwoNumberOfCards";
	public static final String REPLAY_INT_OPPONENT_ONE_NUMBER_OF_CARDS = "opponentOneNumberOfCards";
	public static final String REPLAY_INT_PLAYER_NUMBER_OF_CARDS = "playerNumberOfCards";
	public static final String REPLAY_INT_HISTORY_SIZE = "historySize";
	public static final String REPLAY_INT_ROUNDS_SO_FAR = "roundsSoFar";
	public static final String REPLAY_INT_GAME_COMPLETE = "gameComplete";
	public static final String REPLAY_INT_PLAYER_WON = "playerWon";
	public static final String REPLAY_INT_GAME_MODE = "gameMode";
	
	//Achievement variables, counters etc
	public static final String ACHIEVEMENTS_STRING_SOCIAL_LIST = "socialList";
	public static final String ACHIEVEMENTS_INT_OPPONENT_TWO_COEFF = "opponentTwoWasEnabled";
	public static final String ACHIEVEMENTS_INT_OPPONENT_THREE_COEFF = "opponentThreeWasEnabled";
	
	//rewards
	public static final String REWARDS_BOOLEAN_FOUR_COLOURS = "fourColours";
	//public static final String REWARDS_BOOLEAN_JOKERS = "jokers";
	public static final String REWARDS_BOOLEAN_NEGATIVE_JACKS = "negativeJacks";
	public static final String REWARDS_BOOLEAN_MINIMALIST_DECK = "minimalist";
	public static final String REWARDS_BOOLEAN_BLIND = "blind";
	public static final String REWARDS_BOOLEAN_VECTROPY = "vectropy";
	//public static final String REWARDS_BOOLEAN_HANDICAP = "handicap";
	public static final String REWARDS_BOOLEAN_CARD_REVEAL = "cardReveal";
	public static final String REWARDS_BOOLEAN_DEVELOPER_JOKERS = "developerSet";
	public static final String REWARDS_BOOLEAN_ILLEGAL = "illegal";
	public static final String REWARDS_BOOLEAN_EXTRA_SUITS = "extraSuits";
	public static final String REWARDS_BOOLEAN_CHEATS = "cheats";
	
	//statistics
	public static final String STATISTICS_INT_ENTROPY_GAMES_PLAYED = "gamesPlayed";
	public static final String STATISTICS_INT_ENTROPY_ONLINE_GAMES_PLAYED = "onlineGamesPlayed";
	public static final String STATISTICS_INT_VECTROPY_GAMES_PLAYED = "vGamesPlayed";
	public static final String STATISTICS_INT_VECTROPY_ONLINE_GAMES_PLAYED = "vOnlineGamesPlayed";
	public static final String STATISTICS_INT_ENTROPY_GAMES_WON = "gamesWon";
	public static final String STATISTICS_INT_ENTROPY_ONLINE_GAMES_WON = "onlineGamesWon";
	public static final String STATISTICS_INT_VECTROPY_GAMES_WON = "vGamesWon";
	public static final String STATISTICS_INT_VECTROPY_ONLINE_GAMES_WON = "vOnlineGamesWon";
	public static final String STATISTICS_INT_BEST_STREAK = "bestStreak";
	public static final String STATISTICS_INT_WORST_STREAK = "worstStreak";
	public static final String STATISTICS_INT_CURRENT_STREAK = "currentStreak";
	
	//savedGame
	public static final String SAVED_GAME_STRING_RESULT_TEXT = "resultText";
	public static final String SAVED_GAME_STRING_TOTAL_CARDS_LABEL = "totalCardsLabel";
	public static final String SAVED_GAME_STRING_OPPONENT_THREE_STRATEGY = "opponentThreeStrategy";
	public static final String SAVED_GAME_STRING_OPPONENT_TWO_STRATEGY = "opponentTwoStrategy";
	public static final String SAVED_GAME_STRING_OPPONENT_ONE_STRATEGY = "opponentOneStrategy";
	public static final String SAVED_GAME_STRING_OPPONENT_THREE_HAND = "opponentThreeHand";
	public static final String SAVED_GAME_STRING_OPPONENT_TWO_HAND = "opponentTwoHand";
	public static final String SAVED_GAME_STRING_OPPONENT_ONE_HAND = "opponentOneHand";
	public static final String SAVED_GAME_STRING_PLAYER_HAND = "playerHand";
	public static final String SAVED_GAME_STRING_OPPONENT_THREE_NAME = "opponentThreeName";
	public static final String SAVED_GAME_STRING_OPPONENT_TWO_NAME = "opponentTwoName";
	public static final String SAVED_GAME_STRING_OPPONENT_ONE_NAME = "opponentOneName";
	public static final String SAVED_GAME_STRING_PLAYER_NAME = "playerName";
	public static final String SAVED_GAME_STRING_LISTMODEL = "listmodel";
	public static final String SAVED_GAME_STRING_LAST_BID = "lastbid";
	public static final String SAVED_GAME_STRING_PLAYER_REVEALED_CARD = "playerRevealedCard";
	public static final String SAVED_GAME_STRING_OPPONENT_ONE_REVEALED_CARD = "opponentOneRevealedCard";
	public static final String SAVED_GAME_STRING_OPPONENT_TWO_REVEALED_CARD = "opponentTwoRevealedCard";
	public static final String SAVED_GAME_STRING_OPPONENT_THREE_REVEALED_CARD = "opponentThreeRevealedCard";
	
	public static final String SAVED_GAME_BOOLEAN_EXITED_ON_CHALLENGE = "exitedOnChallenge";
	public static final String SAVED_GAME_BOOLEAN_FIRST_ROUND = "firstRound";
	public static final String SAVED_GAME_BOOLEAN_INCLUDE_JOKERS = "includeJokers";
	public static final String SAVED_GAME_BOOLEAN_CHALLENGE_ENABLED = "challengeEnabled";
	public static final String SAVED_GAME_BOOLEAN_ILLEGAL_ENABLED = "illegalEnabled";
	public static final String SAVED_GAME_BOOLEAN_INCLUDE_STARS = "includeStars";
	public static final String SAVED_GAME_BOOLEAN_INCLUDE_MOONS = "includeMoons";
	public static final String SAVED_GAME_BOOLEAN_CHEAT_USED = "cheatUsed";
	public static final String SAVED_GAME_BOOLEAN_VIEW_CARDS_VISIBLE = "viewCardsVisible";
	public static final String SAVED_GAME_BOOLEAN_HAS_ACTED_BLIND_THIS_GAME = "hasActedBlindThisGame";
	public static final String SAVED_GAME_BOOLEAN_HAS_VIEWED_HAND_THIS_GAME = "hasViewedHandThisGame";
	public static final String SAVED_GAME_BOOLEAN_PLAY_WITH_HANDICAP = "playWithHandicap";
	public static final String SAVED_GAME_BOOLEAN_PLAY_BLIND = "playBlind";
	public static final String SAVED_GAME_BOOLEAN_OPPONENT_THREE_ENABLED = "opponentThreeEnabled";
	public static final String SAVED_GAME_BOOLEAN_OPPONENT_TWO_ENABLED = "opponentTwoEnabled";
	public static final String SAVED_GAME_BOOLEAN_OPPONENT_ONE_ENABLED = "opponentOneEnabled";
	public static final String SAVED_GAME_BOOLEAN_PLAYER_ENABLED = "playerEnabled";
	public static final String SAVED_GAME_BOOLEAN_IS_GAME_TO_CONTINUE = "isGameToContinue";
	public static final String SAVED_GAME_BOOLEAN_HAS_OVERBID = "hasOverbid";
	public static final String SAVED_GAME_BOOLEAN_CAVEMAN = "caveman";
	public static final String SAVED_GAME_BOOLEAN_BURGLAR = "burglar";
	public static final String SAVED_GAME_BOOLEAN_LION = "lion";
	public static final String SAVED_GAME_BOOLEAN_GARDENER = "gardener";
	public static final String SAVED_GAME_BOOLEAN_WEREWOLF = "werewolf";
	public static final String SAVED_GAME_BOOLEAN_SPACEMAN = "spaceman";
	public static final String SAVED_GAME_BOOLEAN_MATHEMATICIAN = "mathematician";
	public static final String SAVED_GAME_BOOLEAN_NEGATIVE_JACKS = "negativeJacks";
	public static final String SAVED_GAME_BOOLEAN_CARD_REVEAL = "cardReveal";
	public static final String SAVED_GAME_BOOLEAN_REVEAL_LISTENER = "revealListener";
	public static final String SAVED_GAME_BOOLEAN_REVEALED_DIFFERENT_SUIT = "revealedDifferentSuit";
	public static final String SAVED_GAME_BOOLEAN_REVEALED_SAME_SUIT = "revealedSameSuit";
	public static final String SAVED_GAME_INT_CARDS_REVEALED = "cardsRevealed";
	public static final String SAVED_GAME_INT_FIRST_SUIT_BID = "firstSuitBid";
	public static final String SAVED_GAME_BOOLEAN_DEVIATED_FROM_FIRST_SUIT = "deviatedFromFirstSuit";
	
	public static final String SAVED_GAME_INT_OPPONENT_THREE_CARDS_TO_SUBTRACT = "opponentThreeCardsToSubtract";
	public static final String SAVED_GAME_INT_OPPONENT_TWO_CARDS_TO_SUBTRACT = "opponentTwoCardsToSubtract";
	public static final String SAVED_GAME_INT_OPPONENT_ONE_CARDS_TO_SUBTRACT = "opponentOneCardsToSubtract";
	public static final String SAVED_GAME_INT_PLAYER_CARDS_TO_SUBTRACT = "playerCardsToSubtract";
	public static final String SAVED_GAME_INT_CURRENT_PLAYER = "currentPlayer";
	public static final String SAVED_GAME_INT_JOKER_QUANTITY = "jokerQuantity";
	public static final String SAVED_GAME_INT_JOKER_VALUE = "jokerValue";
	public static final String SAVED_GAME_INT_PERSON_TO_START = "personToStart";
	public static final String SAVED_GAME_INT_MAX_BID = "maxBid";
	public static final String SAVED_GAME_INT_LAST_BID_AMOUNT = "lastBidAmount";
	public static final String SAVED_GAME_INT_LAST_BID_SUIT_CODE = "lastBidSuitCode";
	public static final String SAVED_GAME_INT_BID_SUIT_CODE = "bidSuitCode";
	public static final String SAVED_GAME_INT_HANDICAP_AMOUNT = "handicapAmount";
	public static final String SAVED_GAME_INT_OPPONENT_THREE_NUMBER_OF_CARDS = "opponentThreeNumberOfCards";
	public static final String SAVED_GAME_INT_OPPONENT_TWO_NUMBER_OF_CARDS = "opponentTwoNumberOfCards";
	public static final String SAVED_GAME_INT_OPPONENT_ONE_NUMBER_OF_CARDS = "opponentOneNumberOfCards";
	public static final String SAVED_GAME_INT_PLAYER_NUMBER_OF_CARDS = "playerNumberOfCards";
	public static final String SAVED_GAME_INT_HISTORY_SIZE = "historySize";
	public static final String SAVED_GAME_STRING_GAME_MODE = "gameMode";

	//statics for default values etc
	public static final String TWO_COLOURS = "twocolour";
	public static final String FOUR_COLOURS = "fourcolour";
	public static final String DECK_DIRECTORY_CLASSIC = "classic";
	public static final String DECK_DIRECTORY_ALTERNATE = "alternate";
	public static final String JOKER_DIRECTORY_CLASSIC = "classic";
	public static final String JOKER_DIRECTORY_DEVELOPERS = "developers";
	public static final String BACK_CODE_CLASSIC_BLUE = "backBlue";
	public static final String DEFAULT_LOOK_AND_FEEL = "Metal";
	public static final int OPEN_ON_FIRST_ROUND = 0;
	public static final int OPEN_ON_LAST_ROUND = 1;
}