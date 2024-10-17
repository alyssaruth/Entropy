package screen;

import object.AchievementBadge;
import object.RewardStar;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static screen.AchievementsPanelKt.makeAchievementPanels;
import static util.AchievementUtilKt.getAchievementsEarned;

public class AchievementsDialog extends JFrame 
								implements MouseMotionListener,
										   MouseListener,
										   ActionListener
								
{
	private static final ImageIcon unlockedRewardIcon = new ImageIcon(AchievementsDialog.class.getResource("/rewards/unlockedReward.png"));
	private static final ImageIcon unlockedRewardIconDark = new ImageIcon(AchievementsDialog.class.getResource("/rewards/unlockedRewardDark.png"));
	private static final ImageIcon unlockedRewardIconDarkest = new ImageIcon(AchievementsDialog.class.getResource("/rewards/unlockedRewardDarkest.png"));
	private static final ImageIcon lockedRewardIcon = new ImageIcon(AchievementsDialog.class.getResource("/rewards/lockedReward.png"));

	private int currentPage = 0;
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

		for (AchievementsPanel page : pages) {
			page.setBounds(56, 56, 615, 255);
			getContentPane().add(page);
		}

		populateBadgeListAndAddMotionListeners();
		populateStarListAndAddMouseListeners();
	}
	
	private final JPanel panelDescriptions = new JPanel();
	private final JSeparator separator = new JSeparator();

	private final List<AchievementsPanel> pages = makeAchievementPanels();
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

		updatePagination();
		redrawStars();
		animateTestTube();
	}
	
	private void populateBadgeListAndAddMotionListeners()
	{
		pages.forEach(page -> {
			for (AchievementBadge badge : page.getBadges())
			{
				badge.addMouseMotionListener(this);
			}
		});

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
	
	private void animateTestTube()
	{
		redrawing = true;
		
		Timer timer = new Timer("Timer-TestTube");
		progressShowing = 0;
		testTube.setIcon(getTubeIconForIndex(0));
		
		for (int i = 1; i<= getAchievementsEarned(); i++)
		{
			timer.schedule(new RedrawTestTube(), 30 * i);
		}
	}
	
	private void updateTestTube()
	{
		testTube.setIcon(getTubeIconForIndex(getAchievementsEarned()));
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
	
	private class RedrawTestTube extends TimerTask
	{
		@Override
		public void run() 
		{
			if (isVisible())
			{
				if (progressShowing < getAchievementsEarned())
				{
					progressShowing++;
				}
				
				testTube.setIcon(getTubeIconForIndex(progressShowing));
				redrawStars();
				
				if (progressShowing == getAchievementsEarned())
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
		if (c.isUnlocked(getAchievementsEarned())
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
		if (source.isUnlocked(getAchievementsEarned()) && !redrawing)
		{
			source.setIcon(unlockedRewardIconDarkest);
		}
	}
	
	@Override
	public void mouseReleased(MouseEvent e) 
	{
		RewardStar source = (RewardStar)e.getComponent();
		if (source.isUnlocked(getAchievementsEarned()) && !redrawing)
		{
			source.setIcon(unlockedRewardIcon);
		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		if (arg0.getSource() == btnLeft) {
			currentPage--;
		} else {
			currentPage++;
		}

		updatePagination();
	}

	private void updatePagination() {
		btnLeft.setEnabled(currentPage > 0);
		btnRight.setEnabled(currentPage < pages.size());

		for (int i=0; i<pages.size(); i++) {
			var page = pages.get(i);
			page.setVisible(i == currentPage);

			if (i == currentPage) {
				title.setText(page.getTitle());
			}
		}
	}
}