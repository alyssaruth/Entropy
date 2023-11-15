package util;

import screen.DebugConsole;

public class MainUtil
{
	public static void initialise()
	{
		Debug.initialise(new DebugConsole());
		EncryptionUtil.setBase64Interface(new Base64Desktop());
	}
}
