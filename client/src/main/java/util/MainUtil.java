package util;

import screen.DebugConsole;

public class MainUtil
{
	public static void initialise()
	{
		Debug.initialise(new DebugConsole());
		Debug.setVersionNumber(OnlineConstants.ENTROPY_VERSION_NUMBER);
		EncryptionUtil.setBase64Interface(new Base64Desktop());
	}
}
