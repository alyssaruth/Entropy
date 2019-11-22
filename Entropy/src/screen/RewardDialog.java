package screen;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;

@SuppressWarnings("serial")
public class RewardDialog extends JDialog
						  implements ActionListener
{
	public static final String REWARD_BANNER_FOUR_COLOUR = "rewardFourColour.png";
	//public static final String REWARD_BANNER_JOKERS = "rewardJokers.png";
	public static final String REWARD_BANNER_NEGATIVE_JACKS = "rewardNegativeJacks.png";
	public static final String REWARD_BANNER_BLIND = "rewardBlind.png";
	public static final String REWARD_BANNER_MINIMALIST = "rewardMinimalist.png";
	public static final String REWARD_BANNER_VECTROPY = "rewardVectropy.png";
	//public static final String REWARD_BANNER_HANDICAP = "rewardHandicap.png";
	public static final String REWARD_BANNER_CARD_REVEAL = "rewardCardReveal.png";
	public static final String REWARD_BANNER_DEVELOPERS = "rewardDevelopers.png";
	public static final String REWARD_BANNER_ILLEGAL = "rewardIllegal.png";
	public static final String REWARD_BANNER_EXTRA_SUITS = "rewardExtraSuits.png";
	public static final String REWARD_BANNER_CHEATS = "rewardCheats.png";
	
	public RewardDialog(String imageName)
	{
		topPanel.setLayout(new BorderLayout(0, 0));
		rewardTitle.setFont(new Font("Tahoma", Font.BOLD, 16));
		rewardTitle.setHorizontalAlignment(SwingConstants.CENTER);
		rewardTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
		topPanel.add(rewardTitle, BorderLayout.NORTH);
		ImageIcon banner = new ImageIcon(getClass().getResource("/rewards/" + imageName));
		imageBanner.setHorizontalAlignment(SwingConstants.CENTER);
		imageBanner.setIcon(banner);
		topPanel.add(imageBanner, BorderLayout.SOUTH);
		getContentPane().add(topPanel, BorderLayout.NORTH);
		
		setRewardDescriptionAndDialogSize(imageName);
		rewardDescription.setEditable(false);
		rewardDescription.setOpaque(false);
		rewardDescription.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));
		rewardDescription.setBackground(new Color(0, 0, 0, 0));
		getContentPane().add(rewardDescription, BorderLayout.CENTER);
		
		buttonPanel.add(btnOk);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);
		
		btnOk.addActionListener(this);
	}
	
	private final JLabel rewardTitle = new JLabel("Title");
	private final JLabel imageBanner = new JLabel("");
	private final JTextPane rewardDescription = new JTextPane();
	private final JPanel topPanel = new JPanel();
	private final JPanel buttonPanel = new JPanel();
	private final JButton btnOk = new JButton("Ok");
	
	
	public static void showDialog(String imageName)
	{
		RewardDialog dialog = new RewardDialog(imageName);
		
		dialog.setLocationRelativeTo(null);
		dialog.setResizable(false);
		dialog.setModal(true);
		dialog.setVisible(true);
	}


	@Override
	public void actionPerformed(ActionEvent arg0) 
	{
		setVisible(false);
	}
	
	private void setRewardDescriptionAndDialogSize(String imageName)
	{
		String rewardStr = "";
		
		if (imageName.equals(REWARD_BANNER_BLIND))
		{
			rewardTitle.setText("Blind play unlocked!");
			rewardStr = "Ramp up the challenge by making decisions without looking at your cards!"
					  + "\n\nWith this option enabled, your cards will be dealt face-down by default. You can look at any time "
					  + "by clicking the eye but be warned: some achievements require you to go an entire game without peeking once!"
					  + "\n\nEnable this option by going to Tools > Preferences > Gameplay and ticking 'Play Blind'.";
			
			rewardDescription.setText(rewardStr);
			setSize(new Dimension(400, 410));
		}
		else if (imageName.equals(REWARD_BANNER_DEVELOPERS))
		{
			rewardTitle.setText("'Developers' joker set unlocked!");
			rewardStr = "These jokers feature the faces of four people who helped to develop Entropy."
					  + "\n\nUse them by going to Tools > Preferences > Appearance and selecting 'Developers' as the Joker Design.";
			
			rewardDescription.setText(rewardStr);
			setSize(new Dimension(400, 330));
		}
		else if (imageName.equals(REWARD_BANNER_FOUR_COLOUR))
		{
			rewardTitle.setText("Four colour deck unlocked!");
			rewardStr = "You can now play with a four-colour deck, where clubs are green and diamonds are blue."
					  + "\n\nEnable this option by going to Tools > Preferences > Appearance and ticking 'Use 4 colour deck'.";
			
			rewardDescription.setText(rewardStr);
			setSize(new Dimension(400, 330));
		}
		else if (imageName.equals(REWARD_BANNER_CARD_REVEAL))
		{
			rewardTitle.setText("Card reveal unlocked!");
			rewardStr = "Add extra pressure to the game by forcing players to reveal their cards!"
					  + "\n\nWith this option set, players will be forced to show a card each time they make a bid. "
					  + "Players do not have to reveal their last card so not all cards will be shown."
					  + "\n\nEnable this option by going to Tools > Preferences > Gameplay and ticking 'Players reveal cards'.";
			
			rewardDescription.setText(rewardStr);
			setSize(new Dimension(400, 390));
		}
		else if (imageName.equals(REWARD_BANNER_NEGATIVE_JACKS))
		{
			rewardTitle.setText("Negative jacks unlocked!");
			rewardStr = "Spice up the deck by makings Jacks worth -1 of their suit!"
					  + "\n\nEnable this option by going to Tools > Preferences > Gameplay and ticking 'Jacks worth -1'";
			
			rewardDescription.setText(rewardStr);
			setSize(new Dimension(400, 310));
		}
		else if (imageName.equals(REWARD_BANNER_MINIMALIST))
		{
			rewardTitle.setText("'Minimalist' deck design unlocked!");
			rewardStr = "Go for a more minimalist feel with this new deck design."
					  + "\n\nUse it by going to Tools > Preferences > Appearance and selecting 'Minimalist' as the Deck Design.";
			
			rewardDescription.setText(rewardStr);
			setSize(new Dimension(400, 310));
		}
		else if (imageName.equals(REWARD_BANNER_VECTROPY))
		{
			rewardTitle.setText("Vectropy unlocked!");
			rewardStr = "Vectropy is a variant where you have to bid in all four suits at once. "
					  + "New help pages have been added that detail the rules for this new game."
					  + "\n\nPlay Vectropy by going to Tools > Preferences > Gameplay and selecting 'Vectropy' (under Game Mode).";
			
			rewardDescription.setText(rewardStr);
			setSize(new Dimension(400, 350));
		}
		else if (imageName.equals(REWARD_BANNER_ILLEGAL))
		{
			rewardTitle.setText("'Illegal' option unlocked!");
			rewardStr = "You can now shout 'Illegal!' in response to a bid that you think is perfect! "
					  + "\n\nIf you're right your opponent loses a card, but if you're wrong you'll lose one - even if a challenge would have been correct!"
					  + "\n\nYou will see the new 'Illegal!' option the next time you play a game.";
			
			rewardDescription.setText(rewardStr);
			setSize(new Dimension(400, 370));
		}
		else if (imageName.equals(REWARD_BANNER_EXTRA_SUITS))
		{
			rewardTitle.setText("Extra suits unlocked!");
			rewardStr = "You can now play with Stars and Moons, making for up to 6 suits in total! "
					  + "\nSuit order remains alphabetical, making Stars the strongest suit if they are in play."
					  + "\n\nChoose which suits to play with by going to Tools > Preferences > Gameplay and using the options under 'Deck Setup'.";
			
			rewardDescription.setText(rewardStr);
			setSize(new Dimension(400, 370));
		}
		else if (imageName.equals(REWARD_BANNER_CHEATS))
		{
			rewardTitle.setText("Cheats unlocked!");
			rewardStr = "Now you can use certain codes to cheat on your opponents, and even access hidden screens!"
					  + "\n\nThese were first created to speed up testing, especially for things that relied on being good - seriously, who has time for that?"
					  + "\n\nA full list of cheats can be found under Help > Miscellaneous > Cheat Codes.";
			
			rewardDescription.setText(rewardStr);
			setSize(new Dimension(400, 390));
		}
	}
}
