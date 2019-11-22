package screen;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

public class BackgroundPanel extends JPanel
{
	private Image bgImage = null;
	
	@Override
	public void paintComponent(Graphics g)
	{
		if (bgImage == null)
		{
			super.paintComponent(g);
		}
		else
		{
			g.drawImage(bgImage, 0, 0, null);
		}
	}
	
	public void setBgImage(Image bgImage)
	{
		this.bgImage = bgImage;
		repaint();
	}
}
