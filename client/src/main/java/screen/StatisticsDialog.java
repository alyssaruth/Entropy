package screen;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Panel;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.text.NumberFormat;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;

import achievement.AchievementSetting;
import util.DateUtil;
import util.Debug;
import util.Registry;

import static util.ClientGlobals.achievementStore;

@SuppressWarnings("serial")
public class StatisticsDialog extends JDialog
							  implements Registry
{
	private Timer timer = new Timer("Timer-Statistics");
	
	private boolean vectropyUnlocked = false;
	
	public StatisticsDialog() 
	{
		try
		{
			vectropyUnlocked = rewards.getBoolean(REWARDS_BOOLEAN_VECTROPY, false);
			getContentPane().setLayout(new BorderLayout(0, 0));
			
			panel_1 = new Panel();
			getContentPane().add(panel_1, BorderLayout.SOUTH);
			
			Panel panel_2 = new Panel();
			getContentPane().add(panel_2);
			panel_2.setLayout(new BorderLayout(0, 0));
			
			JPanel panel = new JPanel();
			panel_2.add(panel, BorderLayout.CENTER);
			FlowLayout flowLayout = (FlowLayout) panel.getLayout();
			flowLayout.setHgap(10);
			panel.setAlignmentY(Component.TOP_ALIGNMENT);
			entropyStats.setPreferredSize(new Dimension(120, 100));
			entropyStats.setOpaque(false);
			entropyStats.setBorder(BorderFactory.createEmptyBorder());
			entropyStats.setBackground(new Color(0,0,0,0));
			entropyStats.setEditable(false);
			entropyStats.setFocusable(false);
			panel.add(entropyStats);
			
			JSeparator separator = new JSeparator();
			separator.setOrientation(SwingConstants.VERTICAL);
			separator.setPreferredSize(new Dimension(2, 100));
			panel.add(separator);
			vectropyStats.setPreferredSize(new Dimension(120, 100));
			vectropyStats.setOpaque(false);
			vectropyStats.setBorder(BorderFactory.createEmptyBorder());
			vectropyStats.setBackground(new Color(0,0,0,0));
			vectropyStats.setEditable(false);
			vectropyStats.setFocusable(false);
			
			JSeparator separator_1 = new JSeparator();
			separator_1.setPreferredSize(new Dimension(2, 100));
			separator_1.setOrientation(SwingConstants.VERTICAL);
			overallStats.setPreferredSize(new Dimension(120, 100));
			overallStats.setOpaque(false);
			overallStats.setBorder(BorderFactory.createEmptyBorder());
			overallStats.setBackground(new Color(0,0,0,0));
			overallStats.setEditable(false);
			overallStats.setFocusable(false);
			separator_2.setPreferredSize(new Dimension(2, 100));
			separator_2.setOrientation(SwingConstants.VERTICAL);
			streaksField.setText("");
			streaksField.setPreferredSize(new Dimension(160, 100));
			streaksField.setFocusable(false);
			streaksField.setEditable(false);
			streaksField.setOpaque(false);
			streaksField.setBorder(BorderFactory.createEmptyBorder());
			streaksField.setBackground(new Color(0,0,0,0));
			panel_2.add(timePlayedField, BorderLayout.SOUTH);
			timePlayedField.setHorizontalAlignment(SwingConstants.CENTER);
			timePlayedField.setText("");
			timePlayedField.setFocusable(false);
			timePlayedField.setBackground(null);
			timePlayedField.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
			
			if (vectropyUnlocked)
			{
				panel.add(vectropyStats);
				panel.add(separator_1);
				panel.add(overallStats);
				panel.add(separator_2);
				setSize(640, 200);
			}
			else
			{
				setSize(340, 200);
			}
			
			panel.add(streaksField);

			initVariables();
			setUpListeners();
		}
		catch (Throwable t)
		{
			Debug.stackTrace(t);
		}
	}
	
	private JButton btnOk = new JButton("Ok");
	private JTextPane entropyStats = new JTextPane();
	private JTextPane vectropyStats = new JTextPane();
	private JTextPane overallStats = new JTextPane();
	private JLabel timePlayedField = new JLabel();
	private Panel panel_1;
	private final JSeparator separator_2 = new JSeparator();
	private final JTextPane streaksField = new JTextPane();
	
	private void initVariables()
	{
		setStatisticsText();
		timer.cancel();
		timer = new Timer("Timer-Statistics");
		timer.schedule(new RefreshTask(), 1000);
	}
	
	private void setStatisticsText()
	{
		setEntropyStatisticsText();
		setVectropyStatisticsText();
		setOverallStatisticsText();
		setStreaksText();
		setTimePlayedText();
	}
	
	private void setEntropyStatisticsText()
	{
		NumberFormat percentFormat = NumberFormat.getPercentInstance();
		percentFormat.setMinimumFractionDigits(1);
		
		double eGamesPlayed = achievementStore.get(AchievementSetting.EntropyGamesPlayed);
		double eGamesWon = achievementStore.get(AchievementSetting.EntropyGamesWon);
		
		String eWinRate = "N/A";
		if (eGamesPlayed > 0)
		{
			eWinRate = percentFormat.format(eGamesWon/eGamesPlayed);
		}
		
		StringBuffer sb = new StringBuffer();

		if (vectropyUnlocked)
		{
			sb.append("Entropy:");
		}
		else
		{
			sb.append("Games:");
		}
		sb.append("\n\nGames Played: ");
		sb.append((int)eGamesPlayed);
		sb.append("\nGames Won: ");
		sb.append((int)eGamesWon);
		sb.append("\nWin Rate: ");
		sb.append(eWinRate);
		
		String statsString =  sb.toString();
		entropyStats.setText(statsString);
	}
	
	private void setVectropyStatisticsText()
	{
		NumberFormat percentFormat = NumberFormat.getPercentInstance();
		percentFormat.setMinimumFractionDigits(1);

		double vGamesPlayed = achievementStore.get(AchievementSetting.VectropyGamesPlayed);
		double vGamesWon = achievementStore.get(AchievementSetting.VectropyGamesWon);

		String vWinRate = "N/A";
		if (vGamesPlayed > 0)
		{
			vWinRate = percentFormat.format(vGamesWon/vGamesPlayed);
		}

		StringBuffer sb = new StringBuffer();
		
		sb.append("Vectropy:");
		sb.append("\n\nGames Played: ");
		sb.append((int)vGamesPlayed);
		sb.append("\nGames Won: ");
		sb.append((int)vGamesWon);
		sb.append("\nWin Rate: ");
		sb.append(vWinRate);
		
		String statsString =  sb.toString();
		vectropyStats.setText(statsString);
	}
	
	private void setOverallStatisticsText()
	{
		NumberFormat percentFormat = NumberFormat.getPercentInstance();
		percentFormat.setMinimumFractionDigits(1);

		double eGamesPlayed = achievementStore.get(AchievementSetting.EntropyGamesPlayed);
		double eGamesWon = achievementStore.get(AchievementSetting.EntropyGamesWon);
		double vGamesPlayed = achievementStore.get(AchievementSetting.VectropyGamesPlayed);
		double vGamesWon = achievementStore.get(AchievementSetting.VectropyGamesWon);
		
		double overallGamesPlayed = eGamesPlayed + vGamesPlayed;
		double overallGamesWon = eGamesWon + vGamesWon;
		
		String winRate = "N/A";
		if (overallGamesPlayed > 0)
		{
			winRate = percentFormat.format(overallGamesWon/overallGamesPlayed);
		}
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("Overall:");
		sb.append("\n\nGames Played: ");
		sb.append((int)overallGamesPlayed);
		sb.append("\nGames Won: ");
		sb.append((int)overallGamesWon);
		sb.append("\nWin Rate: ");
		sb.append(winRate);
		
		String statsString =  sb.toString();
		overallStats.setText(statsString);
	}
	
	private void setStreaksText()
	{
		StringBuffer sb = new StringBuffer();
		
		sb.append("Streaks:");
		sb.append("\n\nLongest Winning Streak: ");
		sb.append(achievementStore.get(AchievementSetting.BestStreak));
		sb.append("\nCurrent Streak: ");
		sb.append(achievementStore.get(AchievementSetting.CurrentStreak));
		
		String statsString =  sb.toString();
		streaksField.setText(statsString);
	}
	
	private void setTimePlayedText()
	{
		var storedTimePlayed = achievementStore.get(AchievementSetting.TimePlayed);
		long timePlayed = System.currentTimeMillis() - ScreenCache.getMainScreen().startTime + storedTimePlayed;
		String timeFormatted = DateUtil.formatHHMMSS(timePlayed);
		
		timePlayedField.setText("Time played: " + timeFormatted);
	}
	
	private void setUpListeners()
	{
		panel_1.add(btnOk);
		btnOk.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent event)
			{
				closeDialog();
			}
		});
	}
	
	private void closeDialog()
	{
		timer.cancel();
		WindowEvent wev = new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
		Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);
	}
	
	class RefreshTask extends TimerTask
	{
		@Override
		public void run()
		{
			try
			{
				setTimePlayedText();
				timer.schedule(new RefreshTask(), 1000);
			}
			catch (Throwable t)
			{
				Debug.stackTrace(t);
			}
		}
	}
}