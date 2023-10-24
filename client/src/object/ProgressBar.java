package object;

import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

public class ProgressBar extends JProgressBar
{
	private int min;
	private int max;
	private int startValue;
	
	public ProgressBar(int min, int max, int startValue)
	{
		this.min = min;
		this.max = max;
		this.startValue = startValue;
		
		resetProgress();
	}
	
	private void resetProgress()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				setMinimum(min);
				setMaximum(max);
				setValue(startValue);
			}
		});
	}
	
	public void incrementProgressLater()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				int newValue = getValue() + 1;
				setValue(newValue);
				repaint();
			}
		});
	}
}
