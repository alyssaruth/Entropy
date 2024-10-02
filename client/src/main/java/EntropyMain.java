import java.net.ServerSocket;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.WindowConstants;

import logging.LoggerUncaughtExceptionHandler;
import online.util.DesktopEntropyClient;
import screen.MainScreen;
import screen.ScreenCache;
import util.*;

import static utils.CoreGlobals.logger;

public class EntropyMain implements Registry
{
	private static final int BIND_PORT_NUMBER = 1321;
	
	private static ServerSocket instanceBind = null;
	
	public static void main(String[] args)
	{
		//Initialise interfaces etc
		Debug.initialise(new DebugOutputSystemOut());
		Thread.setDefaultUncaughtExceptionHandler(new LoggerUncaughtExceptionHandler());
		MainUtilKt.configureLogging();
		AbstractClient.setInstance(new DesktopEntropyClient());

		//Dev mode
		AbstractClient.parseProgramArguments(args);

		setLookAndFeel();

		if (AbstractClient.devMode)
		{
			setInstanceNumber();
		}
		else if (!bindOnPort(BIND_PORT_NUMBER))
		{
			logger.info("instanceCheck", "Multiple instances disabled in non-dev mode, exiting");
			DialogUtil.showError("Entropy is already running.");
			System.exit(0);
			return;
		}

		checkForUpdatesIfRequired();

		MainScreen application = ScreenCache.getMainScreen();
		application.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		application.setSize(1050, 600);
		application.setVisible(true);
		application.setLocationRelativeTo(null);
		application.setResizable(false);
		application.onStart();
	}

	private static void setLookAndFeel()
	{
		logger.info("laf.init", "Initialising Look & Feel - Operating System: " + AbstractClient.operatingSystem);
		
		String lookAndFeel = null;
		try 
		{
			lookAndFeel = prefs.get(PREFERENCES_STRING_LOOK_AND_FEEL, "Metal");
			if (AbstractClient.isAppleOs()
			  && lookAndFeel.equals("Metal"))
			{
				//This doesn't seem to work on macs...
				return;
			}
			
		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) 
		    {
		        if (lookAndFeel.equals(info.getName())) 
		        {
		            UIManager.setLookAndFeel(info.getClassName());
		            break;
		        }
		    }
		} 
		catch (Throwable e) 
		{
			logger.warn("laf.failed", "Failed to load LookAndFeel " + lookAndFeel + ". Caught " + e);
		    DialogUtil.showError("Failed to load Look & Feel '" + lookAndFeel + "'. \nEntropy will use the default instead.");
		    prefs.put(PREFERENCES_STRING_LOOK_AND_FEEL, "Metal");
		}
	}
	
	private static void setInstanceNumber()
	{
		int startingPortNumber = BIND_PORT_NUMBER;
		boolean boundSuccessfully = bindOnPort(startingPortNumber);

		while (!boundSuccessfully)
		{
			startingPortNumber++;
			AbstractClient.instanceNumber++;
			boundSuccessfully = bindOnPort(startingPortNumber);
		}

		logger.info("instanceCheck", "I am instance number " + AbstractClient.instanceNumber);
	}
	
	private static boolean bindOnPort(int portNumber)
	{
		try
		{
			instanceBind = new ServerSocket(portNumber);
		}
		catch (Throwable t)
		{
			logger.info("instanceCheck", "Caught " + t + " trying to bind on port " + portNumber);
			return false;
		}
		
		return true;
	}
	
	private static void checkForUpdatesIfRequired()
	{
		boolean checkForUpdates = Registry.prefs.getBoolean(PREFERENCES_BOOLEAN_CHECK_FOR_UPDATES, true);
		if (!checkForUpdates
		  || AbstractClient.devMode)
		{
			logger.info("updateCheck", "Not checking for updates as preference is disabled or I'm in dev mode");
			return;
		}
		
		AbstractClient.getInstance().checkForUpdatesIfRequired();
	}
}
