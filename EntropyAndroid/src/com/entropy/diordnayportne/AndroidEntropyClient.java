package com.entropy.diordnayportne;

import online.util.AbstractEntropyClient;
import online.util.MessageSender;
import online.util.MessageUtil;
import util.Debug;
import util.EncryptionUtil;
import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;

public class AndroidEntropyClient extends AbstractEntropyClient
{
	@Override
	public boolean isOnline()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void handleResponse(String message, String encryptedResponse)
			throws Throwable
	{
		String resp = EncryptionUtil.decrypt(encryptedResponse, MessageUtil.symmetricKey);
		Debug.append("Response: " + resp);
	}

	@Override
	public boolean isCommunicatingWithServer()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void finishServerCommunication()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unableToConnect()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void connectionLost()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void goOffline()
	{
		// TODO Auto-generated method stub
		
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public void sendAsync(MessageSender runnable)
	{
		//TODO - think about compatibility
		AsyncTask.execute(runnable);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public String sendSyncOnDevice(MessageSender runnable)
	{
		AsyncTask<MessageSender, Object, String> asyncTask = new AsyncTask<MessageSender, Object, String>()
		{
			@Override
			protected String doInBackground(MessageSender... params)
			{
				MessageSender sender = params[0];
				return sender.sendMessage();
			}
		};
		
		AsyncTask<MessageSender, Object, String> result = asyncTask.execute(runnable, null);
		String resultStr = null;
		try
		{
			resultStr = result.get();
		}
		catch (Throwable t)
		{
			Debug.stackTrace(t);
		}
		
		return resultStr;
	}
	
}
