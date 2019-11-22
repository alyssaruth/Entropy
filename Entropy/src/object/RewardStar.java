package object;

import javax.swing.Icon;
import javax.swing.JLabel;

@SuppressWarnings("serial")
public class RewardStar extends JLabel 
{
	private int threshold;
	private String hoverDesc;
	private String imageName;
	
	public RewardStar(int threshold, String hoverDesc, String imageName)
	{
		super();
		this.threshold = threshold;
		this.hoverDesc = hoverDesc;
		this.imageName = imageName;
	}
	
	public boolean isUnlocked(int achievementsEarned)
	{
		return achievementsEarned >= threshold;
	}
	
	public String getHoverDesc()
	{
		return hoverDesc;
	}
	
	public String getImageName()
	{
		return imageName;
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
