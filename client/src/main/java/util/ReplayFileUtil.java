package util;

import game.GameMode;
import object.FlagImage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import screen.ReplayDialog;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public final class ReplayFileUtil implements Registry
{
	public static final String FOLDER_PERSONAL_REPLAYS = "Personal";
	public static final String FOLDER_IMPORTED_REPLAYS = "Imported";
	
	public static final String PERFECT_FLAG = "perfectGame";
	public static final String BLIND_FLAG = "playedBlind";
	public static final String PARTIALLY_BLIND_FLAG = "partiallyBlind";
	public static final String HANDICAP_FLAG_PREFIX = "handicap";
	public static final String HANDICAP_FOUR_FLAG = HANDICAP_FLAG_PREFIX + "4";
	public static final String HANDICAP_THREE_FLAG = HANDICAP_FLAG_PREFIX + "3";
	public static final String HANDICAP_TWO_FLAG = HANDICAP_FLAG_PREFIX + "2";
	public static final String HANDICAP_ONE_FLAG = HANDICAP_FLAG_PREFIX + "1";
	public static final String ONLINE_FLAG = "onlineGame";
	public static final String MOON_FLAG = "moon";
	public static final String STAR_FLAG = "star";
	public static final String MOON_AND_STAR_FLAG = "moonAndStar";
	public static final String CHEAT_FLAG = "cheat";
	public static final String NO_FLAG = "none";
	
	public static final String CODE_PERFECT = "z";
	public static final String CODE_BLIND = "y";
	public static final String CODE_PARTIALLY_BLIND = "x";
	public static final String CODE_HANDICAP = "w";
	public static final String CODE_ONLINE = "s";
	public static final String CODE_MOON_AND_STAR = "r";
	public static final String CODE_STAR = "q";
	public static final String CODE_MOON = "p";
	public static final String CODE_CHEATS = "o";
	
	//Statics for the replay XML tags
	public static final String XML_REPLAY_INT_VERSION = "Version";
	public static final String XML_REPLAY_INT_GAME_MODE = "Mode";
	public static final String XML_REPLAY_INT_TOTAL_ROUNDS = "TotalRounds";
	public static final String XML_REPLAY_INT_JOKER_VALUE = "JokerValue";
	public static final String XML_REPLAY_INT_PLAYER_WON = "PlayerWon";
	public static final String XML_REPLAY_INT_GAME_COMPLETE = "GameComplete";
	public static final String XML_REPLAY_STRING_PLAYER_NAME = "PlayerName";
	public static final String XML_REPLAY_STRING_OPPONENT_ONE_NAME = "OpponentOneName";
	public static final String XML_REPLAY_STRING_OPPONENT_TWO_NAME = "OpponentTwoName";
	public static final String XML_REPLAY_STRING_OPPONENT_THREE_NAME = "OpponentThreeName";
	public static final String XML_REPLAY_STRING_OPPONENT_ONE_STRATEGY = "OpponentOneStrategy";
	public static final String XML_REPLAY_STRING_OPPONENT_TWO_STRATEGY = "OpponentTwoStrategy";
	public static final String XML_REPLAY_STRING_OPPONENT_THREE_STRATEGY = "OpponentThreeStrategy";
	public static final String XML_REPLAY_STRING_ROOM_NAME = "RoomName";
	public static final String XML_REPLAY_BOOLEAN_PLAY_BLIND = "PlayBlind";
	public static final String XML_REPLAY_BOOLEAN_PLAY_WITH_HANDICAP = "PlayWithHandicap";
	public static final String XML_REPLAY_INT_HANDICAP_AMOUNT = "HandicapAmount";
	public static final String XML_REPLAY_BOOLEAN_HAS_ACTED_BLIND_THIS_GAME = "HasActedBlindThisGame";
	public static final String XML_REPLAY_BOOLEAN_HAS_VIEWED_HAND_THIS_GAME = "HasViewedHandThisGame";
	public static final String XML_REPLAY_BOOLEAN_INCLUDE_MOONS = "IncludeMoons";
	public static final String XML_REPLAY_BOOLEAN_INCLUDE_STARS = "IncludeStars";
	public static final String XML_REPLAY_BOOLEAN_CHEAT_USED = "CheatUsed";

	public static final String REPLAY_ELEMENT_ROUND = "Round";
	public static final String ROUND_INT_NUMBER = "Number";	
	public static final String ROUND_INT_PERSON_TO_START = "PersonToStart";
	public static final String ROUND_BOOLEAN_PLAYER_ENABLED = "PlayerEnabled";
	public static final String ROUND_BOOLEAN_OPPONENT_ONE_ENABLED = "OpponentOneEnabled";
	public static final String ROUND_BOOLEAN_OPPONENT_TWO_ENABLED = "OpponentTwoEnabled";
	public static final String ROUND_BOOLEAN_OPPONENT_THREE_ENABLED = "OpponentThreeEnabled";
	public static final String ROUND_INT_PLAYER_NUMBER_OF_CARDS = "PlayerNumberOfCards";
	public static final String ROUND_INT_OPPONENT_ONE_NUMBER_OF_CARDS = "OpponentOneNumberOfCards";
	public static final String ROUND_INT_OPPONENT_TWO_NUMBER_OF_CARDS = "OpponentTwoNumberOfCards";
	public static final String ROUND_INT_OPPONENT_THREE_NUMBER_OF_CARDS = "OpponentThreeNumberOfCards";
	public static final String ROUND_INT_LAST_BID_SUIT_CODE = "LastBidSuitCode";
	
	public static final String ROUND_ELEMENT_HAND = "Hand";
	public static final String HAND_INT_PLAYER_NUMBER = "PlayerNumber";
	public static final String HAND_STRING_CARD = "Card";
	
	public static final String ROUND_ELEMENT_BID_HISTORY = "BidHistory";
	public static final String BID_HISTORY_INT_HISTORY_SIZE = "HistorySize";
	public static final String BID_HISTORY_STRING_HISTORY = "History";
	
	public static final String ROUND_ELEMENT_CHAT_HISTORY = "ChatHistory";	
	public static final String CHAT_HISTORY_ELEMENT_MESSAGE = "Message";
	public static final String MESSAGE_STRING_NUMBER = "Number";
	public static final String MESSAGE_STRING_USERNAME = "Username";
	public static final String MESSAGE_STRING_COLOUR = "Colour";
	public static final String MESSAGE_STRING_CONTENT = "Content";
	
	public static void saveInGameReplayToFile()
	{
		saveReplayToFile(inGameReplay);
	}
	
	public static void saveOnlineReplayToFile(String roomId, String username)
	{
		Preferences replay = Preferences.userRoot().node(NODE_ONLINE_REPLAY + roomId + username);
		saveReplayToFile(replay);
	}
	
	private static void saveReplayToFile(Preferences replay)
	{
		boolean save = prefs.getBoolean(PREFERENCES_BOOLEAN_SAVE_REPLAYS, false);
		if (!save)
		{
			return;
		}
		
		String directory = getDirectoryFromPreferences();
		String filename = factoryFileNameForReplay(replay);
		String folder = ReplayFileUtil.FOLDER_PERSONAL_REPLAYS;

		String folderDirectory = directory + "//Replays//" + folder;
		new File(folderDirectory).mkdirs();
		
		Path replayPath = Paths.get(directory + "//Replays//" + folder + "//"  + filename);
		
		String stringToWrite = getXmlStringFromReplay(replay);
		FileUtil.encodeAndSaveToFile(replayPath, stringToWrite);
	}
	
	public static void exportReplay(String filePath)
	{
		Path replayPath = Paths.get(filePath);
		String stringToWrite = getXmlStringFromReplay(tempReplayStore);
		
		FileUtil.encodeAndSaveToFile(replayPath, stringToWrite);
		RegistryUtil.clearNode(tempReplayStore);
	}
	
	public static String saveImportedReplay()
	{
		String directory = getDirectoryFromPreferences();
		String filename = factoryFileNameForReplay(tempReplayStore);
		String folder = ReplayFileUtil.FOLDER_IMPORTED_REPLAYS;

		String folderDirectory = directory + "//Replays//" + folder;
		new File(folderDirectory).mkdirs();
		
		Path replayPath = Paths.get(directory + "//Replays//" + folder + "//"  + filename);
		
		String stringToWrite = getXmlStringFromReplay(tempReplayStore);

		FileUtil.encodeAndSaveToFile(replayPath, stringToWrite);
		RegistryUtil.clearNode(tempReplayStore);
		
		return filename;
	}
	
	private static String getXmlStringFromReplay(Preferences replay)
	{
		Document document = XmlUtil.factoryNewDocument();
		Element rootElement = document.createElement("Replay");
		
		int mode = replay.getInt(REPLAY_INT_GAME_MODE, -1);
		boolean isEntropy = isEntropy(mode);
		
		appendStandardVariablesToXml(replay, rootElement);
		
		int totalRounds = replay.getInt(REPLAY_INT_ROUNDS_SO_FAR, 0);
		for (int i=1; i<=totalRounds; i++)
		{
			Element roundElement = document.createElement(REPLAY_ELEMENT_ROUND);
			roundElement.setAttribute(ROUND_INT_NUMBER, "" + i);
			
			int personToStart = replay.getInt(i + REPLAY_INT_PERSON_TO_START, 0);
			boolean playerEnabled = replay.getBoolean(i + REPLAY_BOOLEAN_PLAYER_ENABLED, true);
			boolean opponentOneEnabled = replay.getBoolean(i + REPLAY_BOOLEAN_OPPONENT_ONE_ENABLED, true);
			boolean opponentTwoEnabled = replay.getBoolean(i + REPLAY_BOOLEAN_OPPONENT_TWO_ENABLED, true);
			boolean opponentThreeEnabled = replay.getBoolean(i + REPLAY_BOOLEAN_OPPONENT_THREE_ENABLED, true);
			int playerNumberOfCards = replay.getInt(i + REPLAY_INT_PLAYER_NUMBER_OF_CARDS, 0);
			int opponentOneNumberOfCards = replay.getInt(i + REPLAY_INT_OPPONENT_ONE_NUMBER_OF_CARDS, 0);
			int opponentTwoNumberOfCards = replay.getInt(i + REPLAY_INT_OPPONENT_TWO_NUMBER_OF_CARDS, 0);
			int opponentThreeNumberOfCards = replay.getInt(i + REPLAY_INT_OPPONENT_THREE_NUMBER_OF_CARDS, 0);
			
			roundElement.setAttribute(ROUND_INT_PERSON_TO_START, "" + personToStart);
			XmlUtil.setAttributeBoolean(roundElement, ROUND_BOOLEAN_PLAYER_ENABLED, playerEnabled);
			XmlUtil.setAttributeBoolean(roundElement, ROUND_BOOLEAN_OPPONENT_ONE_ENABLED, opponentOneEnabled);
			XmlUtil.setAttributeBoolean(roundElement, ROUND_BOOLEAN_OPPONENT_TWO_ENABLED, opponentTwoEnabled);
			XmlUtil.setAttributeBoolean(roundElement, ROUND_BOOLEAN_OPPONENT_THREE_ENABLED, opponentThreeEnabled);
			roundElement.setAttribute(ROUND_INT_PLAYER_NUMBER_OF_CARDS, "" + playerNumberOfCards);
			roundElement.setAttribute(ROUND_INT_OPPONENT_ONE_NUMBER_OF_CARDS, "" + opponentOneNumberOfCards);
			roundElement.setAttribute(ROUND_INT_OPPONENT_TWO_NUMBER_OF_CARDS, "" + opponentTwoNumberOfCards);
			roundElement.setAttribute(ROUND_INT_OPPONENT_THREE_NUMBER_OF_CARDS, "" + opponentThreeNumberOfCards);
			
			addHandElement(replay, document, roundElement, playerNumberOfCards, 0);
			addHandElement(replay, document, roundElement, opponentOneNumberOfCards, 1);
			addHandElement(replay, document, roundElement, opponentTwoNumberOfCards, 2);
			addHandElement(replay, document, roundElement, opponentThreeNumberOfCards, 3);
			
			if (isEntropy)
			{
				int lastBidSuitCode = replay.getInt(i + REPLAY_INT_LAST_BID_SUIT_CODE, 0);
				roundElement.setAttribute(ROUND_INT_LAST_BID_SUIT_CODE, "" + lastBidSuitCode);
			}
			
			addListmodelElement(replay, document, roundElement, i);
			
			if (isOnline(mode))
			{
				addChatHistoryElement(replay, document, roundElement, i);
			}
			
			rootElement.appendChild(roundElement);
		}
		
		document.appendChild(rootElement);
		return XmlUtil.getStringFromDocument(document);
	}
	
	private static void appendStandardVariablesToXml(Preferences replay, Element rootElement)
	{
		int mode = replay.getInt(REPLAY_INT_GAME_MODE, -1);
		int totalRounds = replay.getInt(REPLAY_INT_ROUNDS_SO_FAR, 0);
		int jokerValue = replay.getInt(REPLAY_INT_JOKER_VALUE, 0);
		int playerWon = replay.getInt(REPLAY_INT_PLAYER_WON, 0);
		int gameComplete = replay.getInt(REPLAY_INT_GAME_COMPLETE, 0);
		String playerName = replay.get(REPLAY_STRING_PLAYER_NAME, "Player");
		String opponentOneName = replay.get(REPLAY_STRING_OPPONENT_ONE_NAME, "Opponent 1");
		String opponentTwoName = replay.get(REPLAY_STRING_OPPONENT_TWO_NAME, "Opponent 2");
		String opponentThreeName = replay.get(REPLAY_STRING_OPPONENT_THREE_NAME, "Opponent 3");
		boolean playBlind = replay.getBoolean(REPLAY_BOOLEAN_PLAY_BLIND, false);
		boolean playWithHandicap = replay.getBoolean(REPLAY_BOOLEAN_PLAY_WITH_HANDICAP, false);
		int handicapAmount = replay.getInt(REPLAY_INT_HANDICAP_AMOUNT, 1);
		boolean hasActedBlindThisGame = replay.getBoolean(REPLAY_BOOLEAN_HAS_ACTED_BLIND_THIS_GAME, false);
		boolean hasViewedHandThisGame = replay.getBoolean(REPLAY_BOOLEAN_HAS_VIEWED_HAND_THIS_GAME, false);
		boolean includeMoons = replay.getBoolean(REPLAY_BOOLEAN_INCLUDE_MOONS, false);
		boolean includeStars = replay.getBoolean(REPLAY_BOOLEAN_INCLUDE_STARS, false);
		boolean cheatUsed = replay.getBoolean(REPLAY_BOOLEAN_CHEAT_USED, false);
		String opponentOneStrategy = replay.get(REPLAY_STRING_OPPONENT_ONE_STRATEGY, "");
		String opponentTwoStrategy = replay.get(REPLAY_STRING_OPPONENT_TWO_STRATEGY, "");
		String opponentThreeStrategy = replay.get(REPLAY_STRING_OPPONENT_THREE_STRATEGY, "");
		String roomName = replay.get(REPLAY_STRING_ROOM_NAME, "");
		
		rootElement.setAttribute(XML_REPLAY_INT_GAME_MODE, "" + mode);
		rootElement.setAttribute(XML_REPLAY_INT_TOTAL_ROUNDS, "" + totalRounds);
		rootElement.setAttribute(XML_REPLAY_INT_JOKER_VALUE, "" + jokerValue);
		rootElement.setAttribute(XML_REPLAY_INT_PLAYER_WON, "" + playerWon);
		rootElement.setAttribute(XML_REPLAY_INT_GAME_COMPLETE, "" + gameComplete);
		rootElement.setAttribute(XML_REPLAY_STRING_PLAYER_NAME, playerName);
		rootElement.setAttribute(XML_REPLAY_STRING_OPPONENT_ONE_NAME, opponentOneName);
		rootElement.setAttribute(XML_REPLAY_STRING_OPPONENT_TWO_NAME, opponentTwoName);
		rootElement.setAttribute(XML_REPLAY_STRING_OPPONENT_THREE_NAME, opponentThreeName);
		rootElement.setAttribute(XML_REPLAY_INT_HANDICAP_AMOUNT, "" + handicapAmount);
		rootElement.setAttribute(XML_REPLAY_STRING_OPPONENT_ONE_STRATEGY, opponentOneStrategy);
		rootElement.setAttribute(XML_REPLAY_STRING_OPPONENT_TWO_STRATEGY, opponentTwoStrategy);
		rootElement.setAttribute(XML_REPLAY_STRING_OPPONENT_THREE_STRATEGY, opponentThreeStrategy);
		rootElement.setAttribute(XML_REPLAY_STRING_ROOM_NAME, roomName);
		
		rootElement.setAttribute(XML_REPLAY_INT_VERSION, "" + ReplayConverter.REPLAY_VERSION);
		
		XmlUtil.setAttributeBoolean(rootElement, XML_REPLAY_BOOLEAN_PLAY_BLIND, playBlind);
		XmlUtil.setAttributeBoolean(rootElement, XML_REPLAY_BOOLEAN_PLAY_WITH_HANDICAP, playWithHandicap);
		XmlUtil.setAttributeBoolean(rootElement, XML_REPLAY_BOOLEAN_HAS_ACTED_BLIND_THIS_GAME, hasActedBlindThisGame);
		XmlUtil.setAttributeBoolean(rootElement, XML_REPLAY_BOOLEAN_HAS_VIEWED_HAND_THIS_GAME, hasViewedHandThisGame);
		XmlUtil.setAttributeBoolean(rootElement, XML_REPLAY_BOOLEAN_INCLUDE_MOONS, includeMoons);
		XmlUtil.setAttributeBoolean(rootElement, XML_REPLAY_BOOLEAN_INCLUDE_STARS, includeStars);
		XmlUtil.setAttributeBoolean(rootElement, XML_REPLAY_BOOLEAN_CHEAT_USED, cheatUsed);
	}
	
	private static void addHandElement(Preferences replay, Document document, Element roundElement, 
	  int numberOfCards, int playerNumber)
	{
		int roundNumber = XmlUtil.getAttributeInt(roundElement, "Number");
		Element handElement = document.createElement(ROUND_ELEMENT_HAND);
		handElement.setAttribute(HAND_INT_PLAYER_NUMBER, "" + playerNumber);
		
		String hand = "";
		if (playerNumber == 0)
		{
			hand = REPLAY_STRING_PLAYER_HAND;
		}
		else if (playerNumber == 1)
		{
			hand = REPLAY_STRING_OPPONENT_ONE_HAND;
		}
		else if (playerNumber == 2)
		{
			hand = REPLAY_STRING_OPPONENT_TWO_HAND;
		}
		else if (playerNumber == 3)
		{
			hand = REPLAY_STRING_OPPONENT_THREE_HAND;
		}
		
		for (int i = 0; i < numberOfCards; i++)
		{
			String card = replay.get(roundNumber + hand + i, "");
			handElement.setAttribute(HAND_STRING_CARD + i, card);
		}
		
		roundElement.appendChild(handElement);
	}
	
	private static void addListmodelElement(Preferences replay, Document document, Element roundElement, 
	  int roundNumber)
	{
		int historySize = replay.getInt(roundNumber + REPLAY_INT_HISTORY_SIZE, 0);
		
		Element listmodelElement = document.createElement(ROUND_ELEMENT_BID_HISTORY);
		listmodelElement.setAttribute(BID_HISTORY_INT_HISTORY_SIZE, "" + historySize);
		
		for (int i=0; i<historySize; i++)
		{
			String history = replay.get(roundNumber + REPLAY_STRING_LISTMODEL + i, "");
			listmodelElement.setAttribute(BID_HISTORY_STRING_HISTORY + i, history);
		}
		
		roundElement.appendChild(listmodelElement);
	}
	
	private static void addChatHistoryElement(Preferences replay, Document document, Element roundElement, 
	  int roundNumber)
	{
		Element chatHistoryElement = document.createElement(ROUND_ELEMENT_CHAT_HISTORY);
		for (int i=0; i<ReplayDialog.RECENT_CHAT_MESSAGES_TO_SHOW; i++)
		{
			Element messageElement = document.createElement(CHAT_HISTORY_ELEMENT_MESSAGE);
			String colour = replay.get(roundNumber + REPLAY_STRING_CHAT_COLOUR + i, "");
			String username = replay.get(roundNumber + REPLAY_STRING_CHAT_USERNAME + i, "");
			String content = replay.get(roundNumber + REPLAY_STRING_CHAT_CONTENT + i, "");
			
			if (!content.isEmpty())
			{
				messageElement.setAttribute(MESSAGE_STRING_NUMBER, "" + i);
				messageElement.setAttribute(MESSAGE_STRING_COLOUR, colour);
				messageElement.setAttribute(MESSAGE_STRING_USERNAME, username);
				messageElement.setAttribute(MESSAGE_STRING_CONTENT, content);
			}
			
			chatHistoryElement.appendChild(messageElement);
		}
		
		roundElement.appendChild(chatHistoryElement);
	}
	
	public static String getDirectoryFromPreferences()
	{
		return prefs.get(PREFERENCES_STRING_REPLAY_DIRECTORY, System.getProperty("user.dir"));
	}
	
	private static String factoryFileNameForReplay(Preferences replay)
	{
		int mode = replay.getInt(REPLAY_INT_GAME_MODE, -1);
		
		String fileName = "";

		switch (mode)
		{
			case ReplayConstants.GAME_MODE_ENTROPY:
			case ReplayConstants.GAME_MODE_ENTROPY_ONLINE:
				fileName += "E";
				break;
				
			case ReplayConstants.GAME_MODE_VECTROPY:
			case ReplayConstants.GAME_MODE_VECTROPY_ONLINE:
				fileName += "V";
				break;

			default: 
				Debug.stackTrace("Invalid gameMode when saving replay: " + mode);
		}
		
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		Date date = new Date();
		fileName += dateFormat.format(date);
		
		//String playerName = replay.get(REPLAY_STRING_PLAYER_NAME, "Player");
		int startNumberOfCards = replay.getInt(1 + REPLAY_INT_PLAYER_NUMBER_OF_CARDS, 0);
		boolean playWithHandicap = replay.getBoolean(REPLAY_BOOLEAN_PLAY_WITH_HANDICAP, false);
		if (playWithHandicap)
		{
			int handicapAmount = replay.getInt(REPLAY_INT_HANDICAP_AMOUNT, 0);
			startNumberOfCards += handicapAmount;
		}
		
		int playerCoeff = replay.getBoolean(1 + REPLAY_BOOLEAN_PLAYER_ENABLED, false)? 1:0;
		int opponentOneCoeff = replay.getBoolean(1 + REPLAY_BOOLEAN_OPPONENT_ONE_ENABLED, false)? 1:0;
		int opponentTwoCoeff = replay.getBoolean(1 + REPLAY_BOOLEAN_OPPONENT_TWO_ENABLED, false)? 1:0;
		int opponentThreeCoeff = replay.getBoolean(1 + REPLAY_BOOLEAN_OPPONENT_THREE_ENABLED, false)? 1:0;
		
		int totalPlayers = playerCoeff + opponentOneCoeff + opponentTwoCoeff + opponentThreeCoeff;

		fileName += startNumberOfCards;
		fileName += totalPlayers;
		fileName += ".txt";
		
		return fileName;
	}
	
	public static String getFormattedDateFromFileName(String name)
	{
		String year = name.substring(1, 5);
		String month = name.substring(5, 7);
		String day = name.substring(7, 9);
		String hours = name.substring(9, 11);
		String minutes = name.substring(11, 13);
		
		return day + "/" + month + "/" + year + "   " + hours + ":" + minutes;
	}
	
	public static String getComparableDateFromFileName(String name)
	{
		return name.substring(1, 9);
	}
	
	public static String getNumberOfPlayersFromFileName(String name)
	{
		int index = name.indexOf(".txt");
		
		return name.substring(index - 1, index);
	}
	
	public static String getNumberOfCardsFromFileName(String name)
	{
		int extension = name.indexOf('.');
		return name.substring(extension-2, extension-1);
	}
	
	public static boolean filenameIsValid(String filename)
	{
		int length = filename.length();
		
		//21 is the minimum length - all filenames will be this length from now on. 
		//Previously, the player name was included in the filename, but this was not safe as it's user-defined
		//so could contain special characters not allowed by Windows in filenames.
		if (length > 31 || length < 21)
		{
			return false;
		}
		
		int extension = filename.indexOf('.');
		
		String fileExtension = filename.substring(extension);
		
		if (!fileExtension.equals(".txt"))
		{
			return false;
		}
		
		String e = filename.substring(0, 1);
		
		if (!e.equals("E") && !e.equals("V"))
		{
			return false;
		}
		
		int year = -1;
		int month = -1;
		int day = -1;
		int hour = -1;
		int minutes = -1;
		int seconds = -1;
		int totalPlayers = -1;
		int numberOfCards = -1;
		
		try
		{
			year = Integer.parseInt(filename.substring(1, 5));
			month = Integer.parseInt(filename.substring(5, 7));
			day = Integer.parseInt(filename.substring(7, 9));
			hour = Integer.parseInt(filename.substring(9, 11));
			minutes = Integer.parseInt(filename.substring(11, 13));
			seconds = Integer.parseInt(filename.substring(13, 15));
			
			totalPlayers = Integer.parseInt(filename.substring(extension-1, extension));
			numberOfCards = Integer.parseInt(filename.substring(extension-2, extension-1));
		}
		catch (NumberFormatException x)
		{
			Debug.append("Caught NFE when validating filename " + filename, true);
			return false;
		}
		
		if (totalPlayers > 4
		 || totalPlayers < 2
		 || numberOfCards > 5
		 || numberOfCards < 1
		 || month < 1
		 || month > 12
		 || day < 1
		 || day > 31
		 || hour < 0
		 || hour > 24
		 || minutes < 0
		 || minutes > 60
		 || seconds < 0
		 || seconds > 60
		 || year < 2000
		 || year > 2500)
		{
			return false;
		}
		
		return true;
	}

	private static boolean isEntropy(int replayGameMode)
	{
		return ReplayConstants.toGameMode(replayGameMode) == GameMode.Entropy;
	}
	public static boolean isOnline(int replayGameMode)
	{
		return replayGameMode == ReplayConstants.GAME_MODE_ENTROPY_ONLINE ||
				replayGameMode == ReplayConstants.GAME_MODE_VECTROPY_ONLINE;
	}
	
	public static boolean successfullyFilledRegistryFromFile(String filePath, Preferences replay)
	{
		try 
		{
			replay.clear();
		} 
		catch (BackingStoreException e) 
		{
			Debug.stackTrace(e);
		}
		
		Document xmlDocumentToRead = getXmlDocumentFromFile(filePath);
		if (xmlDocumentToRead == null)
		{
			return false;
		}
		
		Element rootElement = xmlDocumentToRead.getDocumentElement();
		
		int mode = XmlUtil.getAttributeInt(rootElement, XML_REPLAY_INT_GAME_MODE);
		int playerWon = XmlUtil.getAttributeInt(rootElement, XML_REPLAY_INT_PLAYER_WON);
		int gameComplete = XmlUtil.getAttributeInt(rootElement, XML_REPLAY_INT_GAME_COMPLETE);
		int totalRounds = XmlUtil.getAttributeInt(rootElement, XML_REPLAY_INT_TOTAL_ROUNDS);
		int jokerValue = XmlUtil.getAttributeInt(rootElement, XML_REPLAY_INT_JOKER_VALUE);
		int handicapAmount = XmlUtil.getAttributeInt(rootElement, XML_REPLAY_INT_HANDICAP_AMOUNT);
		
		String playerName = rootElement.getAttribute(XML_REPLAY_STRING_PLAYER_NAME);
		String opponentOneName = rootElement.getAttribute(XML_REPLAY_STRING_OPPONENT_ONE_NAME);
		String opponentTwoName = rootElement.getAttribute(XML_REPLAY_STRING_OPPONENT_TWO_NAME);
		String opponentThreeName = rootElement.getAttribute(XML_REPLAY_STRING_OPPONENT_THREE_NAME);
		
		String opponentOneStrategy = rootElement.getAttribute(XML_REPLAY_STRING_OPPONENT_ONE_STRATEGY);
		String opponentTwoStrategy = rootElement.getAttribute(XML_REPLAY_STRING_OPPONENT_TWO_STRATEGY);
		String opponentThreeStrategy = rootElement.getAttribute(XML_REPLAY_STRING_OPPONENT_THREE_STRATEGY);
		String roomName = rootElement.getAttribute(XML_REPLAY_STRING_ROOM_NAME);
		
		boolean playBlind = XmlUtil.getAttributeBoolean(rootElement, XML_REPLAY_BOOLEAN_PLAY_BLIND);
		boolean playWithHandicap = XmlUtil.getAttributeBoolean(rootElement, XML_REPLAY_BOOLEAN_PLAY_WITH_HANDICAP);
		boolean hasActedBlindThisGame = XmlUtil.getAttributeBoolean(rootElement, XML_REPLAY_BOOLEAN_HAS_ACTED_BLIND_THIS_GAME);
		boolean hasViewedHandThisGame = XmlUtil.getAttributeBoolean(rootElement, XML_REPLAY_BOOLEAN_HAS_VIEWED_HAND_THIS_GAME);
		boolean includeMoons = XmlUtil.getAttributeBoolean(rootElement, XML_REPLAY_BOOLEAN_INCLUDE_MOONS);
		boolean includeStars = XmlUtil.getAttributeBoolean(rootElement, XML_REPLAY_BOOLEAN_INCLUDE_STARS);
		
		try
		{
			//non round dependent stuff
			replay.putInt(REPLAY_INT_GAME_MODE, mode);
			replay.putInt(REPLAY_INT_PLAYER_WON, playerWon);
			replay.putInt(REPLAY_INT_GAME_COMPLETE, gameComplete);
			replay.putInt(REPLAY_INT_ROUNDS_SO_FAR, totalRounds);
			replay.putInt(REPLAY_INT_JOKER_VALUE, jokerValue);
			replay.put(REPLAY_STRING_PLAYER_NAME, playerName);
			replay.put(REPLAY_STRING_OPPONENT_ONE_NAME, opponentOneName);
			replay.put(REPLAY_STRING_OPPONENT_TWO_NAME, opponentTwoName);
			replay.put(REPLAY_STRING_OPPONENT_THREE_NAME, opponentThreeName);
			replay.put(REPLAY_STRING_OPPONENT_ONE_STRATEGY, opponentOneStrategy);
			replay.put(REPLAY_STRING_OPPONENT_TWO_STRATEGY, opponentTwoStrategy);
			replay.put(REPLAY_STRING_OPPONENT_THREE_STRATEGY, opponentThreeStrategy);
			replay.put(REPLAY_STRING_ROOM_NAME, roomName);
			replay.putBoolean(REPLAY_BOOLEAN_PLAY_BLIND, playBlind);
			replay.putBoolean(REPLAY_BOOLEAN_PLAY_WITH_HANDICAP, playWithHandicap);
			replay.putInt(REPLAY_INT_HANDICAP_AMOUNT, handicapAmount);
			replay.putBoolean(REPLAY_BOOLEAN_HAS_ACTED_BLIND_THIS_GAME, hasActedBlindThisGame);
			replay.putBoolean(REPLAY_BOOLEAN_HAS_VIEWED_HAND_THIS_GAME, hasViewedHandThisGame);
			replay.putBoolean(REPLAY_BOOLEAN_INCLUDE_MOONS, includeMoons);
			replay.putBoolean(REPLAY_BOOLEAN_INCLUDE_STARS, includeStars);
			
			boolean isEntropy = isEntropy(mode);
			boolean isOnline = isOnline(mode);
			
			return addRoundsToRegistry(replay, rootElement, isEntropy, isOnline);
		}
		catch (Throwable t)
		{
			Debug.append("Caught " + t.toString() + " when trying to fill registry from file " + filePath, true);
			return false;
		}
	}
	
	public static Document getXmlDocumentFromFile(String filePath)
	{
		File replayFile = new File(filePath);
		String decodedBytesStr = FileUtil.getBase64DecodedFileContentsAsString(replayFile);
		String[] lines = decodedBytesStr.split("\n");
		
		if (lines.length > 1)
		{
			Debug.stackTrace("Got " + lines.length + " lines from XML replay file - should only be 1!");
		}
		
		String xmlString = lines[0];
		return XmlUtil.getDocumentFromXmlString(xmlString);
	}
	
	public static ReplayRowWrapper factoryRowWrapper(String filename, String folder)
	{
		String directory = ReplayFileUtil.getDirectoryFromPreferences();
		String replayPath = directory + "//Replays//" + folder + "//" + filename;
		Document document = ReplayFileUtil.getXmlDocumentFromFile(replayPath);
		Element rootElement = document.getDocumentElement();
		
		String playerWon = rootElement.getAttribute(XML_REPLAY_INT_PLAYER_WON);
		String gameComplete = rootElement.getAttribute(XML_REPLAY_INT_GAME_COMPLETE);
		
		//optional stuff
		int mode = XmlUtil.getAttributeInt(rootElement, XML_REPLAY_INT_GAME_MODE);
		String modeDesc = ReplayConstants.toGameMode(mode).name();
		String roomName = rootElement.getAttribute(XML_REPLAY_STRING_ROOM_NAME);
		String rounds = rootElement.getAttribute(XML_REPLAY_INT_TOTAL_ROUNDS);
		
		String date = ReplayFileUtil.getFormattedDateFromFileName(filename);
		String name = rootElement.getAttribute(XML_REPLAY_STRING_PLAYER_NAME);
		String numberOfPlayers = ReplayFileUtil.getNumberOfPlayersFromFileName(filename);
		String numberOfCards = ReplayFileUtil.getNumberOfCardsFromFileName(filename);
		
		FlagImage flag = createFlagFromFile(filename, rootElement);
		
		return new ReplayRowWrapper(filename, playerWon, gameComplete, modeDesc, roomName, rounds, 
									date, name, numberOfPlayers, numberOfCards, flag);
	}
	
	private static FlagImage createFlagFromFile(String file, Element rootElement)
	{
		FlagImage image = new FlagImage();
		
		int mode = XmlUtil.getAttributeInt(rootElement, XML_REPLAY_INT_GAME_MODE);
		int playerWon = XmlUtil.getAttributeInt(rootElement, XML_REPLAY_INT_PLAYER_WON);
		int numberOfRounds = XmlUtil.getAttributeInt(rootElement, XML_REPLAY_INT_TOTAL_ROUNDS);
		String players = ReplayFileUtil.getNumberOfPlayersFromFileName(file);
		String cards = ReplayFileUtil.getNumberOfCardsFromFileName(file);
		
		if (playerWon == 1 && cards.equals("5"))
		{
			if (players.equals("4") && numberOfRounds == 15
			 || players.equals("3") && numberOfRounds == 10
			 || players.equals("2") && numberOfRounds == 5)
			{
				image.appendImage(PERFECT_FLAG, CODE_PERFECT);
				image.appendToolTip("Perfect Game");
			}
		}
		
		boolean playedBlind = XmlUtil.getAttributeBoolean(rootElement, XML_REPLAY_BOOLEAN_PLAY_BLIND);
		boolean hasActedBlindThisGame = XmlUtil.getAttributeBoolean(rootElement, XML_REPLAY_BOOLEAN_HAS_ACTED_BLIND_THIS_GAME);
		boolean hasViewedHandThisGame = XmlUtil.getAttributeBoolean(rootElement, XML_REPLAY_BOOLEAN_HAS_VIEWED_HAND_THIS_GAME);
		
		if (playedBlind && !hasViewedHandThisGame)
		{
			image.appendImage(BLIND_FLAG, CODE_BLIND);
			image.appendToolTip("Entire game played blind");
		}
		else if (playedBlind && hasActedBlindThisGame)
		{
			image.appendImage(PARTIALLY_BLIND_FLAG, CODE_PARTIALLY_BLIND);
			image.appendToolTip("Some rounds were played blind");
		}
		
		boolean playWithHandicap = XmlUtil.getAttributeBoolean(rootElement, XML_REPLAY_BOOLEAN_PLAY_WITH_HANDICAP);
		int handicapAmount = XmlUtil.getAttributeInt(rootElement, XML_REPLAY_INT_HANDICAP_AMOUNT);
		
		if (playWithHandicap)
		{
			image.appendToolTip("-" + handicapAmount + " cards");
			String flagStr = HANDICAP_FLAG_PREFIX + handicapAmount;
			String code = CODE_HANDICAP + handicapAmount;
			image.appendImage(flagStr, code);
		}
		
		if (isOnline(mode))
		{
			image.appendImage(ONLINE_FLAG, CODE_ONLINE);
			image.appendToolTip("Online");
		}
		
		boolean includeMoons = XmlUtil.getAttributeBoolean(rootElement, XML_REPLAY_BOOLEAN_INCLUDE_MOONS);
		boolean includeStars = XmlUtil.getAttributeBoolean(rootElement, XML_REPLAY_BOOLEAN_INCLUDE_STARS);
		if (includeMoons && includeStars)
		{
			image.appendImage(MOON_AND_STAR_FLAG, CODE_MOON_AND_STAR);
			image.appendToolTip("Moons and Stars included");
		}
		else if (includeStars)
		{
			image.appendImage(STAR_FLAG, CODE_STAR);
			image.appendToolTip("Stars included");
		}
		else if (includeMoons)
		{
			image.appendImage(MOON_FLAG, CODE_MOON);
			image.appendToolTip("Moons included");
		}
		
		boolean cheatUsed = XmlUtil.getAttributeBoolean(rootElement, XML_REPLAY_BOOLEAN_CHEAT_USED);
		if (cheatUsed)
		{
			image.appendImage(CHEAT_FLAG, CODE_CHEATS);
			image.appendToolTip("Cheats used");
		}
		
		return image;
	}
	
	private static boolean addRoundsToRegistry(Preferences replay, Element rootElement, boolean entropy, boolean online)
	{
		NodeList children = rootElement.getElementsByTagName(REPLAY_ELEMENT_ROUND);
		int length = children.getLength();
		
		for (int i=0; i<length; i++)
		{
			Element roundElement = (Element)children.item(i);
			
			int roundNumber = XmlUtil.getAttributeInt(roundElement, ROUND_INT_NUMBER);
			int personToStart = XmlUtil.getAttributeInt(roundElement, ROUND_INT_PERSON_TO_START);
			int playerNumberOfCards = XmlUtil.getAttributeInt(roundElement, ROUND_INT_PLAYER_NUMBER_OF_CARDS);
			int opponentOneNumberOfCards = XmlUtil.getAttributeInt(roundElement, ROUND_INT_OPPONENT_ONE_NUMBER_OF_CARDS);
			int opponentTwoNumberOfCards = XmlUtil.getAttributeInt(roundElement, ROUND_INT_OPPONENT_TWO_NUMBER_OF_CARDS);
			int opponentThreeNumberOfCards = XmlUtil.getAttributeInt(roundElement, ROUND_INT_OPPONENT_THREE_NUMBER_OF_CARDS);
			
			boolean playerEnabled = XmlUtil.getAttributeBoolean(roundElement, ROUND_BOOLEAN_PLAYER_ENABLED);
			boolean opponentOneEnabled = XmlUtil.getAttributeBoolean(roundElement, ROUND_BOOLEAN_OPPONENT_ONE_ENABLED);
			boolean opponentTwoEnabled = XmlUtil.getAttributeBoolean(roundElement, ROUND_BOOLEAN_OPPONENT_TWO_ENABLED);
			boolean opponentThreeEnabled = XmlUtil.getAttributeBoolean(roundElement, ROUND_BOOLEAN_OPPONENT_THREE_ENABLED);

			replay.putInt(roundNumber + REPLAY_INT_PERSON_TO_START, personToStart);
			replay.putBoolean(roundNumber + REPLAY_BOOLEAN_PLAYER_ENABLED, playerEnabled);
			replay.putBoolean(roundNumber + REPLAY_BOOLEAN_OPPONENT_ONE_ENABLED, opponentOneEnabled);
			replay.putBoolean(roundNumber + REPLAY_BOOLEAN_OPPONENT_TWO_ENABLED, opponentTwoEnabled);
			replay.putBoolean(roundNumber + REPLAY_BOOLEAN_OPPONENT_THREE_ENABLED, opponentThreeEnabled);
			replay.putInt(roundNumber + REPLAY_INT_PLAYER_NUMBER_OF_CARDS, playerNumberOfCards);
			replay.putInt(roundNumber + REPLAY_INT_OPPONENT_ONE_NUMBER_OF_CARDS, opponentOneNumberOfCards);
			replay.putInt(roundNumber + REPLAY_INT_OPPONENT_TWO_NUMBER_OF_CARDS, opponentTwoNumberOfCards);
			replay.putInt(roundNumber + REPLAY_INT_OPPONENT_THREE_NUMBER_OF_CARDS, opponentThreeNumberOfCards);

			saveHandsToRegistry(roundElement, roundNumber, replay);

			if (entropy)
			{
				int lastBidSuitCode = XmlUtil.getAttributeInt(roundElement, ROUND_INT_LAST_BID_SUIT_CODE);
				replay.putInt(roundNumber + REPLAY_INT_LAST_BID_SUIT_CODE, lastBidSuitCode);
			}

			saveListModelToRegistry(roundElement, roundNumber, replay);
			
			if (online)
			{
				saveChatHistoryToRegistry(roundElement, roundNumber, replay);
			}
		}
		
		return true;
	}
	private static void saveHandsToRegistry(Element roundElement, int roundNumber, Preferences replay)
	{
		NodeList children = roundElement.getElementsByTagName(ROUND_ELEMENT_HAND);
		int length = children.getLength();
		
		for (int j=0; j<length; j++)
		{
			Element handElement = (Element)children.item(j);
			int playerNumber = XmlUtil.getAttributeInt(handElement, HAND_INT_PLAYER_NUMBER);
			
			int numberOfCards = 0;
			String registryNodeForPlayer = "";
			if (playerNumber == 0)
			{
				numberOfCards = replay.getInt(roundNumber + REPLAY_INT_PLAYER_NUMBER_OF_CARDS, 0);
				registryNodeForPlayer = REPLAY_STRING_PLAYER_HAND;
			}
			else if (playerNumber == 1)
			{
				numberOfCards = replay.getInt(roundNumber + REPLAY_INT_OPPONENT_ONE_NUMBER_OF_CARDS, 0);
				registryNodeForPlayer = REPLAY_STRING_OPPONENT_ONE_HAND;
			}
			else if (playerNumber == 2)
			{
				numberOfCards = replay.getInt(roundNumber + REPLAY_INT_OPPONENT_TWO_NUMBER_OF_CARDS, 0);
				registryNodeForPlayer = REPLAY_STRING_OPPONENT_TWO_HAND;
			}
			else if (playerNumber == 3)
			{
				numberOfCards = replay.getInt(roundNumber + REPLAY_INT_OPPONENT_THREE_NUMBER_OF_CARDS, 0);
				registryNodeForPlayer = REPLAY_STRING_OPPONENT_THREE_HAND;
			}
			
			for (int i=0; i<numberOfCards; i++)
			{
				replay.put(roundNumber + registryNodeForPlayer + i, handElement.getAttribute(HAND_STRING_CARD + i));
			}
		}
	}
	private static void saveListModelToRegistry(Element roundElement, int roundNumber, Preferences replay)
	{
		NodeList children = roundElement.getElementsByTagName(ROUND_ELEMENT_BID_HISTORY);
		Element bidHistoryElement = (Element)children.item(0);
		
		int historySize = XmlUtil.getAttributeInt(bidHistoryElement, BID_HISTORY_INT_HISTORY_SIZE);
		replay.putInt(roundNumber + REPLAY_INT_HISTORY_SIZE, historySize);
		
		for (int i=0; i<historySize; i++)
		{
			String historyItem = bidHistoryElement.getAttribute(BID_HISTORY_STRING_HISTORY + i);
			replay.put(roundNumber + REPLAY_STRING_LISTMODEL + i, historyItem);
		}
	}
	private static void saveChatHistoryToRegistry(Element roundElement, int roundNumber, Preferences replay)
	{
		NodeList chatHistoryList = roundElement.getElementsByTagName(ROUND_ELEMENT_CHAT_HISTORY);
		Element chatHistoryElement = (Element)chatHistoryList.item(0);
		
		NodeList messageList = chatHistoryElement.getElementsByTagName(CHAT_HISTORY_ELEMENT_MESSAGE);
		int length = messageList.getLength();
		
		for (int i=0; i<length; i++)
		{
			Element messageElement = (Element)messageList.item(i);
			
			String colour = messageElement.getAttribute(MESSAGE_STRING_COLOUR);
			String username = messageElement.getAttribute(MESSAGE_STRING_USERNAME);
			String content = messageElement.getAttribute(MESSAGE_STRING_CONTENT);
			String number = messageElement.getAttribute(MESSAGE_STRING_NUMBER);
			
			replay.put(roundNumber + REPLAY_STRING_CHAT_COLOUR + number, colour);
			replay.put(roundNumber + REPLAY_STRING_CHAT_USERNAME + number, username);
			replay.put(roundNumber + REPLAY_STRING_CHAT_CONTENT + number, content);
		}
	}
	
	public static void moveReplays(File[] myFiles, File[] importedFiles, String oldDirectory, String newDirectory)
	{
		try
		{
			moveFiles(myFiles, newDirectory, ReplayFileUtil.FOLDER_PERSONAL_REPLAYS);
			moveFiles(importedFiles, newDirectory, ReplayFileUtil.FOLDER_IMPORTED_REPLAYS);
		}
		catch (Throwable x)
		{
			Debug.stackTrace(x);
			DialogUtil.showError("Moving of one or more files failed. "
							   + "These have been left in the old directory and will have to be moved manually.");
			return;
		}

		try
		{
			Files.delete(Paths.get(oldDirectory + "//Replays//" + ReplayFileUtil.FOLDER_PERSONAL_REPLAYS));
			Files.delete(Paths.get(oldDirectory + "//Replays//" + ReplayFileUtil.FOLDER_IMPORTED_REPLAYS));
			Files.delete(Paths.get(oldDirectory + "//Replays"));
		}
		catch (Throwable x) 
		{
			Debug.stackTrace(x);
			DialogUtil.showError("Failed to delete old directory folders.");
			return;
		}
		
		DialogUtil.showInfo("Replays moved successfully.");
		return;
	}
	
	private static void moveFiles(File[] files, String newDirectory, String folder) throws IOException
	{
		int length = files.length;
		for (int i=0; i<length; i++)
		{
			File file = files[i];
			String name = file.getName();
			String oldPath = file.getPath();
			String newPath = newDirectory + "//Replays//" + folder + "//" + name;
			
			new File(newPath).mkdirs();
			
			Files.move(Paths.get(oldPath), Paths.get(newPath), StandardCopyOption.REPLACE_EXISTING);
		}
	}
	
	public static String deleteCorruptReplays(ArrayList<File> listOfFiles)
	{
		String failedReplays = "";
		
		int existingFileCount = listOfFiles.size();
		
		for (int i=existingFileCount-1; i>=0; i--)
		{
			File fileToDelete = listOfFiles.remove(i);
			Path filePath = fileToDelete.toPath();
			
			try 
			{
				Files.delete(filePath);
			} 
			catch (Throwable e) 
			{
				Debug.stackTrace(e);
				failedReplays += "\n" + fileToDelete.getName();
				continue;
			}
		}
		
		return failedReplays;
	}
	
	public static String deleteReplays(String folder)
	{
		String directory = getDirectoryFromPreferences() + "//Replays//" + folder;
		
		File[] replayFiles = new File(directory).listFiles();
		
		String failedReplays = "";
		
		if (replayFiles == null)
		{
			//Just log a line - this happens if there isn't a directory
			//Debug.stackTrace("replayFiles was null for path " + directory);
			Debug.append("replayFiles was null for path " + directory);
			return "noReplays";
		}
		
		int existingFileCount = replayFiles.length;
		if (existingFileCount == 0)
		{
			Debug.append("There were no files to delete in " + directory + ".", true);
			return "noReplays";
		}
		
		for (int i=0; i<existingFileCount; i++)
		{
			String fileName = "<Unknown Name>";
			try 
			{
				File fileToDelete = replayFiles[i];
				fileName = fileToDelete.getName();
				Path filePath = fileToDelete.toPath();
				Files.delete(filePath);
			} 
			catch (Throwable e) 
			{
				Debug.stackTrace(e);
				failedReplays += "\n" + fileName;
				continue;
			}
		}
		
		return failedReplays;
	}
}