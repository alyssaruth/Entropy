package online.util;

import online.screen.ConnectingDialog;
import online.screen.EntropyLobby;
import screen.ScreenCache;
import util.*;

public class DesktopEntropyClient extends AbstractClient
{
	@Override
	public void init()
	{
	}
	
	@Override
	public String getUsername()
	{
		return ScreenCache.getEntropyLobby().getUsername();
	}
	
	@Override
	public boolean isOnline()
	{
		return ScreenCache.getEntropyLobby().isVisible();
	}

	@Override
	public void handleResponse(String message, String encryptedResponse) throws Throwable
	{
		ResponseHandler.handleResponse(message, encryptedResponse);
	}

	@Override
	public boolean isCommunicatingWithServer()
	{
		ConnectingDialog dialog = ScreenCache.getConnectingDialog();
		return dialog.isVisible();
	}

	@Override
	public void finishServerCommunication()
	{
		ScreenCache.dismissConnectingDialog();
	}

	@Override
	public void unableToConnect()
	{
		ConnectingDialog dialog = ScreenCache.getConnectingDialog();
		dialog.dismissDialog();
		DialogUtil.showErrorLater("Unable to connect.");
	}

	@Override
	public void connectionLost()
	{
		DialogUtil.showConnectionLost();
	}

	@Override
	public void goOffline()
	{
		EntropyLobby lobby = ScreenCache.getEntropyLobby();
		lobby.exit(true);
	}

	@Override
	public void sendAsyncInSingleThread(MessageSenderParams message)
	{
		addToPendingMessages(message);
		
		MessageSender senderRunnable = new MessageSender(this);
		Thread senderThread = new Thread(senderRunnable, "MessageSender-" + System.currentTimeMillis());
		senderThread.start();
	}
	
	@Override
	public String sendSyncOnDevice(MessageSender runnable)
	{
		return runnable.sendMessage();
	}

	@Override
	public void checkForUpdates()
	{
		ClientGlobals.INSTANCE.getUpdateManager().checkForUpdates(OnlineConstants.ENTROPY_VERSION_NUMBER);
	}
}
