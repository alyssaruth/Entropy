package util;

import game.PlayerAction;
import object.Player;
import org.w3c.dom.Document;
import strategy.ApiStrategy;
import strategy.StrategyParams;
import strategy.StrategyUtilKt;
import utils.CoreGlobals;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.UUID;

public class ApiUtil
{
	private static final InetAddress INET_ADDRESS_LOCALHOST = MessageUtil.factoryInetAddress("localhost");
	
	public static void sendTestMessage(int port, boolean xml)
	{
		if (!xml)
		{
			DialogUtilNew.showError("JSON is currently unsupported.");
			return;
		}
		
		Document xmlDoc = XmlUtil.factorySimpleMessage("ApiTest");
		String messageString = XmlUtil.getStringFromDocument(xmlDoc);
		sendWithCatch(messageString, port, true, null);
	}
	
	public static PlayerAction processApiTurn(StrategyParams parms, ApiStrategy strategy)
	{
		int port = strategy.getPort();
		
		String messageString = "some message TODO";
		
		String responseString = sendWithCatch(messageString, port, parms.getLogging(), strategy.getId());
		if (responseString == null)
		{
			//An error occurred which we'll already have caught.
			return null;
		}
		
		return handleResponse(responseString, strategy.getId());
	}
	
	private static String sendWithCatch(String messageString, int port, boolean logging, UUID id)
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
			int option = DialogUtilNew.showQuestion(question, false);
			if (option == JOptionPane.YES_OPTION)
			{
				sendWithCatch(messageString, port, logging, id);
			}
			else if (id != null)
			{
				StrategyUtilKt.saveStrategyErrorAndUnsetStrategies(id, "An error occurred connecting to the third party software.");
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
			DialogUtilNew.showError("A severe error occurred communicating with the third party software. "
								+ "\n\nLogs have been sent for investigation.");

			if (id != null) {
				StrategyUtilKt.saveStrategyErrorAndUnsetStrategies(id, "A severe error occurred communicating with the third party software.");
			}
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
	
//	private static String factoryXmlApiMessage(StrategyParams parms, Player player)
//	{
//		Document document = XmlUtil.factoryNewDocument();
//		Element rootElement = document.createElement(ROOT_TAG_API_MESSAGE);
//
//		var settings = parms.getSettings();
//		GameMode gameMode = settings.getMode();
//		int totalCards = parms.getCardsInPlay();
//		int jokerQuantity = settings.getJokerQuantity();
//		int jokerValue = settings.getJokerValue();
//		boolean includeMoons = settings.getIncludeMoons();
//		boolean includeStars = settings.getIncludeStars();
//		boolean negativeJacks = settings.getNegativeJacks();
//		boolean cardReveal = settings.getCardReveal();
//		BidAction lastBid = parms.getLastBid();
//
//		rootElement.setAttribute("GameMode", gameMode.name());
//
//		List<String> playerHand = player.getHand();
//		Element handElement = document.createElement("PlayerHand");
//		int length = playerHand.size();
//		for (int i=0; i<length; i++)
//		{
//			handElement.setAttribute("Card-" + i, playerHand.get(i));
//		}
//
//		rootElement.appendChild(handElement);
//
//		//add stuff
//		rootElement.setAttribute("TotalCards", "" + totalCards);
//
//		if (jokerQuantity > 0)
//		{
//			rootElement.setAttribute("JokerQuantity", "" + jokerQuantity);
//			rootElement.setAttribute("JokerValue", "" + jokerValue);
//		}
//
//		XmlUtil.setAttributeBoolean(rootElement, "IncludeMoons", includeMoons);
//		XmlUtil.setAttributeBoolean(rootElement, "IncludeStars", includeStars);
//		XmlUtil.setAttributeBoolean(rootElement, "NegativeJacks", negativeJacks);
//		XmlUtil.setAttributeBoolean(rootElement, "ShowCards", cardReveal);
//
//		if (cardReveal)
//		{
//			Element opponentCardsOnShow = document.createElement("OpponentCardsOnShow");
//			List<String> cards = parms.getOpponentCardsOnShow();
//			for (int i=0; i<cards.size(); i++)
//			{
//				opponentCardsOnShow.setAttribute("Card-" + i, cards.get(i));
//			}
//
//			if (cards.size() > 0)
//			{
//				rootElement.appendChild(opponentCardsOnShow);
//			}
//
//			Element myCardsAlreadyShowing = document.createElement("PlayerCardsOnShow");
//			cards = player.getRevealedCards();
//			for (int i=0; i<cards.size(); i++)
//			{
//				myCardsAlreadyShowing.setAttribute("Card-" + i, cards.get(i));
//			}
//
//			if (cards.size() > 0)
//			{
//				rootElement.appendChild(myCardsAlreadyShowing);
//			}
//		}
//
//		if (lastBid != null)
//		{
//			rootElement.setAttribute("LastBid", lastBid.toJsonString());
//		}
//
//		document.appendChild(rootElement);
//		return XmlUtil.getStringFromDocument(document);
//	}
	
	private static PlayerAction handleResponse(String responseString, UUID id)
	{
		try {
			return CoreGlobals.jsonMapper.readValue(responseString, PlayerAction.class);
		} catch (Exception e) {
			showMalformedResponseError(responseString, id);
			return null;
		}
	}
	
	private static void showMalformedResponseError(String response, UUID id)
	{
		String message = "The third-party software returned an unexpected message type:"
					   + "\n\n" + response;
		
		StrategyUtilKt.saveStrategyErrorAndUnsetStrategies(id, message);
		
		message += "\n\nRefer to the API documentation to see the responses that are accepted.";
		DialogUtilNew.showError(message);
	}
}
