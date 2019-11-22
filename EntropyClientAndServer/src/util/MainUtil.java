package util;

import screen.DebugConsole;

public class MainUtil
{
	public static void initialise()
	{
		Debug.initialise(new DebugConsole());
		Debug.setVersionNumber(OnlineConstants.VERSION_NUMBER);
		EncryptionUtil.setBase64Interface(new Base64Desktop());
	}
}
