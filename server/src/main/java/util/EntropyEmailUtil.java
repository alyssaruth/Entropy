package util;

import javax.mail.MessagingException;

public class EntropyEmailUtil
{
	private static final String NO_REPLY_USERNAME = "entropynoreply";
	private static final String NO_REPLY_PASSWORD = "****";

	
	/**
	 * Send an email from EntropyNoReply, e.g. a password reset
	 */
	public static void sendEmailNoReply(String title, String message, String targetEmail) throws MessagingException
	{
		EmailUtil.sendEmail(title, message, targetEmail, NO_REPLY_USERNAME, NO_REPLY_PASSWORD, null);
	}
}
