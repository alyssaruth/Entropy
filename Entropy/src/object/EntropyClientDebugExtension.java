package object;

import util.AchievementsUtil;
import util.DebugExtension;
import util.DialogUtil;
import util.ClientEmailer;

public class EntropyClientDebugExtension implements DebugExtension
{
	@Override
	public void exceptionCaught(boolean showError) 
	{
		AchievementsUtil.unlockBlueScreenOfDeath();
		if (showError)
		{
			DialogUtil.showErrorLater("A serious error has occurred. Logs will now be sent for investigation."
					+ "\nThere is no need to send a bug report.");
		}
	}

	@Override
	public void unableToEmailLogs() 
	{
		DialogUtil.showErrorLater("An error occurred e-mailing logs. Please submit a bug report manually.");
	}

	@Override
	public void sendEmail(String title, String message) throws Exception
	{
		ClientEmailer.sendClientEmail(title, message, true);
	}
}
