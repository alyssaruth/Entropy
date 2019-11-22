package com.entropy.diordnayportne;

import util.UserErrorHandler;
import android.support.v7.app.AppCompatActivity;

public class AndroidErrorHandler extends AppCompatActivity
								 implements UserErrorHandler
{
	@Override
	public void exceptionCaught(boolean showError)
	{
		if (showError)
		{
			//ToastUtil.showErrorToast(getApplicationContext(), "Oops.");
		}
	}

	@Override
	public void unableToEmailLogs()
	{
		//Fail silently
	}
}
