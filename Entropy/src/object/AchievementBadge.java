package object;

import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import screen.AchievementBadges;
import util.AchievementsUtil;
import util.Registry;

@SuppressWarnings("serial")
public class AchievementBadge extends JLabel
							  implements Registry
{
	private String registryLocation;
	private String name;
	private String explanation;
	private String description;
	private ImageIcon earnedIcon;
	
	public AchievementBadge(String registryLocation)
	{
		super(AchievementBadges.lockedIcon);
		
		setPreferredSize(new Dimension(56, 56));
		
		this.registryLocation = registryLocation;
		this.name = AchievementsUtil.getAchievementName(registryLocation);
		this.earnedIcon = AchievementsUtil.getIconForAchievement(registryLocation);
	}
	
	@Override
	public String toString() 
	{
		return name;
	}
	
	public boolean isEarned() 
	{
		return achievements.getBoolean(registryLocation, false);
	}
	@Override
	public String getName() 
	{
		return name;
	}
	public String getExplanation() 
	{
		return explanation;
	}
	public void setExplanation(String explanation)
	{
		this.explanation = explanation;
	}
	public String getDescription() 
	{
		return description;
	}
	public void setDescription(String description)
	{
		this.description = description;
	}
	public ImageIcon getEarnedIcon()
	{
		return earnedIcon;
	}
	public String getRegistryLocation()
	{
		return registryLocation;
	}
}
