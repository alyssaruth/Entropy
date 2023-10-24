package object;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class PulsingTextLabel extends JLabel
							  implements ActionListener
{
	private int saturationIndex = 0;
	private float[] saturationValues = new float[20];
	private Timer pulseTimer = new Timer(80, this);
	
	public PulsingTextLabel(String text)
	{
		super(text);
		buildSaturationValues();
		startPulsing();
	}
	
	private void buildSaturationValues()
	{
		for (double i=0; i<10; i++)
		{
			float value = (float)(0.5 + i/20);
			saturationValues[(int)i] = value;
		}
		
		for (double i=10; i<20; i++)
		{
			float value = (float)(1 - (i-10)/20);
			saturationValues[(int)i] = value;
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) 
	{
		if (isVisible())
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				@Override
				public void run()
				{
					if (saturationIndex < 19)
					{
						saturationIndex++;
					}
					else
					{
						saturationIndex = 0;
					}

					setForeground(Color.getHSBColor(0, saturationValues[saturationIndex], 1));
				}
			});
		}
	}
	
	public void stopPulsing()
	{
		pulseTimer.stop();
		setForeground(Color.getHSBColor(0, 1, 1));
	}
	
	public void startPulsing()
	{
		pulseTimer.setRepeats(true);
		pulseTimer.start();
	}
}
