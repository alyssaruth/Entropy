package screen;

import achievement.Reward;
import screen.preference.PreferencesDialog;
import util.Registry;

import javax.swing.*;
import java.awt.event.ActionListener;

public abstract class AbstractPreferencesPanel extends JPanel
											   implements ActionListener
{
	protected PreferencesDialog parent = null;
	
	public abstract void initVariables();
	public abstract boolean valid();
	public abstract void savePreferences();
	
	protected void toggleLockedComponent(AbstractButton c, Reward requiredReward)
	{
		boolean unlocked = requiredReward.isUnlocked();
		String text = unlocked? c.getText() : "Locked";
		String toolTipText = unlocked? c.getToolTipText() : "Unlock at " + requiredReward.getThreshold() + " achievements";
		
		c.setText(text);
		c.setToolTipText(toolTipText);
		c.setEnabled(unlocked);
	}
	
	public void setParent(PreferencesDialog parent)
	{
		this.parent = parent;
	}
}
