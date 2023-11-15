package util;

import java.security.SecureRandom;

public class PasswordServerUtil 
{
	private static String passwordCharacters = "abcdefghijklmnopqrstuvwxyz0123456789$%!\"&*()#@[]?";
	
	public static String generateRandomPassword()
	{
		SecureRandom random = new SecureRandom();
		int length = passwordCharacters.length();
		
		String s = "";
		for (int i=0; i<EncryptionUtil.MINIMUM_PASSWORD_LENGTH; i++)
		{
			int index = random.nextInt(length);
			String letter = passwordCharacters.substring(index, index+1);
			
			int coinFlip = random.nextInt(2);
			if (coinFlip == 1)
			{
				letter = letter.toUpperCase();
			}
			
			s += letter;
		}
		
		return s;
	}
}
