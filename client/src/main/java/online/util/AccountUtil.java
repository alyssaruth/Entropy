package online.util;

import util.DialogUtil;
import util.EncryptionUtil;

public class AccountUtil 
{
	public static boolean validateEmail(String email)
	{
		int atIndex = email.indexOf('@');
		if (atIndex < 0)
		{
			DialogUtil.showError("The email address you have entered is not valid.");
			return false;
		}
		
		int dotIndex = email.indexOf('.', atIndex);
		if (dotIndex < 0 || dotIndex == atIndex+1)
		{
			DialogUtil.showError("The email address you have entered is not valid.");
			return false;
		}
		
//		try
//		{
//			InternetAddress.parse(email, true);
//		}
//		catch (Throwable t)
//		{
//			Debug.stackTraceSilently(t);
//			DialogUtil.showError("The email address you have entered is not valid.");
//			return false;
//		}
		
		return true;
	}
	
	public static boolean passwordsMatch(char[] passwordOne, char[] passwordTwo)
	{
		int lengthOne = passwordOne.length;
		int lengthTwo = passwordTwo.length;
		
		if (lengthOne != lengthTwo)
		{
			return false;
		}
		
		for (int i=0; i<lengthOne; i++)
		{
			if (passwordOne[i] != passwordTwo[i])
			{
				return false;
			}
		}
		
		return true;
	}
	
	public static boolean passwordStrongEnough(char[] password)
	{
		int length = password.length;
		if (length < EncryptionUtil.MINIMUM_PASSWORD_LENGTH)
		{
			DialogUtil.showError("Your password must be at least " + EncryptionUtil.MINIMUM_PASSWORD_LENGTH + " characters long.");
			return false;
		}
		
		return true;
	}
}
