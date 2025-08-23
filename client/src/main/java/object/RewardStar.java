package object;

import achievement.Reward;

import javax.swing.Icon;
import javax.swing.JLabel;

@SuppressWarnings("serial")
public class RewardStar extends JLabel 
{
	private String hoverDesc;
	private Reward reward;
	
	public RewardStar(String hoverDesc, Reward reward)
	{
		super();
		this.hoverDesc = hoverDesc;
		this.reward = reward;
	}
	
	public boolean isUnlocked(int achievementsEarned)
	{
		return achievementsEarned >= reward.getThreshold();
	}
	
	public String getHoverDesc()
	{
		return hoverDesc;
	}
	
	public Reward getReward()
	{
		return reward;
	}
	
	@Override
	public void setIcon(Icon icon)
	{
		Icon currentIcon = getIcon();
		
		if (currentIcon == null || !icon.equals(currentIcon))
		{
			super.setIcon(icon);
		}
	}
	
	@Override
	public void setToolTipText(String text)
	{
		String currentText = getToolTipText();
		
		if (currentText == null || !text.equals(currentText))
		{
			super.setToolTipText(text);
		}
	}
}
