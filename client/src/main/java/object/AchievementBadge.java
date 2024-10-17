package object;

import util.AchievementsUtil;
import util.Registry;
import utils.Achievement;

import javax.swing.*;
import java.awt.*;

import static util.AchievementUtilKt.LOCKED_ICON;

public class AchievementBadge extends JLabel
							  implements Registry
{
	private final String registryLocation;
	private final String name;
	private final String explanation;
	private final String description;
	
	public AchievementBadge(Achievement achievement)
	{
		setPreferredSize(new Dimension(56, 56));
		
		this.registryLocation = achievement.getRegistryLocation();
		this.name = achievement.getTitle();
		this.explanation = achievement.getExplanation();
		this.description = achievement.getDescription();

		if (achievements.getBoolean(registryLocation, false)) {
			setIcon(AchievementsUtil.getIconForAchievement(registryLocation));
		} else {
			setIcon(LOCKED_ICON);
		}
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
	public String getDescription()
	{
		return description;
	}
}
