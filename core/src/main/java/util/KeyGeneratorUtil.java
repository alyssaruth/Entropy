package util;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import static utils.CoreGlobals.logger;

public class KeyGeneratorUtil 
{
	public static SecretKey generateSymmetricKey()
	{
		SecretKey symmetricKey = null;
		try
		{
			KeyGenerator keyGen = KeyGenerator.getInstance("AES");
			keyGen.init(128);
			symmetricKey = keyGen.generateKey();
		}
		catch (Throwable t)
		{
			logger.error("keyError", "Failed to generate symmetric key", t);
		}
		
	    return symmetricKey;
	}
}
