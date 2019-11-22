package online.screen;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public class ConnectingDialog extends JDialog
{
	public ConnectingDialog()
	{
		setSize(200, 100);
		setLocationRelativeTo(null);
		setResizable(false);
		setModal(true);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		
		JLabel lblNewLabel = new JLabel("Communicating with Server...");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		getContentPane().add(lblNewLabel, BorderLayout.CENTER);
	}
	
	public void showDialog()
	{
		Runnable showRunnable = new Runnable()
		{
			@Override
			public void run()
			{
				setVisible(true);
			}
		};
		
		SwingUtilities.invokeLater(showRunnable);
	}
	
	public void dismissDialog()
	{
		Runnable hideRunnable = new Runnable()
		{
			@Override
			public void run()
			{
				setVisible(false);
			}
		};
		
		SwingUtilities.invokeLater(hideRunnable);
	}
}
