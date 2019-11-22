package util;

public class ServerDebugExtension implements DebugExtension
{
	@Override
	public void exceptionCaught(boolean showError)
	{
		//Do nothing
	}

	@Override
	public void unableToEmailLogs()
	{
		//Do nothing
	}

	@Override
	public void sendEmail(String title, String message) throws Exception
	{
		EntropyEmailUtil.sendEmail(title, message);
	}

}
