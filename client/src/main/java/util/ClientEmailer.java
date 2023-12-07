package util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.crypto.SecretKey;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;


/**
 * Class to handle sending emails from the client, which is all done via the Entropy Server
 */
public class ClientEmailer
{
	private static final String TEMP_DIRECTORY = System.getProperty("user.dir") + "\\temp";
	private static final String LOG_FILENAME_PREFIX = "DebugLog";
	private static final int SO_TIMEOUT_MILLIS = 60000; //1 minute
	
	private static String obfuscationVersion = "";
	
	public static void sendClientEmail(String subject, String body, boolean containsCodeLines) throws Exception
	{
		sendClientEmail(subject, body, containsCodeLines, new ArrayList<File>());
	}
	public static void sendClientEmail(String subject, String body, boolean containsCodeLines, ArrayList<File> files) throws Exception
	{
		Document xml = factoryClientMailMessage(subject, body, containsCodeLines, files);
		String responseStr = sendEmailMessage(xml);
		if (responseStr == null)
		{
			if (files.isEmpty())
			{
				writeEmailToFile(xml);
			}
			
			throw new Exception("Failed to send email");
		}
	}
	private static void writeEmailToFile(Document xmlMessage)
	{
		String xmlStr = XmlUtil.getStringFromDocument(xmlMessage);
		File tempDir = new File(TEMP_DIRECTORY);
		if (!tempDir.isDirectory()
		  && !tempDir.mkdirs())
		{
			return;
		}
		
		String fileName = LOG_FILENAME_PREFIX + System.currentTimeMillis() + ".txt";
		FileUtil.createNewFile(TEMP_DIRECTORY + "\\" + fileName, xmlStr);
	}
	
	/**
	 * Write out the XML for a client mail message
	 */
	public static Document factoryClientMailMessage(String subject, String body, boolean obfuscated, ArrayList<File> attachments)
	{
		Document message = XmlUtil.factoryNewDocument();
		Element root = message.createElement(XmlConstants.ROOT_TAG_CLIENT_MAIL);
		
		SecretKey symmetricKey = KeyGeneratorUtil.generateSymmetricKey();
		String symmetricKeyString = EncryptionUtil.convertSecretKeyToString(symmetricKey);
		String encryptedKey = EncryptionUtil.encrypt(symmetricKeyString, MessageUtil.publicKey, true);
		
		root.setAttribute("EncryptedKey", encryptedKey);
		root.setAttribute("Subject", EncryptionUtil.encrypt(subject, symmetricKey));
		root.setAttribute("Body", EncryptionUtil.encrypt(body, symmetricKey));
		root.setAttribute("ClientVersion", obfuscationVersion);
		XmlUtil.setAttributeBoolean(root, "Obfuscated", obfuscated);
		
		for (int i=0; i<attachments.size(); i++)
		{
			File attachment = attachments.get(i);
			addAttachment(message, root, symmetricKey, attachment);
		}
		
		message.appendChild(root);
		return message;
	}
	
	private static void addAttachment(Document message, Element root, SecretKey symmetricKey, File attachment)
	{
		Path path = Paths.get(attachment.getPath());
		byte[] bytes = null;
		
		try
		{
			bytes = Files.readAllBytes(path);
		}
		catch (IOException ioe)
		{
			return;
		}
		
		String encodedBytes = EncryptionUtil.base64Interface.encode(bytes);
		String encryptedBytes = EncryptionUtil.encrypt(encodedBytes, symmetricKey);
		String filename = attachment.getName();
		
		Element attachmentElement = message.createElement("Attachment");
		attachmentElement.setAttribute("Filename", EncryptionUtil.encrypt(filename, symmetricKey));
		attachmentElement.setAttribute("Bytes", encryptedBytes);
		
		root.appendChild(attachmentElement);
	}
	
	/**
	 * Check for unsent logs in the temp directory. If we find some, spawn a background thread to try sending these
	 * to the server again.
	 */
	public static void tryToSendUnsentLogs()
	{
		File tempDir = new File(TEMP_DIRECTORY);
		if (!tempDir.isDirectory())
		{
			return;
		}
		
		final File[] files = tempDir.listFiles(new FileFilter()
		{
			@Override
			public boolean accept(File arg0)
			{
				String filename = arg0.getName();
				return filename.startsWith(LOG_FILENAME_PREFIX)
				  && filename.endsWith(".txt");
			}
		});
		
		if (files == null
		  || files.length == 0)
		{
			return;
		}

		Runnable emailRunnable = new Runnable()
		{
			@Override
			public void run()
			{
				for (int i=0; i<files.length; i++)
				{
					File file = files[i];
					resendFromDebugFile(file);
				}
			}
		};
		
		Thread t = new Thread(emailRunnable, "LogResender");
		t.start();
	}
	
	private static void resendFromDebugFile(File file)
	{
		String xmlStr = FileUtil.getFileContentsAsString(file);
		Document xml = XmlUtil.getDocumentFromXmlString(xmlStr);
		String responseStr = sendEmailMessage(xml);
		if (responseStr != null)
		{
			FileUtil.deleteFileIfExists(file.getAbsolutePath());
		}
	}
	
	private static String sendEmailMessage(Document xml)
	{
		//Send with a longer timeout as the server may take some time to send the email. Don't retry forever though.
		return AbstractClient.getInstance().sendSync(xml, false, SO_TIMEOUT_MILLIS, false);
	}
	
	/**
	 * Dartzee is not going to be obfuscated for now, so
	 */
	public static void setObfuscationVersion(String obfuscationVersion)
	{
		ClientEmailer.obfuscationVersion = obfuscationVersion;
	}
}
