package bean;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import static utils.CoreGlobals.logger;

public abstract class AbstractDevScreen extends FocusableWindow
{
	public AbstractDevScreen()
	{
		commandBar.setCheatListener(this);
	}
	
	protected final CheatBar commandBar = new CheatBar();
	
	/**
	 * Abstract methods
	 */
	public abstract boolean commandsEnabled();
	public abstract String processCommand(String cmd);
	
	/**
	 * Regular methods
	 */
	public void enableCheatBar(boolean enable)
	{
		commandBar.setEnabled(enable);
	}
	public KeyStroke getKeyStrokeForCommandBar()
	{
		return KeyStroke.getKeyStroke(KeyEvent.VK_SEMICOLON, InputEvent.CTRL_MASK);
	}
	public String processCommandWithTry(String cmd)
	{
		logger.info("commandEntered", "[Command Entered: " + cmd + "]");
		
		String result = "";
		try
		{
			result = processCommand(cmd);
		}
		catch (Throwable t)
		{
			logger.error("commandError", "Error processing command [" + cmd + "]", t);
		}
		
		return result;
	}
}
