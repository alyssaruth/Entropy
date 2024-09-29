package util;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import static utils.CoreGlobals.logger;

@Deprecated()
public class DialogUtil 
{
	private static boolean shownConnectionLost = false;
	
	public static void showInfo(String infoText)
	{
		logger.info("infoShown", infoText);
		JOptionPane.showMessageDialog(null, infoText, "Information", JOptionPane.INFORMATION_MESSAGE);
	}
	
	public static void showError(String errorText)
	{
		logger.info("errorShown", errorText);
		JOptionPane.showMessageDialog(null, errorText, "Error", JOptionPane.ERROR_MESSAGE);
	}
	
	public static void showInfoLater(final String infoText)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				showInfo(infoText);
			}
		});
	}
	
	public static void invokeInfoLaterAndWait(final String infoText)
	{
		try
		{
			SwingUtilities.invokeAndWait(new Runnable()
			{
				@Override
				public void run()
				{
					showInfo(infoText);
				}
			});
		}
		catch (Throwable t)
		{
			logger.error("showInfo", "Failed to invokeAndWait for info message: " + infoText);
		}
	}
	
	public static void showErrorLater(final String errorText)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				showError(errorText);
			}
		});
	}
	
	public static int showQuestion(String message, boolean allowCancel)
	{
		int option = JOptionPane.YES_NO_OPTION;
		if (allowCancel)
		{
			option = JOptionPane.YES_NO_CANCEL_OPTION;
		}

		logger.info("questionShown", message);
		int choice = JOptionPane.showConfirmDialog(null, message, "Question", option, JOptionPane.QUESTION_MESSAGE);
		logger.info("questionAnswered", "Answered " + translateOption(choice));
		return choice;
	}

	private static String translateOption(int option)
	{
		switch (option)
		{
			case JOptionPane.YES_OPTION:
				return "Yes";
			case JOptionPane.NO_OPTION:
				return "No";
			case JOptionPane.CANCEL_OPTION:
				return "Cancel";
			default:
				return "Unknown";
		}
	}
	
	public static void showConnectionLost()
	{
		if (!shownConnectionLost)
		{
			showErrorLater("The connection has been lost - EntropyOnline will now exit.");
			shownConnectionLost = true;
		}
	}
}
