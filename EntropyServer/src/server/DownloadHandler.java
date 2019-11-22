package server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

import object.ServerRunnable;
import object.ServerThread;
import object.UserConnection;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import util.Debug;
import util.FileUtil;
import util.OnlineConstants;
import util.XmlBuilderServer;
import util.XmlConstants;
import util.XmlUtil;

public class DownloadHandler implements XmlConstants
{
	private static final String FILE_PATH_JARS = "C:\\EntropyServer\\";
	
	public static boolean isDownloadMessage(String name)
	{
		return name.equals(ROOT_TAG_CRC_CHECK);
	}
	
	/**
	 * CRC Check message
	 */
	public static Document processMessage(Document message, EntropyServer server)
	{
		Element rootElement = message.getDocumentElement();
		String fileCrcFromClient = rootElement.getAttribute("FileCrc");
		String filename = rootElement.getAttribute("FileName");
		
		//Handle old clients without the new CRC message
		if (filename.isEmpty())
		{
			filename = OnlineConstants.FILE_NAME_ENTROPY_JAR;
		}
		
		String version = server.getClientVersion(filename);
		return doCrcCheck(fileCrcFromClient, filename, version);
	}
	
	private static Document doCrcCheck(String fileCrcFromClient, String filename, String version)
	{
		String serverCrc = FileUtil.getMd5Crc(FILE_PATH_JARS + filename);
		if (fileCrcFromClient.equals(serverCrc))
		{
			return XmlUtil.factorySimpleMessage(RESPONSE_TAG_NO_UPDATES);
		}
		
		long fileSize = getLatestVersionFileSize(filename);
		return XmlBuilderServer.getUpdateAvailableResponse(fileSize, version);
	}
	
	public static long getLatestVersionFileSize(String filename)
	{
		return FileUtil.getFileSize(FILE_PATH_JARS + filename);
	}
	
	/**
	 * Actual Download
	 */
	public static void handleDownload(final EntropyServer server, final Socket socket, final String filename)
	{
		InetAddress address = socket.getInetAddress();
		final String ipAddress = address.getHostAddress();
		
		ServerRunnable downloadRunnable = new ServerRunnable()
		{
			@Override
			public void run() 
			{
				doDownload(socket, server, filename);
			}

			@Override
			public String getDetails()
			{
				return "Executing download for " + ipAddress;
			}

			@Override
			public UserConnection getUserConnection(){return null;}
		};
		
		ServerThread t = new ServerThread(downloadRunnable, "DownloadThread " + ipAddress);
		t.start();
	}
	
	private static void doDownload(Socket socket, EntropyServer server, String filename)
	{
		server.incrementFunctionsReceived();
		server.incrementFunctionsReceivedForMessage("DownloadRequest");
		
		File f = new File(FILE_PATH_JARS + filename);
		try (BufferedOutputStream outStream = new BufferedOutputStream(socket.getOutputStream());
			 BufferedInputStream inStream = new BufferedInputStream(new FileInputStream(f));)
		{
			byte[] buffer = new byte[4096];
			int count = 0;
			while ((count = inStream.read(buffer)) > 0)
			{
				outStream.write(buffer, 0, count);
			}
		}
		catch (SocketException se)
		{
			Debug.append("Caught " + se + " for JAR download.");
		}
		catch (Throwable t)
		{
			Debug.stackTrace(t);
		}
		finally
		{
			server.incrementFunctionsHandled();
			server.incrementFunctionsHandledForMessage("DownloadRequest");
			
			if (socket != null)
			{
				try {socket.close();} catch (Throwable t) {}
			}
		}
	}
}
