package util;

public abstract class AbstractDesktopClient extends AbstractClient
											implements CoreRegistry
{
	@Override
	public void init()
	{
		EncryptionUtil.setBase64Interface(new Base64Desktop());
		MessageUtil.generatePublicKey();
	}
}
