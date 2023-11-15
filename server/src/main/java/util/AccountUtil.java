package util;


public class AccountUtil implements ServerRegistry
{
	public static String createAccount(String username, String passwordHash, String email)
	{
		if (usernameExists(username))
		{
			return "An account already exists with that name.";
		}
		else if (username.equalsIgnoreCase("admin"))
		{
			return "You cannot have an account called " + username;
		}
		else if (username.length() > 10)
		{
			return "The username specified is too long.";
		}
		else if (emailExists(email))
		{
			return "An account with that email already exists.";
		}
		
		if (email != null && !email.isEmpty())
		{
			try
			{
				String title = "Welcome to EntropyOnline!";
				String body = "Hi " + username + "! This is a quick test message to check that the email you entered is valid.";
				body += "\nIf you forget your password, the 'Reset Password' option will generate a new one";
				body += " and send it to this address. \n\nEnjoy the game!";
				// EntropyEmailUtil.sendEmailNoReply(title, body, email);
				emailData.put(username, email);
			}
			catch (Throwable t)
			{
				Debug.append("Failed to send verification email to " + email + ". Caught " + t);
				return "Unable to send confirmation email to " + email + ".\n\nCheck that the email you entered is valid.";
			}
		}
		
		accountData.put(username, passwordHash);
		Debug.append("Registered new account: " + username);
		return "";
	}
	
	public static boolean usernameExists(String username)
	{
		String existingPassword = accountData.get(username, "");
		
		return !existingPassword.isEmpty();
	}
	
	private static boolean emailExists(String email)
	{
		try
		{
			String[] keys = emailData.keys();
			if (keys == null)
			{
				return false;
			}
			
			for (int i=0; i<keys.length; i++)
			{
				String key = keys[i];
				String value = emailData.get(key, "");
				if (!value.isEmpty()
				  && value.equals(email))
				{
					return true;
				}
			}
			
			return false;
		}
		catch (Throwable t)
		{
			Debug.stackTrace(t);
			return false;
		}
	}
	
	public static boolean passwordIsCorrect(String username, String passwordHash)
	{
		String existingPassword = accountData.get(username, "");
		
		return passwordHash.equals(existingPassword);
	}
	
	public static boolean resetPassword(String username, String email)
	{
		String tempPassword = PasswordServerUtil.generateRandomPassword();
		
		try
		{
			String body = "Your new password is " + tempPassword;
			body += "\nThis is a one-time password only - you will be prompted to change it when you next log in.";
			EntropyEmailUtil.sendEmailNoReply("Password reset for EntropyOnline", body, email);
		}
		catch (Throwable t)
		{
			Debug.append("Caught " + t + " trying to send reset password email to " + email);
			return false;
		}
		
		String hashedPassword = EncryptionUtil.sha256Hash(tempPassword);
		accountData.put(username, hashedPassword);
		accountData.putBoolean(username + "ForceReset", true);
		
		Debug.append("Reset password for " + username);
		return true;
	}
	
	public static String changePassword(String username, String oldPass, String newPass)
	{
		if (!passwordIsCorrect(username, oldPass))
		{
			return "Incorrect password.";
		}
		
		accountData.put(username, newPass);
		accountData.remove(username + "ForceReset");
		
		Debug.append(username + " has changed their password.");
		return "";
	}
	
	public static boolean passwordNeedsToBeChanged(String username)
	{
		return accountData.getBoolean(username + "ForceReset", false);
	}
	
	public static String getEmailForUser(String username)
	{
		return emailData.get(username, "");
	}
	
	public static String changeEmail(String username, String newEmail, boolean sendTest)
	{
		if (!newEmail.isEmpty() && emailExists(newEmail))
		{
			return "An account with that email already exists.";
		}
		
		if (sendTest)
		{
			try
			{
				String title = "Your email has been changed";
				String body = "Hi " + username + "! If you are receiving this message then your new email has been set up correctly.";
				EntropyEmailUtil.sendEmailNoReply(title, body, newEmail);
			}
			catch (Throwable t)
			{
				Debug.append("Failed to send test email to " + newEmail + ". Caught " + t);
				return "Unable to send test email to " + newEmail + ".\n\nCheck that the email you entered is valid.";
			}
		}
		
		emailData.put(username, newEmail);
		return "";
	}
	
	public static boolean isAdmin(String username)
	{
		return adminData.getBoolean(username, false);
	}
}
