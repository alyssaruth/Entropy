package object;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * A special kind of socket that is opened up from a client to a server and left open in order to
 * receive notifications. The socket times out and renews itself automatically every X seconds.
 */
public class NotificationSocket
{
	private Socket socket = null;
	private BufferedOutputStream os = null;
	private OutputStreamWriter osw = null;
	private BufferedReader in = null;
	
	public NotificationSocket(Socket socket, BufferedOutputStream os, OutputStreamWriter osw, BufferedReader in)
	{
		this.socket = socket;
		this.os = os;
		this.osw = osw;
		this.in = in;
	}
	
	public Throwable sendMessageViaSocket(String encryptedMessage)
	{
		try
		{
			osw.write(encryptedMessage + "\n");
			osw.flush();
			return null;
		}
		catch (Throwable t)
		{
			return t;
		}
		finally
		{
			closeResources();
		}
	}
	
	public void closeResources()
	{
		if (osw != null)
		{
			try {osw.close();} catch (Throwable t){}
		}
		
		if (os != null)
		{
			try {os.close();} catch (Throwable t){}
		}
		
		if (socket != null)
		{
			try {socket.close();} catch (Throwable t){}
		}
		
		if (in != null)
		{
			try {in.close();} catch (Throwable t){}
		}
	}
}
