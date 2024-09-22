
package util;

import http.LegacyConstants;
import kotlin.Pair;
import org.w3c.dom.Document;

import javax.crypto.SecretKey;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;

import static utils.InjectedThings.logger;

public class MessageUtil implements OnlineConstants
{	
	// public static final String LIVE_IP = "54.149.26.201"; //Live
	// public static final String LAPTOP_IP = "82.3.206.177"; //Laptop (in Leeds)
	public static final String LOCAL_IP = "localhost";
	
	public static final String SERVER_IP = LOCAL_IP;
	
	public static int millisDelay = 0;
	public static PublicKey publicKey = null;
	public static SecretKey symmetricKey = EncryptionUtil.reconstructKeyFromString(LegacyConstants.SYMMETRIC_KEY_STR);

	private static int cachedPortNumber = SERVER_PORT_NUMBER;
	
	public static void generatePublicKey()
	{
		Debug.append("Reading in public key...");
		
		try (InputStream in = MessageUtil.class.getResourceAsStream("/public.key");
		  ObjectInputStream oin = new ObjectInputStream(new BufferedInputStream(in)))
		{
			BigInteger m = (BigInteger) oin.readObject();
			BigInteger e = (BigInteger) oin.readObject();
			RSAPublicKeySpec keySpec = new RSAPublicKeySpec(m, e);
			KeyFactory fact = KeyFactory.getInstance("RSA");
			MessageUtil.publicKey = fact.generatePublic(keySpec);
			Debug.append("Key read successfully");
		} 
		catch (Throwable t)
		{
			logger.error("keyError", "Unable to read public key - won't be able to communicate with Server.", t);
		} 
	}
	
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
		
		AbstractClient.getInstance().sendAsyncInSingleThread(message);
	}
	
	public static int getRandomPortNumber()
	{
		return cachedPortNumber;
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
		AbstractClient.getInstance().finishServerCommunication();

		logger.error("messageFailure",
				"Failed to send message",
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