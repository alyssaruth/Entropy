package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JOptionPane;

import object.ApiStrategy;
import object.Bid;
import object.ChallengeBid;
import object.EntropyBid;
import object.IllegalBid;
import object.Player;
import object.VectropyBid;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ApiUtil implements Registry
{
	public static final String API_PREFIX = "API: ";
	public static final String MESSAGE_TYPE_XML = "XML";
	public static final String MESSAGE_TYPE_JSON = "JSON";
	
	private static final String ROOT_TAG_API_MESSAGE = "ApiMessage";
	private static final InetAddress INET_ADDRESS_LOCALHOST = MessageUtil.factoryInetAddress("localhost");
	
	//Cache this for speed in the simulator
	private static HashMap<String, ApiStrategy> hmNameToApiStrategy = null;
	private static ApiStrategy apiStrategy = null;
	
	public static void sendTestMessage(int port, boolean xml)
	{
		if (!xml)
		{
			DialogUtil.showError("JSON is currently unsupported.");
			return;
		}
		
		Document xmlDoc = XmlUtil.factorySimpleMessage("ApiTest");
		String messageString = XmlUtil.getStringFromDocument(xmlDoc);
		sendWithCatch(messageString, port, true, true);
	}
	
	public static Bid processApiTurn(StrategyParms parms, Player player)
	{
		apiStrategy = getApiStrategy(player.getStrategy());
		int port = apiStrategy.getPortNumber();
		String messageType = apiStrategy.getMessageType();
		
		String messageString = factoryApiMessage(parms, player, messageType);
		
		String responseString = sendWithCatch(messageString, port, parms.getLogging(), false);
		if (responseString == null)
		{
			//An error occurred which we'll already have caught.
			return null;
		}
		
		return handleResponse(parms, responseString);
	}
	
	private static String sendWithCatch(String messageString, int port, boolean logging, boolean testMode)
	{
		Debug.append("API OUT: " + messageString, logging);
		
		BufferedReader in = null;
		String responseString = null;
		
		try (Socket socket = new Socket(INET_ADDRESS_LOCALHOST, port);
		  PrintWriter out = new PrintWriter(socket.getOutputStream(), true);)
		{
			//Allow 10s for API, it should be fast over localhost.
			socket.setSoTimeout(10000);
			
			out.write(messageString + "\n");
			out.flush();

			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			responseString = in.readLine();
			
			Debug.append("API IN: " + responseString, logging);
		}
		catch (SocketException | SocketTimeoutException t)
		{
			Debug.append("Caught " + t.getMessage() + " sending message via API.");
			String question = "An error occurred connecting to the third party software.\n\nRetry?";
			int option = DialogUtil.showQuestion(question, false);
			if (option == JOptionPane.YES_OPTION)
			{
				sendWithCatch(messageString, port, logging, testMode);
			}
			else if (!testMode)
			{
				saveStrategyErrorAndUnsetStrategies(apiStrategy, "An error occurred connecting to the third party software.");
			}
		}
		catch (Throwable t)
		{
			if (!logging)
			{
				Debug.append("API OUT: " + messageString);
				Debug.append("API IN: " + responseString);
			}
			
			Debug.stackTrace(t);
			DialogUtil.showError("A severe error occurred communicating with the third party software. "
								+ "\n\nLogs have been sent for investigation.");
			
			saveStrategyErrorAndUnsetStrategies(apiStrategy, "A severe error occurred communicating with the third party software.");
		}
		finally
		{
			if (in != null)
			{
				try {in.close();} catch (Throwable t){}
			}
		}
		
		return responseString;
	}
	
	private static String factoryApiMessage(StrategyParms parms, Player player, String messageType)
	{
		if (messageType.equals(MESSAGE_TYPE_XML))
		{
			return factoryXmlApiMessage(parms, player);
		}
		
		return factoryJsonApiMessage(parms, player);
	}
	
	private static String factoryXmlApiMessage(StrategyParms parms, Player player)
	{
		Document document = XmlUtil.factoryNewDocument();
		Element rootElement = document.createElement(ROOT_TAG_API_MESSAGE);
		
		int gameMode = parms.getGameMode();
		int totalCards = parms.getTotalNumberOfCards();
		int jokerQuantity = parms.getJokerQuantity();
		int jokerValue = parms.getJokerValue();
		boolean includeMoons = parms.getIncludeMoons();
		boolean includeStars = parms.getIncludeStars();
		boolean negativeJacks = parms.getNegativeJacks();
		boolean cardReveal = parms.getCardReveal();
		Bid lastBid = parms.getLastBid();
		
		if (gameMode == GameConstants.GAME_MODE_ENTROPY)
		{
			rootElement.setAttribute("GameMode", "Entropy");
		}
		else if (gameMode == GameConstants.GAME_MODE_VECTROPY)
		{
			rootElement.setAttribute("GameMode", "Vectropy");
		}
		
		String[] playerHand = player.getHand();
		Element handElement = document.createElement("PlayerHand");
		int length = playerHand.length;
		for (int i=0; i<length; i++)
		{
			handElement.setAttribute("Card-" + i, playerHand[i]);
		}
		
		rootElement.appendChild(handElement);
		
		//add stuff
		rootElement.setAttribute("TotalCards", "" + totalCards);
		
		if (jokerQuantity > 0)
		{
			rootElement.setAttribute("JokerQuantity", "" + jokerQuantity);
			rootElement.setAttribute("JokerValue", "" + jokerValue);
		}
		
		XmlUtil.setAttributeBoolean(rootElement, "IncludeMoons", includeMoons);
		XmlUtil.setAttributeBoolean(rootElement, "IncludeStars", includeStars);
		XmlUtil.setAttributeBoolean(rootElement, "NegativeJacks", negativeJacks);
		XmlUtil.setAttributeBoolean(rootElement, "ShowCards", cardReveal);
		
		if (cardReveal)
		{
			Element opponentCardsOnShow = document.createElement("OpponentCardsOnShow");
			ArrayList<String> cards = parms.getCardsOnShowFromOpponents();
			for (int i=0; i<cards.size(); i++)
			{
				opponentCardsOnShow.setAttribute("Card-" + i, cards.get(i));
			}
			
			if (cards.size() > 0)
			{
				rootElement.appendChild(opponentCardsOnShow);
			}
			
			Element myCardsAlreadyShowing = document.createElement("PlayerCardsOnShow");
			cards = player.getRevealedCards();
			for (int i=0; i<cards.size(); i++)
			{
				myCardsAlreadyShowing.setAttribute("Card-" + i, cards.get(i));
			}
			
			if (cards.size() > 0)
			{
				rootElement.appendChild(myCardsAlreadyShowing);
			}
		}
		
		if (lastBid != null)
		{
			Element bidElement = document.createElement("LastBid");
			lastBid.populateXmlTag(bidElement);
			rootElement.appendChild(bidElement);
		}
		
		document.appendChild(rootElement);
		return XmlUtil.getStringFromDocument(document);
	}
	
	private static String factoryJsonApiMessage(StrategyParms parms, Player player)
	{
		/*Gson gson = new Gson();
		
		String json = gson.toJson(parms);
		Debug.append("JSON parms: " + json);
		
		return json;*/
		
		
		if (parms == null
		  || player == null)
		{
			//Empty if block to avoid warnings
		}
		
		return null;
	}
	
	private static Bid handleResponse(StrategyParms parms, String responseString)
	{
		Document xmlResponse = XmlUtil.getDocumentFromXmlString(responseString);
		if (xmlResponse == null)
		{
			Debug.append("Received unparsable response via API: " + responseString);
			showMalformedResponseError(responseString);
			return null;
		}
		
		Element root = xmlResponse.getDocumentElement();
		String responseName = root.getNodeName();
		
		if (responseName.equals("Bid"))
		{
			Bid bid = factoryBid(parms, root, responseString);
			String cardToShow = root.getAttribute("CardToShow");
			bid.setCardToReveal(cardToShow);
			return bid;
		}
		else if (responseName.equals("Challenge"))
		{
			return new ChallengeBid();
		}
		else if (responseName.equals("Illegal"))
		{
			return new IllegalBid();
		}
		else
		{
			showMalformedResponseError(responseString);
			return null;
		}
	}
	
	private static Bid factoryBid(StrategyParms parms, Element root, String responseString)
	{
		try
		{
			int gameMode = parms.getGameMode();
			if (gameMode == GameConstants.GAME_MODE_ENTROPY)
			{
				return EntropyBid.factoryFromXmlTag(root);
			}
			else
			{
				return VectropyBid.factoryFromXmlTag(root, parms.getIncludeMoons(), parms.getIncludeStars());
			}
		}
		catch (IOException ioe)
		{
			String message = "The third-party software returned a message that was not successfully parsed."
					 	   + "\n\nMessage: " + responseString
					 	   + "\n\nError: " + ioe.getMessage();
			
			saveStrategyErrorAndUnsetStrategies(apiStrategy, message);
			
			DialogUtil.showError(message);
			return null;
		}
	}
	
	private static void showMalformedResponseError(String response)
	{
		String message = "The third-party software returned an unexpected message type:"
					   + "\n\n" + response;
		
		saveStrategyErrorAndUnsetStrategies(apiStrategy, message);
		
		message += "\n\nRefer to the API documentation to see the responses that are accepted.";
		DialogUtil.showError(message);
	}
	
	private static void initialiseStrategyHashMap()
	{
		HashMap<String, ApiStrategy> temp = new HashMap<>();
		
		Document apiXml = RegistryUtil.getAttributeXml(prefs, PREFERENCES_XML_API_SETTINGS);
		if (apiXml == null)
		{
			hmNameToApiStrategy = temp;
			return;
		}
		
		Element rootElement = apiXml.getDocumentElement();
		NodeList strategyTags = rootElement.getElementsByTagName(PREFERENCES_TAG_API);
		int size = strategyTags.getLength();
		for (int i=0; i<size; i++)
		{
			Element strategyTag = (Element)strategyTags.item(i);
			String name = strategyTag.getAttribute(PREFERENCES_ATTR_API_NAME);
			String error = strategyTag.getAttribute(PREFERENCES_ATTR_ERROR);
			int portNumber = XmlUtil.getAttributeInt(strategyTag, PREFERENCES_ATTR_PORT_NUMNER);
			String messageType = strategyTag.getAttribute(PREFERENCES_ATTR_MESSAGE_TYPE);
			boolean entropy = XmlUtil.getAttributeBoolean(strategyTag, PREFERENCES_ATTR_SUPPORTS_ENTROPY);
			boolean vectropy = XmlUtil.getAttributeBoolean(strategyTag, PREFERENCES_ATTR_SUPPORTS_VECTROPY);
			
			ApiStrategy strategy = new ApiStrategy();
			strategy.setName(name);
			strategy.setError(error);
			strategy.setPortNumber(portNumber);
			strategy.setEntropy(entropy);
			strategy.setVectropy(vectropy);
			strategy.setMessageType(messageType);
			
			temp.put(name, strategy);
		}
		
		hmNameToApiStrategy = temp;
	}
	
	public static ArrayList<ApiStrategy> getApiStrategiesFromPreferences()
	{
		ArrayList<ApiStrategy> strategies = new ArrayList<>();
		if (hmNameToApiStrategy == null)
		{
			initialiseStrategyHashMap();
		}
		
		Iterator<Map.Entry<String, ApiStrategy>> it = hmNameToApiStrategy.entrySet().iterator();
		for (; it.hasNext(); )
		{
			Map.Entry<String, ApiStrategy> entry = it.next();
			ApiStrategy strategy = entry.getValue();
			strategies.add(strategy);
		}
		
		return strategies;
	}
	
	public static ApiStrategy getApiStrategy(String name)
	{
		if (hmNameToApiStrategy == null)
		{
			initialiseStrategyHashMap();
		}
		
		int prefixLength = API_PREFIX.length();
		int totalLength = name.length();
		name = name.substring(prefixLength, totalLength);
		
		return hmNameToApiStrategy.get(name);
	}
	
	public static void saveApiStrategiesToPreferences(ArrayList<ApiStrategy> strategies)
	{
		//Construct a document with any old root element
		Document apiDoc = XmlUtil.factoryNewDocument();
		Element rootElement = apiDoc.createElement("ApiStrategies");
		
		int size = strategies.size();
		for (int i=0; i<size; i++)
		{
			ApiStrategy strategy = strategies.get(i);
			
			Element strategyTag = apiDoc.createElement(PREFERENCES_TAG_API);
			strategyTag.setAttribute(PREFERENCES_ATTR_API_NAME, strategy.getName());
			strategyTag.setAttribute(PREFERENCES_ATTR_ERROR, strategy.getError());
			strategyTag.setAttribute(PREFERENCES_ATTR_MESSAGE_TYPE, strategy.getMessageType());
			strategyTag.setAttribute(PREFERENCES_ATTR_PORT_NUMNER, "" + strategy.getPortNumber());
			XmlUtil.setAttributeBoolean(strategyTag, PREFERENCES_ATTR_SUPPORTS_ENTROPY, strategy.getEntropy());
			XmlUtil.setAttributeBoolean(strategyTag, PREFERENCES_ATTR_SUPPORTS_VECTROPY, strategy.getVectropy());
			
			rootElement.appendChild(strategyTag);
		}
		
		//Append the root, which has all the strategies within it
		apiDoc.appendChild(rootElement);
		
		//Save to the Registry
		RegistryUtil.setAttributeXml(prefs, PREFERENCES_XML_API_SETTINGS, apiDoc);
		
		//Clear the cache
		clearCache();
	}
	
	public static void clearCache()
	{
		hmNameToApiStrategy = null;
	}
	
	public static void saveStrategyErrorAndUnsetStrategies(ApiStrategy strategy, String error)
	{
		//Something has gone wrong, so save the API strategy as disabled
		String name = strategy.getName();
		ArrayList<ApiStrategy> apiStrategies = getApiStrategiesFromPreferences();
		for (int i=0; i<apiStrategies.size(); i++)
		{
			ApiStrategy savedStrategy = apiStrategies.get(i);
			if (savedStrategy.getName().equals(name))
			{
				savedStrategy.setError(error);
			}
		}
		
		saveApiStrategiesToPreferences(apiStrategies);
		
		resetCpuStrategy(PREFERENCES_STRING_OPPONENT_ONE_STRATEGY, name);
		resetCpuStrategy(PREFERENCES_STRING_OPPONENT_TWO_STRATEGY, name);
		resetCpuStrategy(PREFERENCES_STRING_OPPONENT_THREE_STRATEGY, name);
	}
	
	private static void resetCpuStrategy(String prefsKey, String apiName)
	{
		String strategy = prefs.get(prefsKey, "");
		if (strategy.equals("API: " + apiName))
		{
			prefs.put(prefsKey, CpuStrategies.STRATEGY_BASIC);
		}
	}
}
