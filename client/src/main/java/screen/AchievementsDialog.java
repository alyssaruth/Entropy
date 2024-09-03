package screen;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import object.AchievementBadge;
import object.RewardStar;
import util.Debug;
import javax.swing.border.EmptyBorder;
import java.awt.Color;

public class AchievementsDialog extends JFrame 
								implements MouseMotionListener,
										   MouseListener,
										   ActionListener,
										   AchievementBadges
								
{
	private static final ImageIcon unlockedRewardIcon = new ImageIcon(AchievementsDialog.class.getResource("/rewards/unlockedReward.png"));
	private static final ImageIcon unlockedRewardIconDark = new ImageIcon(AchievementsDialog.class.getResource("/rewards/unlockedRewardDark.png"));
	private static final ImageIcon unlockedRewardIconDarkest = new ImageIcon(AchievementsDialog.class.getResource("/rewards/unlockedRewardDarkest.png"));
	private static final ImageIcon lockedRewardIcon = new ImageIcon(AchievementsDialog.class.getResource("/rewards/lockedReward.png"));
	
	private int achievementsEarned = 0;
	private int achievementsTotal = 0;
	private int progressShowing = 0;
	
	private boolean redrawing = false;
	
	private ArrayList<AchievementBadge> achievementBadges = new ArrayList<>();
	private ArrayList<AchievementBadge> bonusBadges = new ArrayList<>();
	private ArrayList<RewardStar> rewardStars = new ArrayList<>();
	
	public AchievementsDialog() 
	{
		setTitle("Achievements");
		setSize(860, 504);
		addMouseMotionListener(this);
		setIconImage(new ImageIcon(AchievementsDialog.class.getResource("/icons/achievements.png")).getImage());
		getContentPane().setLayout(null);
		separator.setBounds(0, 342, 878, 2);
		getContentPane().add(separator);
		title.setHorizontalAlignment(SwingConstants.CENTER);
		title.setFont(new Font("Tahoma", Font.BOLD, 20));
		title.setBounds(255, 16, 235, 25);
		getContentPane().add(title);
		panelDescriptions.setBounds(10, 345, 714, 131);
		getContentPane().add(panelDescriptions);
		panelDescriptions.setLayout(null);
		achievementName.setBounds(0, 0, 714, 36);
		achievementName.setFont(new Font("Tahoma", Font.BOLD, 15));
		panelDescriptions.add(achievementName);
		achievementExplanation.setBounds(0, 32, 714, 36);
		panelDescriptions.add(achievementExplanation);
		achievementDescription.setVerticalAlignment(SwingConstants.TOP);
		achievementDescription.setBounds(0, 70, 714, 54);
		achievementDescription.setFont(new Font("Tahoma", Font.ITALIC, 12));
		panelDescriptions.add(achievementDescription);

		reward5.setBounds(820, 287, 17, 16);
		getContentPane().add(reward5);
		reward10.setBounds(715, 272, 17, 16);
		getContentPane().add(reward10);
		reward15.setBounds(820, 257, 17, 16);
		getContentPane().add(reward15);
		reward20.setBounds(715, 242, 17, 16);
		getContentPane().add(reward20);
		reward25.setBounds(820, 227, 17, 16);
		getContentPane().add(reward25);
		reward30.setBounds(715, 212, 17, 16);
		getContentPane().add(reward30);
		reward35.setBounds(820, 197, 17, 16);
		getContentPane().add(reward35);
		reward40.setBounds(715, 182, 17, 16);
		getContentPane().add(reward40);
		reward45.setBounds(820, 167, 17, 16);
		getContentPane().add(reward45);
		reward50.setBounds(715, 152, 17, 16);
		getContentPane().add(reward50);
		testTube.setIcon(new ImageIcon(AchievementsDialog.class.getResource("/tubes/t28.png")));
		testTube.setBounds(730, -28, 92, 344);
		getContentPane().add(testTube);
		pageOne.setBounds(56, 56, 615, 255);
		getContentPane().add(pageOne);
		pageTwo.setBounds(56, 56, 615, 255);
		getContentPane().add(pageTwo);
		btnLeft.setBackground(Color.WHITE);
		btnLeft.setOpaque(false);
		btnLeft.setFont(new Font("Segoe UI Symbol", Font.BOLD, 24));
		btnLeft.setBorder(new EmptyBorder(0, 0, 0, 0));
		btnLeft.setBounds(11, 158, 30, 50);
		getContentPane().add(btnLeft);
		btnLeft.addActionListener(this);
		btnRight.setOpaque(false);
		btnRight.setBackground(Color.WHITE);
		btnRight.setFont(new Font("Segoe UI Symbol", Font.BOLD, 24));
		btnRight.setBorder(new EmptyBorder(0, 0, 0, 0));
		btnRight.setBounds(680, 158, 30, 50);
		getContentPane().add(btnRight);
		btnRight.addActionListener(this);

		btnLeft.setEnabled(false);
		pageTwo.setVisible(false);

		populateBadgeListAndAddMotionListeners();
		populateStarListAndAddMouseListeners();
	}
	
	private final JPanel panelDescriptions = new JPanel();
	private final JSeparator separator = new JSeparator();
	
	private final AchievementsPanelOne pageOne = new AchievementsPanelOne("Page One");
	private final AchievementsPanelTwo pageTwo = new AchievementsPanelTwo("Page Two");
	private final JButton btnLeft = new JButton("\u25C0");
	private final JButton btnRight = new JButton("\u25B6");
	
	private final JLabel testTube = new JLabel("");
	private final RewardStar reward5 = new RewardStar(5, "Four Colours", RewardDialog.REWARD_BANNER_FOUR_COLOUR);
	private final RewardStar reward10 = new RewardStar(10, "Negative Jacks", RewardDialog.REWARD_BANNER_NEGATIVE_JACKS);
	private final RewardStar reward15 = new RewardStar(15, "Blind Play", RewardDialog.REWARD_BANNER_BLIND);
	private final RewardStar reward20 = new RewardStar(20, "New Deck Design", RewardDialog.REWARD_BANNER_MINIMALIST);
	private final RewardStar reward25 = new RewardStar(25, "Vectropy", RewardDialog.REWARD_BANNER_VECTROPY);
	private final RewardStar reward30 = new RewardStar(30, "Card Reveal", RewardDialog.REWARD_BANNER_CARD_REVEAL);
	private final RewardStar reward35 = new RewardStar(35, "Extra Suits", RewardDialog.REWARD_BANNER_EXTRA_SUITS);
	private final RewardStar reward40 = new RewardStar(40, "Illegal!", RewardDialog.REWARD_BANNER_ILLEGAL);
	private final RewardStar reward45 = new RewardStar(45, "New Joker Design", RewardDialog.REWARD_BANNER_DEVELOPERS);
	private final RewardStar reward50 = new RewardStar(50, "Cheats", RewardDialog.REWARD_BANNER_CHEATS);
	
	private final JLabel title = new JLabel("");
	private final JLabel achievementName = new JLabel("Achievement Name");
	private final JLabel achievementExplanation = new JLabel("How you earned it");
	private final JLabel achievementDescription = new JLabel("Humorous description");
	
	public void init()
	{
		achievementName.setText("");
		achievementDescription.setText("");
		achievementExplanation.setText("");
		
		setAchievementExplanations();
		setAchievementDescriptions();
		displayBadgesAndTitles();
		redrawStars();
		animateTestTube();
	}
	
	private void setAchievementExplanations()
	{
		cavemanBadge.setExplanation("Make a perfect bid in Clubs");
		burglarBadge.setExplanation("Make a perfect bid in Diamonds");
		lionBadge.setExplanation("Make a perfect bid in Hearts");
		gardenerBadge.setExplanation("Make a perfect bid in Spades");
		psychicBadge.setExplanation("Make a perfect bid whilst blind");
		fiveMinutesBadge.setExplanation("Play the game for 5 minutes");
		fifteenMinutesBadge.setExplanation("Play the game for 15 minutes");
		thirtyMinutesBadge.setExplanation("Play the game for 30 minutes");
		sixtyMinutesBadge.setExplanation("Play the game for 1 hour");
		twoHoursBadge.setExplanation("Play the game for 2 hours");
		cowardBadge.setExplanation("Quit a game mid-way through");
		spectatorBadge.setExplanation("Watch a game through to the end after you've been knocked out");
		vanityBadge.setExplanation("View your achievements and/or statistics 20 times");
		unscathedBadge.setExplanation("Win a full two player game without losing a single round");
		bulletproofBadge.setExplanation("Win a full three player game without losing a single round");
		superhumanBadge.setExplanation("Win a full four player game without losing a single round");
		momentumBadge.setExplanation("Win 3 games in a row");
		chainReactionBadge.setExplanation("Win 6 games in a row");
		perpetualMotionBadge.setExplanation("Win 10 games in a row");
		participantBadge.setExplanation("Play 10 games (includes online)");
		hobbyistBadge.setExplanation("Play 25 games (includes online)");
		enthusiastBadge.setExplanation("Play 50 games (includes online)");
		professionalBadge.setExplanation("Play 100 games (includes online)");
		veteranBadge.setExplanation("Play 200 games (includes online)");
		firstTimerBadge.setExplanation("Win a game of Entropy (includes online)");
		casualStrategistBadge.setExplanation("Win 10 games of Entropy (includes online)");
		consistentWinnerBadge.setExplanation("Win 25 games of Entropy (includes online)");
		dominantForceBadge.setExplanation("Win 50 games of Entropy (includes online)");
		secondThoughtsBadge.setExplanation("Look at your cards after bidding blind");
		fullBlindTwoBadge.setExplanation("Win a full two player game entirely blind");
		fullBlindThreeBadge.setExplanation("Win a full three player game entirely blind");
		fullBlindFourBadge.setExplanation("Win a full four player game entirely blind");
		chimeraBadge.setExplanation("Make perfect bids in 3 or more different suits during one game");
		precisionBadge.setExplanation("Win a game of 5 rounds or more without ever making an overbid");
		mathematicianBadge.setExplanation("Make a perfect bid in Vectropy");
		nuclearStrikeBadge.setExplanation("Win a full four player game starting with one card and playing entirely blind");
		handicapTwoBadge.setExplanation("Win a two player game with a handicap of 2 or more");
		handicapThreeBadge.setExplanation("Win a three player game with a handicap of 2 or more");
		handicapFourBadge.setExplanation("Win a four player game with a handicap of 2 or more");
		vectropyOneBadge.setExplanation("Win a game of Vectropy (includes online)");
		vectropyTenBadge.setExplanation("Win 10 games of Vectropy (includes online)");
		vectropyTwentyFiveBadge.setExplanation("Win 25 games of Vectropy (includes online)");
		vectropyFiftyBadge.setExplanation("Win 50 games of Vectropy (includes online)");
		distractedBadge.setExplanation("Take longer than 3 minutes to act on your turn");
		citizensArrestBadge.setExplanation("Call 'Illegal!' correctly");
		connectedBadge.setExplanation("Connect to the Entropy Server");
		railbirdBadge.setExplanation("Observe at least one round online");
		socialBadge.setExplanation("Play with 5 or more individuals online");
		blueScreenOfDeathBadge.setExplanation("Cause something to go wrong in the game");
		werewolfBadge.setExplanation("Make a perfect bid in Moons");
		spacemanBadge.setExplanation("Make a perfect bid in Stars");
		konamiCodeBadge.setExplanation("Enter the Konami code in the main Entropy window");
		lookAtMeBadge.setExplanation("Export a replay");
		bookwormBadge.setExplanation("Spend 5 minutes on the Help dialog");
		omniscientBadge.setExplanation("Play in a round where 10 or more of your opponents' cards are revealed");
		chattyBadge.setExplanation("Send 25 online chat messages");
		honestBadge.setExplanation("Win a game only revealing cards of the suit you were bidding (must reveal at least 5)");
		deceitfulBadge.setExplanation("Win a game never revealing a card of the suit you were bidding (must reveal at least 5)");
		monotoneBadge.setExplanation("Win a 5-card game of Entropy only ever bidding one suit");
	}
	
	private void setAchievementDescriptions()
	{
		cavemanBadge.setDescription("Clobbered 'em!");
		burglarBadge.setDescription("Dazzling.");
		lionBadge.setDescription("A bid with some real heart.");
		gardenerBadge.setDescription("Textbook digging!");
		psychicBadge.setDescription("You totally *knew* it was there, didn't you?");
		fiveMinutesBadge.setDescription("Kudos, you stuck with this for five minutes. If it helps, that's the same as a slow 1500m run...");
		fifteenMinutesBadge.setDescription("Still not setting the world alight, you've progressed to a medium-paced 5000m. Keep trying.");
		thirtyMinutesBadge.setDescription("Now the blood's pumping! 10,000m and a new World Record (assuming you're playing pre-1938).");
		sixtyMinutesBadge.setDescription("<HTML>All that training is finally paying off - you* just ran a half marathon!<br><br>"
				 					   + "*Not really you. You're still inside, staring at a screen.</HTML>");
		twoHoursBadge.setDescription("<HTML>You could have run a marathon in the time you've been playing this. Where 'could have' assumes<br>"
			     				   + "your name to be Haile Gebrselassie and that you chose to run with a strong following wind.</HTML>");
		cowardBadge.setDescription("At least I assume it was cowardice. Maybe you just had better things to do.");
		spectatorBadge.setDescription("Bet that was fun. Did the best random number generator win?");
		vanityBadge.setDescription("Your own biggest fan!");
		unscathedBadge.setDescription("Five whole rounds and not a foot wrong.");
		bulletproofBadge.setDescription("I can dodge bullets, baby!");
		superhumanBadge.setDescription("No sarcastic remark for this one - just a sincere pat on the back. Well played, sir.");
		momentumBadge.setDescription("Hey look at that! You're almost on a roll!");
		chainReactionBadge.setDescription("This is actually pretty good. If they were all two player games it's still kinda lame.");
		perpetualMotionBadge.setDescription("Tough, even with just two players. You'll have needed some luck for this one.");
		participantBadge.setDescription("Well you've given it a try. Just how into card games are you?");
		hobbyistBadge.setDescription("Meh, somewhat entertaining. Aren't you curious about some of those achievements?");
		enthusiastBadge.setDescription("It's more than just a time-waster now, isn't it?");
		professionalBadge.setDescription("How many hours must you have racked up by now...?");
		veteranBadge.setDescription("Please go outside.");
		firstTimerBadge.setDescription("Hoorah! You're a success, and here's a medal to prove it!");
		casualStrategistBadge.setDescription("You might just be starting to get the hang of this.");
		consistentWinnerBadge.setDescription("Definitely getting the hang of it now, depending on your loss statistics.");
		dominantForceBadge.setDescription("Fine, fine. You're a competent Entropy player. Happy now?");
		secondThoughtsBadge.setDescription("'Fraidy-cat.");
		fullBlindTwoBadge.setDescription("Just clicking buttons would get you this - a highly skilled pursuit indeed.");
		fullBlindThreeBadge.setDescription("You would've had to think about what you were doing a bit for this one.");
		fullBlindFourBadge.setDescription("Impressive. Wasn't it just a little tempting to sneak a peek near the end?");
		chimeraBadge.setDescription("One of the hardest achievements to earn. Also a badass creature from Greek mythology.");
		precisionBadge.setDescription("There's a fine line between precise and overly cautious, but this time you nailed it.");
		mathematicianBadge.setDescription("Perfect in all four suits at once, like only a mathematician could.");
		nuclearStrikeBadge.setDescription("NUCLEAR STRRRRRRRIKE!");
		handicapTwoBadge.setDescription("I wonder what the others could be...");
		handicapThreeBadge.setDescription("Hey, that picture looks familiar!");
		handicapFourBadge.setDescription("Yeah, I wasn't at my most imaginative when I made these.");
		vectropyOneBadge.setDescription("Maybe not that easy at first, but you get used to it I swear.");
		vectropyTenBadge.setDescription("It's a good job it's not binary, otherwise you'd have only won twice.");
		vectropyTwentyFiveBadge.setDescription("I've got a formula for you: this badge * 2 = next badge. Get on it.");
		vectropyFiftyBadge.setDescription("The big 5-0, as they say. Congratulations.");
		distractedBadge.setDescription("Hey, it's your turn! Do something already!");
		citizensArrestBadge.setDescription("Aren't you glad the computer opponents can't do this to you?");
		connectedBadge.setDescription("It's a real hive of activity...");
		railbirdBadge.setDescription("I'm surprised there were enough people online for you to accomplish this.");
		socialBadge.setDescription("Awesome!");
		blueScreenOfDeathBadge.setDescription("Oops! Looks like I'll be getting an email to fix whatever that was...");
		werewolfBadge.setDescription("Harooooo!");
		spacemanBadge.setDescription("A bid so great it's taken you to outer space!");
		konamiCodeBadge.setDescription("I hear if you hold B and Down the opponent will never challenge.");
		lookAtMeBadge.setDescription("I'm Mr. Meeseeeeks, look at me!");
		bookwormBadge.setDescription("Well at least someone reads the help.");
		omniscientBadge.setDescription("Not exactly cloak-and-daggers when you can pretty much see everyone's hand...");
		chattyBadge.setDescription("Bonus points if there was actually someone else around at the time.");
		honestBadge.setDescription("Honesty is the best policy. Except it really probably isn't.");
		deceitfulBadge.setDescription("No double-bluffs from you, just straight up lies.");
		monotoneBadge.setDescription("Sometimes being a one-trick pony has its upsides.");
	}
	
	private void populateBadgeListAndAddMotionListeners()
	{
		achievementBadges.addAll(pageOne.getAchievementBadges());
		achievementBadges.addAll(pageTwo.getAchievementBadges());
		
		for (int i=0; i<achievementBadges.size(); i++)
		{
			AchievementBadge badge = achievementBadges.get(i);
			badge.addMouseMotionListener(this);
		}
	}
	
	private void populateStarListAndAddMouseListeners()
	{
		Component[] components = getContentPane().getComponents();
		for (int i=0; i<components.length; i++)
		{
			Component c = components[i];
			if (c instanceof RewardStar)
			{
				RewardStar star = (RewardStar)c;
				star.addMouseListener(this);
				rewardStars.add(star);
			}
		}
	}
	
	private void displayBadgesAndTitles()
	{
		achievementsEarned = 0;
		achievementsTotal = achievementBadges.size();
		
		for (int i=0; i<achievementBadges.size(); i++)
		{
			AchievementBadge badge = achievementBadges.get(i);
			if (badge.isEarned())
			{
				achievementsEarned++;
				ImageIcon icon = badge.getEarnedIcon();
				badge.setIcon(icon);
			}
			else
			{
				badge.setIcon(lockedIcon);
			}
		}
		
		for (int i=0; i<bonusBadges.size(); i++)
		{
			AchievementBadge badge = bonusBadges.get(i);
			if (badge.isEarned())
			{
				achievementsEarned++;
				ImageIcon icon = badge.getEarnedIcon();
				badge.setIcon(icon);
				badge.setVisible(true);
			}
			else
			{
				badge.setVisible(false);
			}
		}
		
		updateTitleText();
	}
	
	private void updateTitleText()
	{
		String titleText = getVisiblePage().getPageTitle();
		title.setText(titleText);
	}
	
	private void animateTestTube()
	{
		redrawing = true;
		
		Timer timer = new Timer("Timer-TestTube");
		progressShowing = 0;
		testTube.setIcon(getTubeIconForIndex(0));
		
		for (int i=1; i<=achievementsEarned; i++)
		{
			timer.schedule(new RedrawTestTube(), 30 * i);
		}
	}
	
	private void updateTestTube()
	{
		testTube.setIcon(getTubeIconForIndex(achievementsEarned));
	}
	
	private ImageIcon getTubeIconForIndex(int i)
	{
		String name = "t" + i + ".png";
		return new ImageIcon(AchievementsDialog.class.getResource("/tubes/" + name));
	}
	
	private void redrawStars()
	{
		for (int i=0; i<rewardStars.size(); i++)
		{
			RewardStar star = rewardStars.get(i);
			
			if (star.isUnlocked(progressShowing))
			{
				star.setIcon(unlockedRewardIcon);
				String hoverDesc = star.getHoverDesc();
				star.setToolTipText(hoverDesc);
			}
			else
			{
				star.setIcon(lockedRewardIcon);
				star.setToolTipText("Locked");
			}
		}
	}
	
	public void refresh(boolean restartTube)
	{
		displayBadgesAndTitles();

		if (restartTube)
		{
			animateTestTube();
		}
		else
		{
			updateTestTube();
		}
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {}

	@Override
	public void mouseMoved(MouseEvent arg0) 
	{
		Component c = arg0.getComponent();
		
		if (c instanceof AchievementBadge)
		{
			AchievementBadge badge = (AchievementBadge)arg0.getComponent();
			achievementName.setText(c.getName());
			
			if (badge.isEarned())
			{
				achievementExplanation.setText(badge.getExplanation());
				achievementDescription.setText(badge.getDescription());

			}
			else
			{
				achievementExplanation.setText("???");
				achievementDescription.setText("");
			}
		}
		else
		{
			achievementName.setText("");
			achievementExplanation.setText("");
			achievementDescription.setText("");
		}
	}
	
	public int getAchievementsEarned()
	{
		if (!isVisible())
		{
			displayBadgesAndTitles();
		}
		
		return achievementsEarned;
	}
	
	private AchievementsPanel getVisiblePage()
	{
		if (pageOne.isVisible())
		{
			return pageOne;
		}
		
		return pageTwo;
	}
	
	private class RedrawTestTube extends TimerTask
	{
		@Override
		public void run() 
		{
			if (isVisible())
			{
				if (progressShowing < achievementsEarned)
				{
					progressShowing++;
				}
				
				testTube.setIcon(getTubeIconForIndex(progressShowing));
				redrawStars();
				
				if (progressShowing == achievementsEarned)
				{
					redrawing = false;
				}
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) 
	{
		RewardStar c = (RewardStar)e.getComponent();
		if (c.isUnlocked(achievementsEarned) 
		  && !redrawing)
		{
			String imageName = c.getImageName();
			RewardDialog.showDialog(imageName);
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) 
	{
		RewardStar source = (RewardStar)e.getComponent();
		if (source.isUnlocked(progressShowing) && !redrawing)
		{
			source.setIcon(unlockedRewardIconDark);
		}
	}

	@Override
	public void mouseExited(MouseEvent e) 
	{
		RewardStar source = (RewardStar)e.getComponent();
		if (source.isUnlocked(progressShowing) && !redrawing)
		{
			source.setIcon(unlockedRewardIcon);
		}
	}
	
	@Override
	public void mousePressed(MouseEvent e) 
	{
		RewardStar source = (RewardStar)e.getComponent();
		if (source.isUnlocked(achievementsEarned) && !redrawing)
		{
			source.setIcon(unlockedRewardIconDarkest);
		}
	}
	
	@Override
	public void mouseReleased(MouseEvent e) 
	{
		RewardStar source = (RewardStar)e.getComponent();
		if (source.isUnlocked(achievementsEarned) && !redrawing)
		{
			source.setIcon(unlockedRewardIcon);
		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		Object source = arg0.getSource();
		boolean right = (source == btnRight);

		btnLeft.setEnabled(right);
		btnRight.setEnabled(!right);
		pageTwo.setVisible(right);
		pageOne.setVisible(!right);
		
		updateTitleText();
	}
}