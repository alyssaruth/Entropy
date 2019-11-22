package online.screen;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;

import org.w3c.dom.Element;

import screen.ScreenCache;
import util.EntropyColour;
import util.XmlUtil;

public class OnlineStatsPanel extends JPanel
							  implements MouseListener
{
	private static final int DISPLAY_MODE_ENTROPY = 0;
	private static final int DISPLAY_MODE_VECTROPY = 1;
	private static final int DISPLAY_MODE_OVERALL = 2;
	
	private int displayMode = DISPLAY_MODE_ENTROPY;
	private Element responseElement = null;
	
	private int twoPlayerWins = 0;
	private int twoPlayerLosses = 0;
	private int threePlayerWins = 0;
	private int threePlayerLosses = 0;
	private int fourPlayerWins = 0;
	private int fourPlayerLosses = 0;
	
	public OnlineStatsPanel() 
	{	
		setBorder(null);
		setLayout(new BorderLayout(0, 0));
		lblModeSelection.setPreferredSize(new Dimension(0, 26));
		lblModeSelection.setBorder(new MatteBorder(1, 1, 0, 1, new Color(0, 0, 0)));
		lblModeSelection.setFont(new Font("Tahoma", Font.BOLD, 15));
		add(lblModeSelection, BorderLayout.NORTH);
		
		JPanel panelStats = new JPanel();
		panelStats.setBorder(null);
		add(panelStats, BorderLayout.CENTER);
		panelStats.setLayout(new GridLayout(4, 3, 0, 0));
		
		JLabel blankLabel = new JLabel("");
		blankLabel.setBorder(new LineBorder(new Color(0, 0, 0)));
		blankLabel.setOpaque(true);
		blankLabel.setBackground(EntropyColour.COLOUR_LOBBY_PALE_BLUE);
		panelStats.add(blankLabel);
		
		JLabel lblWins = new JLabel("W");
		lblWins.setBorder(new MatteBorder(1, 0, 1, 0, new Color(0, 0, 0)));
		lblWins.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblWins.setHorizontalAlignment(SwingConstants.CENTER);
		lblWins.setOpaque(true);
		lblWins.setBackground(EntropyColour.COLOUR_STATS_WINS_TITLE_BACKGROUND);
		panelStats.add(lblWins);
		
		JLabel lblLosses = new JLabel("L");
		lblLosses.setBorder(new LineBorder(new Color(0, 0, 0)));
		lblLosses.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblLosses.setHorizontalAlignment(SwingConstants.CENTER);
		lblLosses.setOpaque(true);
		lblLosses.setBackground(EntropyColour.COLOUR_STATS_LOSSES_TITLE_BACKGROUND);
		panelStats.add(lblLosses);
		
		JLabel lblTwoPlayer = new JLabel("2 Player");
		lblTwoPlayer.setBorder(new MatteBorder(0, 1, 0, 1, new Color(0, 0, 0)));
		lblTwoPlayer.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblTwoPlayer.setHorizontalAlignment(SwingConstants.CENTER);
		lblTwoPlayer.setOpaque(true);
		lblTwoPlayer.setBackground(EntropyColour.COLOUR_LOBBY_MEDIUM_BLUE);
		
		panelStats.add(lblTwoPlayer);
		lblTwoPlayerWins.setBorder(new MatteBorder(0, 0, 0, 1, new Color(0, 0, 0)));
		panelStats.add(lblTwoPlayerWins);
		lblTwoPlayerLosses.setBorder(new MatteBorder(0, 0, 0, 1, new Color(0, 0, 0)));
		panelStats.add(lblTwoPlayerLosses);
		
		JLabel lblThreePlayer = new JLabel("3 Player");
		lblThreePlayer.setBorder(new MatteBorder(0, 1, 0, 1, new Color(0, 0, 0)));
		lblThreePlayer.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblThreePlayer.setHorizontalAlignment(SwingConstants.CENTER);
		lblThreePlayer.setOpaque(true);
		lblThreePlayer.setBackground(EntropyColour.COLOUR_LOBBY_MEDIUM_BLUE);
		panelStats.add(lblThreePlayer);
		lblThreePlayerWins.setBorder(new MatteBorder(0, 0, 0, 1, new Color(0, 0, 0)));
		panelStats.add(lblThreePlayerWins);
		lblThreePlayerLosses.setBorder(new MatteBorder(0, 0, 0, 1, new Color(0, 0, 0)));
		panelStats.add(lblThreePlayerLosses);
		
		JLabel lblFourPlayer = new JLabel("4 Player");
		lblFourPlayer.setBorder(new MatteBorder(0, 1, 1, 1, new Color(0, 0, 0)));
		lblFourPlayer.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblFourPlayer.setHorizontalAlignment(SwingConstants.CENTER);
		lblFourPlayer.setOpaque(true);
		lblFourPlayer.setBackground(EntropyColour.COLOUR_LOBBY_MEDIUM_BLUE);
		panelStats.add(lblFourPlayer);
		lblFourPlayerWins.setBorder(new MatteBorder(0, 0, 1, 1, new Color(0, 0, 0)));
		panelStats.add(lblFourPlayerWins);
		lblFourPlayerLosses.setBorder(new MatteBorder(0, 0, 1, 1, new Color(0, 0, 0)));
		panelStats.add(lblFourPlayerLosses);
		
		lblTwoPlayerWins.setHorizontalAlignment(SwingConstants.CENTER);
		lblTwoPlayerWins.setOpaque(true);
		lblTwoPlayerWins.setBackground(EntropyColour.COLOUR_STATS_WINS_BACKGROUND);
		lblTwoPlayerLosses.setHorizontalAlignment(SwingConstants.CENTER);
		lblTwoPlayerLosses.setOpaque(true);
		lblTwoPlayerLosses.setBackground(EntropyColour.COLOUR_STATS_LOSSES_BACKGROUND);
		lblThreePlayerWins.setHorizontalAlignment(SwingConstants.CENTER);
		lblThreePlayerWins.setOpaque(true);
		lblThreePlayerWins.setBackground(EntropyColour.COLOUR_STATS_WINS_BACKGROUND);
		lblThreePlayerLosses.setHorizontalAlignment(SwingConstants.CENTER);
		lblThreePlayerLosses.setOpaque(true);
		lblThreePlayerLosses.setBackground(EntropyColour.COLOUR_STATS_LOSSES_BACKGROUND);
		lblFourPlayerWins.setHorizontalAlignment(SwingConstants.CENTER);
		lblFourPlayerWins.setOpaque(true);
		lblFourPlayerWins.setBackground(EntropyColour.COLOUR_STATS_WINS_BACKGROUND);
		lblFourPlayerLosses.setHorizontalAlignment(SwingConstants.CENTER);
		lblFourPlayerLosses.setOpaque(true);
		lblFourPlayerLosses.setBackground(EntropyColour.COLOUR_STATS_LOSSES_BACKGROUND);
		
		lblModeSelection.setOpaque(true);
		lblModeSelection.setBackground(EntropyColour.COLOUR_LOBBY_DARK_BLUE);
		lblModeSelection.setHorizontalAlignment(SwingConstants.CENTER);
		
		lblLeaderboardLink.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblLeaderboardLink.setForeground(Color.BLUE);
		lblLeaderboardLink.setBorder(new EmptyBorder(5, 0, 0, 0));
		lblLeaderboardLink.setHorizontalAlignment(SwingConstants.CENTER);
		lblLeaderboardLink.setOpaque(true);
		lblLeaderboardLink.setBackground(EntropyColour.COLOUR_LOBBY_PALE_BLUE);
		add(lblLeaderboardLink, BorderLayout.SOUTH);
		
		lblModeSelection.addMouseListener(this);
		lblLeaderboardLink.addMouseListener(this);
	}
	
	private final JLabel lblModeSelection = new JLabel();
	private final JLabel lblTwoPlayerWins = new JLabel("");
	private final JLabel lblTwoPlayerLosses = new JLabel("");
	private final JLabel lblThreePlayerWins = new JLabel("");
	private final JLabel lblThreePlayerLosses = new JLabel("");
	private final JLabel lblFourPlayerWins = new JLabel("");
	private final JLabel lblFourPlayerLosses = new JLabel("");
	private final JLabel lblLeaderboardLink = new JLabel("<html><u>View Leaderboard</u></html>");
	
	public void init()
	{
		displayMode = DISPLAY_MODE_ENTROPY;
		lblModeSelection.setText("My Stats - Entropy");
	}
	
	public void updateVariablesFromResponse(Element responseElement)
	{
		this.responseElement = responseElement;
		
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				populateStats();
			}
		});
	}
	
	private void resetVariables()
	{
		twoPlayerWins = 0;
		twoPlayerLosses = 0;
		threePlayerWins = 0;
		threePlayerLosses = 0;
		fourPlayerWins = 0;
		fourPlayerLosses = 0;
	}
	
	private void populateStats()
	{
		synchronized (this)
		{
			resetVariables();
			
			if (displayMode == DISPLAY_MODE_ENTROPY)
			{
				addEntropyStats();
			}
			else if (displayMode == DISPLAY_MODE_VECTROPY)
			{
				addVectropyStats();
			}
			else
			{
				addEntropyStats();
				addVectropyStats();
			}
			
			lblTwoPlayerWins.setText("" + twoPlayerWins);
			lblTwoPlayerLosses.setText("" + twoPlayerLosses);
			lblThreePlayerWins.setText("" + threePlayerWins);
			lblThreePlayerLosses.setText("" + threePlayerLosses);
			lblFourPlayerWins.setText("" + fourPlayerWins);
			lblFourPlayerLosses.setText("" + fourPlayerLosses);
		}
	}
	
	private void addEntropyStats()
	{
		addStats("Entropy");
	}
	
	private void addVectropyStats()
	{
		addStats("Vectropy");
	}
	
	private void addStats(String tagName)
	{
		twoPlayerWins += XmlUtil.getAttributeInt(responseElement, tagName + "2Won");
		threePlayerWins += XmlUtil.getAttributeInt(responseElement, tagName + "3Won");
		fourPlayerWins += XmlUtil.getAttributeInt(responseElement, tagName + "4Won");
		
		twoPlayerLosses += XmlUtil.getAttributeInt(responseElement, tagName + "2Lost");
		threePlayerLosses += XmlUtil.getAttributeInt(responseElement, tagName + "3Lost");
		fourPlayerLosses += XmlUtil.getAttributeInt(responseElement, tagName + "4Lost");
	}
	
	private void changeDisplayMode()
	{
		if (displayMode == DISPLAY_MODE_ENTROPY)
		{
			displayMode = DISPLAY_MODE_VECTROPY;
			lblModeSelection.setText("My Stats - Vectropy");
			populateStats();
		}
		else if (displayMode == DISPLAY_MODE_VECTROPY)
		{
			displayMode = DISPLAY_MODE_OVERALL;
			lblModeSelection.setText("My Stats - Overall");
			populateStats();
		}
		else
		{
			displayMode = DISPLAY_MODE_ENTROPY;
			lblModeSelection.setText("My Stats - Entropy");
			populateStats();
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent arg0) 
	{
		Component source = (Component)arg0.getSource();
		if (source == lblModeSelection)
		{
			changeDisplayMode();
		}
		else if (source == lblLeaderboardLink)
		{
			Leaderboard leaderboard = ScreenCache.getLeaderboard();
			if (leaderboard.isVisible())
			{
				leaderboard.requestFocus();
			}
			else
			{
				leaderboard.setLocationRelativeTo(null);
				leaderboard.setVisible(true);
				leaderboard.init();
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) 
	{
		Component source = (Component)arg0.getSource();
		if (source == lblModeSelection)
		{
			lblModeSelection.setBackground(EntropyColour.COLOUR_LOBBY_DARKER_BLUE);
		}
		else if (source == lblLeaderboardLink)
		{
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}
	}

	@Override
	public void mouseExited(MouseEvent arg0) 
	{
		Component source = (Component)arg0.getSource();
		if (source == lblModeSelection)
		{
			lblModeSelection.setBackground(EntropyColour.COLOUR_LOBBY_DARK_BLUE);
		}
		else if (source == lblLeaderboardLink)
		{
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}

	@Override
	public void mousePressed(MouseEvent arg0) 
	{
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) 
	{
		
	}
}
