
package screen;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import object.Bid;
import object.Player;
import online.util.ResponseHandler;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import util.CpuStrategies;
import util.Debug;
import util.StrategyParms;
import util.XmlUtil;

public class ApiTestHarness implements UncaughtExceptionHandler
{
	private static final boolean LOGGING = true;
	
	/*public static void main(String[] args)
	{
		ApiTestHarness testHarness = new ApiTestHarness();
		Thread.setDefaultUncaughtExceptionHandler(testHarness);
		
		Debug.initialise(new DebugConsole());
		Debug.setSendingEmails(false);
		Debug.setLogToSystemOut(true);
		
		testHarness.init();
	}*/
	
	public void init()
	{
		Thread listenerThread = new Thread(new ListenerRunnable());
		listenerThread.setName("Listener-" + 1153);
		listenerThread.start();
	}
	
	private static class ListenerRunnable implements Runnable
	{
		@Override
		public void run()
		{
			try (ServerSocket serverSocket = new ServerSocket(1153))
			{
				Debug.append("Started listening on port 1153");
				
				while (!serverSocket.isClosed())
				{
					try (Socket clientSocket = serverSocket.accept();
					  BufferedOutputStream os = new BufferedOutputStream(clientSocket.getOutputStream());
					  OutputStreamWriter osw = new OutputStreamWriter(os, "US-ASCII");
					  BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));)
					{
						String apiMessage = in.readLine();
						Debug.append("IN: " + apiMessage, LOGGING);
						
						Document xmlMessage = XmlUtil.getDocumentFromXmlString(apiMessage);
						if (xmlMessage == null)
						{
							Debug.stackTrace("Failed to parse message");
						}
						else
						{
							Element root = xmlMessage.getDocumentElement();

							StrategyParms parms = StrategyParms.factoryFromXml(root);
							parms.setLogging(false);
							
							Player opponent = new Player(-1, null);
							opponent.setStrategy("Easy");
							
							NodeList handElements = root.getElementsByTagName("PlayerHand");
							if (handElements.getLength() > 0)
							{
								Element handElement = (Element)handElements.item(0);
								String[] playerHand = ResponseHandler.getHandFromElement(handElement);
								opponent.setHand(playerHand);
							}
							
							ArrayList<String> cardsOnShow = XmlUtil.getListFromElement(root, "PlayerCardsOnShow", "Card");
							opponent.setRevealedCards(cardsOnShow);
							
							Bid bid = CpuStrategies.processOpponentTurn(parms, opponent);
							
							Document response = factoryResponse(bid);
							String responseStr = XmlUtil.getStringFromDocument(response);
							
							osw.write(responseStr);
							osw.flush();
							
							Debug.append("OUT: " + responseStr, LOGGING);
						}
					}	
				} 
			}
			catch (Throwable t)
			{
				Debug.stackTrace(t);
			}
		}
	}
	
	private static Document factoryResponse(Bid bid)
	{
		if (bid.isChallenge())
		{
			return XmlUtil.factorySimpleMessage("Challenge");
		}
		
		if (bid.isIllegal())
		{
			return XmlUtil.factorySimpleMessage("Illegal");
		}
		
		Document response = XmlUtil.factoryNewDocument();
		Element rootElement = response.createElement("Bid");
		bid.populateXmlTag(rootElement);
		
		String cardToReveal = bid.getCardToReveal();
		if (!cardToReveal.isEmpty())
		{
			rootElement.setAttribute("CardToShow", cardToReveal);
		}
		
		response.appendChild(rootElement);
		return response;
	}

	@Override
	public void uncaughtException(Thread arg0, Throwable arg1)
	{
		Debug.append("UNCAUGHT EXCEPTION");
		Debug.stackTrace(arg1);
	}
}
