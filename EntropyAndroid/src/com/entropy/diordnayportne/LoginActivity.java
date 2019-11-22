package com.entropy.diordnayportne;

import online.util.AbstractEntropyClient;
import online.util.KeyGeneratorUtil;
import online.util.MessageUtil;
import online.util.XmlBuilderClient;

import org.w3c.dom.Document;

import util.Debug;
import util.EncryptionUtil;
import util.XmlUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity
{
	private EditText usernameField;
	private EditText passwordField;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		initialiseApp();
		
		usernameField = (EditText)findViewById(R.id.usernameField);
		passwordField = (EditText)findViewById(R.id.passwordField);
	}
	
	private void initialiseApp()
	{
		Debug.initialise(new AndroidOutput());
		Debug.setServer(false);
		Debug.setUserErrorHandler(new AndroidErrorHandler());
		Debug.setLogToSystemOut(true);
		Debug.setVersionNumber(AndroidConstants.VERSION_NUMBER);
		
		AbstractEntropyClient.setInstance(new AndroidEntropyClient());
		EncryptionUtil.setBase64Interface(new Base64Android());
		KeyGeneratorUtil.generatePublicKey();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings)
		{
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * Called when 'Login' is pressed
	 */
	public void doLogin(View view) 
	{
	    String username = "" + usernameField.getText();
	    String password = "" + passwordField.getText();
	    
	    if (validLogin(username, password))
	    {
	    	boolean success = XmlBuilderClient.sendSymmetricKeyRequest();
	    	if (success)
	    	{
	    		String hashedPassword = EncryptionUtil.sha256Hash(password);
	    		Document loginRequest = XmlBuilderClient.factoryConnectionRequest(username, hashedPassword, true);
	    		String requestStr = XmlUtil.getStringFromDocument(loginRequest);
	    		MessageUtil.sendMessage(requestStr, 0);
	    	}
	    }
	}
	
	public void doError(View view) 
	{
		try
		{
			String testStr = "test_string";
			Debug.append("Encrypting " + testStr);
			Debug.append("Using key " + MessageUtil.publicKey);
			
			String result = EncryptionUtil.encrypt(testStr, MessageUtil.publicKey);
			Debug.append("Result = " + result);
		}
		catch (Throwable t)
		{
			Debug.stackTrace(t);
		}
	}
	
	private boolean validLogin(String username, String password)
	{
		if (username.equals(""))
		{
			ToastUtil.showErrorToast(getApplicationContext(), "Enter a username");
			return false;
		}
		
		if (password.equals(""))
		{
			ToastUtil.showErrorToast(getApplicationContext(), "Enter a password");
			return false;
		}
		
		return true;
	}
}
