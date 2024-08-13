package util;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import javax.crypto.SecretKey;
import javax.mail.MessagingException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import server.EntropyServer;

public class EntropyEmailUtil
{
	public static final String EMAIL_ADDRESS_ENTROPY = "entropyDebug@gmail.com";
	
	private static final String ENTROPY_USERNAME = "entropyDebug";
	private static final String NO_REPLY_USERNAME = "entropynoreply";
	private static final String ENTROPY_PASSWORD = "****";
	private static final String NO_REPLY_PASSWORD = "****";
	
	/**
	 * Send an email via the usual EntropyDebug account
	 */
	public static void sendEmail(String title, String message) throws MessagingException
	{
		sendEmail(title, message, null);
	}
	public static void sendEmail(String title, String message, ArrayList<File> attachments) throws MessagingException
	{
		EmailUtil.sendEmail(title, message, EMAIL_ADDRESS_ENTROPY, ENTROPY_USERNAME, ENTROPY_PASSWORD, attachments);
	}
	
	/**
	 * Send an email from EntropyNoReply, e.g. a password reset
	 */
	public static void sendEmailNoReply(String title, String message, String targetEmail) throws MessagingException
	{
		EmailUtil.sendEmail(title, message, targetEmail, NO_REPLY_USERNAME, NO_REPLY_PASSWORD, null);
	}
	
	/**
	 * Handle a client mail message
	 */
	public static Document handleClientMail(EntropyServer server, Element rootElement)
	{
		String encryptedSymetricKeyStr = rootElement.getAttribute("EncryptedKey");
		String symmetricKeyStr = EncryptionUtil.decrypt(encryptedSymetricKeyStr, server.getPrivateKey(), true);
		SecretKey symmetricKey = EncryptionUtil.reconstructKeyFromString(symmetricKeyStr);
		
		if (symmetricKey == null)
		{
			//Want to know about this. I've seen a client passing up a blank tag for EncryptedKey, and I don't
			//know how that's possible.
			Debug.stackTrace("Failed to reconstruct symmetricKey for EncryptedKey attribute");
			Debug.append("EncryptedKeyStr: " + encryptedSymetricKeyStr);
			Debug.append("SymmetricKeyStr: " + symmetricKeyStr);
		}
		
		String subject = EncryptionUtil.decryptIfPossible(rootElement.getAttribute("Subject"), symmetricKey);
		String body = EncryptionUtil.decryptIfPossible(rootElement.getAttribute("Body"), symmetricKey);
		
		ArrayList<File> attachments = new ArrayList<>();
		NodeList attachmentElements = rootElement.getElementsByTagName("Attachment");
		for (int i=0; i<attachmentElements.getLength(); i++)
		{
			Element attachmentElement = (Element)attachmentElements.item(i);
			File file = reconstructAttachment(attachmentElement, symmetricKey);
			if (file != null)
			{
				attachments.add(file);
			}
		}
		
		try
		{
			sendEmail(subject, body, attachments);
			return XmlBuilderServer.getAcknowledgement();
		}
		catch (MessagingException me)
		{
			Debug.append("Failed to send client mail with " + attachments.size() + " attachments. Subject: " + subject);
			Debug.append("EncryptedKeyStr: " + encryptedSymetricKeyStr);
			Debug.append("SymmetricKeyStr: " + symmetricKeyStr);
			Debug.stackTraceSilently(me);
			return null;
		}
		finally
		{
			for (int i=0; i<attachments.size(); i++)
			{
				File file = attachments.get(i);
				String filePath = file.getAbsolutePath();
				FileUtil.deleteFileIfExists(filePath);
			}
			
		}
	}
	private static File reconstructAttachment(Element attachmentElement, SecretKey symmetricKey)
	{
		String filename = EncryptionUtil.decrypt(attachmentElement.getAttribute("Filename"), symmetricKey);
		
		String filePath = "C:\\EntropyServer\\temp\\" + filename;
		File file = new File(filePath);
		boolean createdNewFile = false;
		
		try
		{
			createdNewFile = file.createNewFile();
		}
		catch (Throwable t)
		{
			Debug.stackTrace(t);
		}
		
		if (!createdNewFile)
		{
			return null;
		}
		
		try (FileOutputStream fileOuputStream = new FileOutputStream(filePath))
		{
			String encryptedBytesStr = attachmentElement.getAttribute("Bytes");
			String encodedBytesStr = EncryptionUtil.decrypt(encryptedBytesStr, symmetricKey);
			byte[] attachmentBytes = EncryptionUtil.base64Interface.decode(encodedBytesStr);
			fileOuputStream.write(attachmentBytes);
		}
		catch (Throwable t)
		{
			Debug.append("Caught " + t + " trying to reconstruct attachment " + filename);
			return null;
		}
		
		return file;
	}
}
