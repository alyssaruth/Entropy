
package util;

import http.LegacyConstants;
import kotlin.Pair;
import org.w3c.dom.Document;
import screen.ScreenCache;

import javax.crypto.SecretKey;
import java.net.InetAddress;
import java.net.UnknownHostException;

import static utils.CoreGlobals.logger;

public class MessageUtil implements OnlineConstants
{	
	// public static final String LIVE_IP = "54.149.26.201"; //Live
	// public static final String LAPTOP_IP = "82.3.206.177"; //Laptop (in Leeds)
	public static final String LOCAL_IP = "localhost";
	
	public static final String SERVER_IP = LOCAL_IP;
	
	public static int millisDelay = 0;
	public static SecretKey symmetricKey = EncryptionUtil.reconstructKeyFromString(LegacyConstants.SYMMETRIC_KEY_STR);
	
	public static void sendMessage(Document message, int millis)
	{
		sendMessage(message, millis, 5);
	}
	public static void sendMessage(Document message, int millis, int retries)
	{
		String messageString = XmlUtil.getStringFromDocument(message);
		sendMessage(messageString, millis, retries);
	}
	public static void sendMessage(String messageString, int millis)
	{
		sendMessage(messageString, millis, 5);
	}
	public static void sendMessage(String messageString, int millis, int retries)
	{
		MessageSenderParams params = new MessageSenderParams(messageString, millis, retries);
		sendMessage(params, true);
	}
	public static void sendMessage(MessageSenderParams message, boolean encrypt)
	{
		String messageString = message.getMessageString();
		String encryptedMessageString = messageString;
		if (encrypt)
		{
			encryptedMessageString = EncryptionUtil.encrypt(messageString, symmetricKey);
		}
		
		message.setEncryptedMessageString(encryptedMessageString);
		
		ClientUtil.sendAsyncInSingleThread(message);
	}
	
	public static int getRandomPortNumber()
	{
		return SERVER_PORT_NUMBER;
	}
	
	public static InetAddress factoryInetAddress(String ipAddress)
	{
		InetAddress address = null;
		
		try
		{
			address = InetAddress.getByName(ipAddress);
		}
		catch (UnknownHostException uhe)
		{
			logger.error("addressError", "Failed to create InetAddress", uhe);
		}
		
		return address;
	}
	
	public static void stackTraceAndDumpMessages(Throwable t, Throwable clientStackTrace, String messageStr, 
	  String encryptedResponseStr)
	{
		ScreenCache.dismissConnectingDialog();

		logger.error("messageFailure",
				"Failed to send message: " + messageStr,
				t,
				new Pair<>("message", messageStr),
				new Pair<>("previousStack", clientStackTrace));
		
		String responseStr = null;
		if (symmetricKey != null)
		{
			responseStr = EncryptionUtil.decrypt(encryptedResponseStr, symmetricKey);
		}
		
		if (responseStr != null)
		{
			Debug.append("responseString: " + responseStr);
		}
		else
		{
			Debug.append("Unable to decrypt response");
		}
	}
}