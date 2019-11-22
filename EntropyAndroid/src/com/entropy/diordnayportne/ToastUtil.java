package com.entropy.diordnayportne;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil
{
	public static void showErrorToast(Context context, String errorText)
	{
		int duration = Toast.LENGTH_SHORT;

		Toast toast = Toast.makeText(context, errorText, duration);
		toast.show();
	}
}
