package screen;

import java.awt.Component;
import java.util.ArrayList;

import javax.swing.JPanel;

import object.AchievementBadge;

public class AchievementsPanel extends JPanel
{
	String panelName = "";
	
	public AchievementsPanel(String panelName)
	{
		super();
		
		this.panelName = panelName;
	}
	
	public ArrayList<AchievementBadge> getAchievementBadges()
	{
		ArrayList<AchievementBadge> ret = new ArrayList<>();
		
		Component[] components = getComponents();
		for (int i=0; i<components.length; i++)
		{
			Component c = components[i];
			if (c instanceof AchievementBadge)
			{
				AchievementBadge badge = (AchievementBadge)c;
				ret.add(badge);
			}
		}
		
		return ret;
	}
	
	public String getPageTitle()
	{
		ArrayList<AchievementBadge> badges = getAchievementBadges();
		int totalForPage = badges.size();
		
		int totalUnlocked = 0;
		for (int i=0; i<badges.size(); i++)
		{
			AchievementBadge badge = badges.get(i);
			if (badge.isEarned())
			{
				totalUnlocked++;
			}
		}
		
		return panelName + " (" + totalUnlocked + "/" + totalForPage + ")";
	}
}
