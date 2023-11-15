package screen;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JPanel;

public class TransparentPanel extends JPanel
{
	private int alpha = 0;
	
	public TransparentPanel()
	{
		setOpaque(false);
	}
	
	public TransparentPanel(int alpha)
	{
		this.alpha = alpha;
		setOpaque(false);
	}
	
	
	@Override
	public Component add(Component arg0)
	{
		if (arg0 instanceof JComponent)
		{
			JComponent jComp = (JComponent)arg0;
			jComp.setBackground(new Color(0, 0, 0, alpha));
			jComp.setOpaque(alpha > 0);
			return super.add(jComp);
		}
		
		return super.add(arg0);
	}
	
	@Override
	public void add(Component arg0, Object arg1)
	{
		if (arg0 instanceof JComponent)
		{
			JComponent jComp = (JComponent)arg0;
			jComp.setBackground(new Color(0, 0, 0, alpha));
			jComp.setOpaque(alpha > 0);
			super.add(jComp, arg1);
		}
		else
		{
			super.add(arg0, arg1);
		}
	}
}
