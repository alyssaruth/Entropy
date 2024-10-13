package screen;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import game.GameMode;
import util.ApiUtil;
import util.Debug;
import util.Registry;

/**
 * Created by Alex Burlton (08/10/13)
 */
public class PreferencesDialog extends JDialog
							   implements Registry,
							   			  ActionListener
{
	public PreferencesDialog() 
	{
		try
		{
			getContentPane().setLayout(new BorderLayout(0, 0));
			tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
			getContentPane().add(tabbedPane);
			appearanceScrollPane.getVerticalScrollBar().setUnitIncrement(16);
			tabbedPane.addTab("Gameplay", null, gameplayPanel, null);
			tabbedPane.addTab("Players", null, playersPanel, null);
			appearanceScrollPane.setViewportView(appearancePanel);
			tabbedPane.addTab("Appearance", null, appearanceScrollPane, null);
			tabbedPane.addTab("Miscellaneous", null, miscPanel, null);
			getContentPane().add(okCancelPanel, BorderLayout.SOUTH);
			okCancelPanel.setBorder(BorderFactory.createEmptyBorder(10, 150, 10, 150));
			okCancelPanel.setLayout(new BorderLayout(0, 0));
			okCancelPanel.add(btnOk, BorderLayout.WEST);
			okCancelPanel.add(btnCancel, BorderLayout.EAST);
			
			btnOk.addActionListener(this);
			btnCancel.addActionListener(this);
			
			initVariables();
		}
		catch (Throwable t)
		{
			Debug.stackTrace(t);
		}
	}
	
	private final JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.TOP);
	private final PreferencesPanelGameplay gameplayPanel = new PreferencesPanelGameplay();
	private final JScrollPane appearanceScrollPane = new JScrollPane();
	private final PreferencesPanelAppearance appearancePanel = new PreferencesPanelAppearance();
	private final PreferencesPanelPlayers playersPanel = new PreferencesPanelPlayers();
	private final PreferencesPanelMisc miscPanel = new PreferencesPanelMisc();
	private final JPanel okCancelPanel = new JPanel();
	private final JButton btnOk = new JButton("Ok");
	private final JButton btnCancel = new JButton("Cancel");
	
	public void initVariables()
	{
		ArrayList<AbstractPreferencesPanel> childPanels = getChildPanels();
		for (int i=0; i<childPanels.size(); i++)
		{
			AbstractPreferencesPanel panel = childPanels.get(i);
			panel.setParent(this);
			panel.initVariables();
		}
	}

	private boolean valid()
	{
		ArrayList<AbstractPreferencesPanel> childPanels = getChildPanels();
		for (int i=0; i<childPanels.size(); i++)
		{
			AbstractPreferencesPanel panel = childPanels.get(i);
			if (!panel.valid())
			{
				tabbedPane.setSelectedComponent(panel);
				return false;
			}
		}
		
		return true;
	}
	
	public void gameModeChanged(GameMode gameMode)
	{
		playersPanel.updateStrategySelection(gameMode);
	}
	
	private ArrayList<AbstractPreferencesPanel> getChildPanels()
	{
		ArrayList<AbstractPreferencesPanel> ret = new ArrayList<>();
		
		ret.add(gameplayPanel);
		ret.add(appearancePanel);
		ret.add(playersPanel);
		ret.add(miscPanel);
		
		return ret;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		Object source = arg0.getSource();
		
		if (source == btnOk)
		{
			if (valid())
			{
				gameplayPanel.savePreferences();
				appearancePanel.savePreferences();
				playersPanel.savePreferences();
				miscPanel.savePreferences();
				closeDialog();
			}
		}
		else if (source == btnCancel)
		{
			closeDialog();
		}
		else
		{
			Debug.stackTrace("Unexpected actionPerformed: " + source);
		}
	}

	private void closeDialog()
	{
		ApiUtil.clearCache();
		dispose();
	}
}