package screen;

import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.JPanel;

import util.Registry;

public abstract class AbstractPreferencesPanel extends JPanel
											   implements Registry, 
														  ActionListener
{
	protected PreferencesDialog parent = null;
	
	public abstract void initVariables();
	public abstract boolean valid();
	public abstract void savePreferences();
	
	protected void toggleLockedComponent(AbstractButton c, String rewardsStr)
	{
		boolean unlocked = rewards.getBoolean(rewardsStr, false);
		String text = unlocked? c.getText() : "Locked";
		String toolTipText = unlocked? c.getToolTipText() : "";
		
		c.setText(text);
		c.setToolTipText(toolTipText);
		c.setEnabled(unlocked);
	}
	
	public void setParent(PreferencesDialog parent)
	{
		this.parent = parent;
	}
}
