package util;

import online.util.ResponseHandler;
import org.w3c.dom.Document;

public class ClientNotificationRunnable implements Runnable
{
	private static final int SO_TIMEOUT_MILLIS = 120000; //2 minutes
	
	private String socketType = null;
	
	public ClientNotificationRunnable(String socketType)
	{
		this.socketType = socketType;
	}
	
	@Override
	public void run()
	{
		Throwable clientStackTrace = new Throwable();
		String messageStr = null;
		String response = null;
		
		while (ClientUtil.isOnline())
		{
			try
			{
				String username = ClientUtil.getUsername();
				Document notificationXml = XmlUtil.factorySimpleMessage(username, socketType);
				messageStr = XmlUtil.getStringFromDocument(notificationXml);
				
				//Send encrypted, with a 1 minute timeout
				response = ClientUtil.sendSync(notificationXml, true, SO_TIMEOUT_MILLIS, true);
				
				//If the thread has stopped due to a d/c, we'll get a null response.
				if (response == null)
				{
					return;
				}

				response = EncryptionUtil.decrypt(response, MessageUtil.symmetricKey);
				if (response == null) {
					throw new Throwable("Failed to decrypt response. Server may not be genuine.");
				}

				if (ClientGlobals.INSTANCE.getWebSocketReceiver().canHandleMessage(response)) {
					ClientGlobals.INSTANCE.getWebSocketReceiver().receiveMessage(response);
				} else {
					ResponseHandler.handleDecryptedResponse(messageStr, response);
				}
			}
			catch (Throwable t)
			{
				MessageUtil.stackTraceAndDumpMessages(t, clientStackTrace, messageStr, response);
			}
		}
	}

}
