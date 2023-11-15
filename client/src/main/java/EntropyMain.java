import java.net.ServerSocket;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.WindowConstants;

import object.EntropyClientDebugExtension;
import online.util.DesktopEntropyClient;
import screen.MainScreen;
import screen.ScreenCache;
import util.AbstractClient;
import util.Debug;
import util.DebugUncaughtExceptionHandler;
import util.DialogUtil;
import util.EncryptionUtil;
import util.OnlineConstants;
import util.Registry;

public class EntropyMain implements Registry
{
	private static final int BIND_PORT_NUMBER = 1321;
	
	private static ServerSocket instanceBind = null;
	
	public static void main(String[] args)
	{
		try
		{
			//Initialise interfaces etc
			Debug.initialise(ScreenCache.getDebugConsole());
			AbstractClient.setInstance(new DesktopEntropyClient());
			
			//Dev mode
			AbstractClient.parseProgramArguments(args);
			
			//Set Debug variables
			Debug.setProductDesc("Entropy " + OnlineConstants.ENTROPY_VERSION_NUMBER);
			Debug.setDebugExtension(new EntropyClientDebugExtension());
			Debug.setLogToSystemOut(AbstractClient.devMode);
			
			setLookAndFeel();
			
			if (AbstractClient.devMode)
			{
				setInstanceNumber();
			}
			else if (!bindOnPort(BIND_PORT_NUMBER))
			{
				Debug.append("Multiple instances disabled in non-dev mode, exiting");
				DialogUtil.showError("Entropy is already running.");
				System.exit(0);
				return;
			}

			EncryptionUtil.failedDecryptionLogging = true;
			
			checkForUpdatesIfRequired();
			
			MainScreen application = ScreenCache.getMainScreen();
			application.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			application.setSize(1050, 600);
			application.setVisible(true);
			application.setLocationRelativeTo(null);
			application.setResizable(false);
			
			Thread.setDefaultUncaughtExceptionHandler(new DebugUncaughtExceptionHandler());
			application.onStart();
		}
		catch (Throwable t)
		{
			Debug.stackTrace(t);
		}
	}
	
	private static void setLookAndFeel()
	{
		AbstractClient.setOs();
		Debug.append("Initialising Look & Feel - Operating System: " + AbstractClient.operatingSystem);
		
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
		    Debug.append("Failed to load LookAndFeel " + lookAndFeel + ". Caught " + e);
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
		
		Debug.append("I am instance number " + AbstractClient.instanceNumber);
	}
	
	private static boolean bindOnPort(int portNumber)
	{
		try
		{
			instanceBind = new ServerSocket(portNumber);
		}
		catch (Throwable t)
		{
			Debug.append("Caught " + t + " trying to bind on port " + portNumber);
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
			Debug.append("Not checking for updates as preference is disabled or I'm in dev mode");
			return;
		}
		
		AbstractClient.getInstance().checkForUpdatesIfRequired();
	}
}
