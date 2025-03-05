package util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import kotlin.Pair;
import online.screen.EntropyLobby;
import online.util.ResponseHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import screen.ScreenCache;

import static utils.CoreGlobals.logger;

public class MessageSender implements Runnable
{
	private MessageSenderParams messageParms = null;
	private String encryptedResponseString = null;
	private int currentRetries = 0;
	
	/**
	 * Constructor where message params are passed in directly. Used when sending synchronously.
	 */
	public MessageSender(MessageSenderParams messageWrapper)
	{
		this.messageParms = messageWrapper;
	}
	
	/**
	 * Constructor just containing the client. When this runnable gets kicked off, we'll get the next 
	 * messageWrapper to send off of the client.
	 */
	public MessageSender() {}
	
	@Override
	public void run()
	{
		if (messageParms == null)
		{
			this.messageParms = ClientUtil.getNextMessageToSend();
			sendMessage();
		}
		else
		{
			sendMessage();
		}
	}
	
	public String sendMessage()
	{
		int portNumber = MessageUtil.getRandomPortNumber();
		InetAddress address = MessageUtil.factoryInetAddress(MessageUtil.SERVER_IP);
		
		sleepWithCatch();
		
		BufferedReader in = null;
		String messageString = messageParms.getMessageString();
		
		try (Socket socket = new Socket(address, portNumber);
		  PrintWriter out = new PrintWriter(socket.getOutputStream(), true);)
		{
			ClientUtil.setLastSentMessageMillis(System.currentTimeMillis());
			
			int soTimeOut = messageParms.getReadTimeOut();
			socket.setSoTimeout(soTimeOut);
			
			out.write(messageParms.getEncryptedMessageString() + "\n");
			out.flush();
			
			//If we're not expecting a response (e.g. for a disconnect), just return out.
			if (!messageParms.getExpectResponse())
			{
				return null;
			}

			//Read in the response
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			encryptedResponseString = in.readLine();
			if (encryptedResponseString == null)
			{
				return retryOrStackTrace(new Throwable("NULL responseString"));
			}

			//Handle the response if we're not ignoring it
			if (!messageParms.getIgnoreResponse())
			{
				ResponseHandler.handleResponse(messageString, encryptedResponseString);
			}
			
			return encryptedResponseString;
		}
		catch (SocketException | SocketTimeoutException t)
		{
			return retryOrStackTrace(t);
		}
		catch (Throwable t)
		{
			MessageUtil.stackTraceAndDumpMessages(t, messageParms.getCreationStack(), messageString, encryptedResponseString);
			return null;
		}
		finally
		{
			if (in != null)
			{
				try {in.close();} catch (Throwable t){}
			}
		}
	}
	
	private void sleepWithCatch()
	{
		try
		{
			Thread.sleep(messageParms.getMillis());
			if (MessageUtil.millisDelay > 0)
			{
				Thread.sleep(MessageUtil.millisDelay);
			}
		}
		catch (InterruptedException ie)
		{
			Debug.stackTrace(ie);
		}
	}
	
	private String retryOrStackTrace(Throwable t)
	{
		if (!ClientUtil.isOnline())
		{
			return null;
		}
		
		String messageName = messageParms.getMessageName();
		if (t instanceof SocketTimeoutException
		  && messageParms.getAlwaysRetryOnSoTimeout())
		{
			//Always retry, don't bother logging a line
			logger.info("messageFailure", "Had SocketTimeoutException for " + messageName + ", retrying");
			return sendMessage();
		}
		
		int retries = messageParms.getRetries();
		if (currentRetries < retries)
		{
			currentRetries++;
			messageParms.setMillis(0);
			logger.info("messageFailure", t.getMessage() + " for " + messageName + ", will retry (" + currentRetries + "/" + retries + ")");
			return sendMessage();
		}
		else
		{
			logger.error("messageFailure",
					"Failed to send message after " + retries + " retries",
					t,
					new Pair<>("message", messageParms.getMessageString()),
					new Pair<>("previousStack", messageParms.getCreationStack()));
			
			if (ScreenCache.getConnectingDialog().isVisible())
			{
				ScreenCache.getConnectingDialog().dismissDialog();
				DialogUtilNew.showErrorLater("Unable to connect.");
			}
			else
			{
				ScreenCache.get(EntropyLobby.class).exit(true);

				if (!messageParms.getIgnoreResponse())
				{
					DialogUtil.showConnectionLost();
				}
			}
			
			return null;
		}
	}
}
