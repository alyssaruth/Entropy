package util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

public class EncryptionUtil 
{
	private static final String ALGORITHM_AES_ECB_PKCS5PADDING = "AES/ECB/PKCS5Padding";
	public static Base64Desktop base64Interface = new Base64Desktop();
	
	public static String convertSecretKeyToString(SecretKey secretKey)
	{
		byte[] keyBytes = secretKey.getEncoded();
		return base64Interface.encode(keyBytes);
	}
	
	public static SecretKey reconstructKeyFromString(String keyStr)
	{
		SecretKey secretKey = null;
		
		try
		{
			byte[] encodedKey = base64Interface.decode(keyStr.getBytes());
	    	secretKey = new SecretKeySpec(encodedKey, "AES");
		}
		catch (Throwable t)
		{
			Debug.append("Caught " + t + " trying to reconstruct secret key from String");
		}
		
		return secretKey;
	}
	
	public static String encrypt(String messageString, Key key)
	{
		String encryptedString = null;
		try
		{
			byte[] messageBytes = messageString.getBytes();
			
			Cipher cipher = Cipher.getInstance(ALGORITHM_AES_ECB_PKCS5PADDING);
			cipher.init(Cipher.ENCRYPT_MODE, key);
			byte[] cipherData = cipher.doFinal(messageBytes);
			encryptedString = base64Interface.encode(cipherData);
			
			//Strip out any newline characters
			encryptedString = encryptedString.replaceAll("\n", "");
			encryptedString = encryptedString.replaceAll("\r", "");
		}
		catch (Throwable t)
		{
			Debug.append("Caught " + t + " trying to encrypt message: " + messageString);
		}
		
		if (encryptedString != null)
		{
			encryptedString = encryptedString.intern();
			return encryptedString.intern();
		}
		
		return encryptedString;
	}

	public static String decrypt(String encryptedMessage, Key key)
	{
		String messageString = null;
		try
		{
			byte[] cipherData = base64Interface.decode(encryptedMessage);
			Cipher cipher = Cipher.getInstance(ALGORITHM_AES_ECB_PKCS5PADDING);
			cipher.init(Cipher.DECRYPT_MODE, key);
			byte[] messageBytes = cipher.doFinal(cipherData);
			messageString = new String(messageBytes);
		}
		catch (Throwable ignored)
		{

		}
		
		if (messageString != null)
		{
			return messageString.intern();
		}
		
		return messageString;
	}
}
