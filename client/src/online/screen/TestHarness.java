package online.screen;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;

import util.Debug;
import util.MessageSenderParams;
import util.MessageUtil;

public class TestHarness extends JFrame
						 implements ActionListener
{
	public TestHarness() 
	{	
		try
		{
			setTitle("Test Harness");
			setSize(451, 390);
			getRootPane().setDefaultButton(btnSend);
			JPanel panel = new JPanel();
			getContentPane().add(panel, BorderLayout.SOUTH);
			panel.add(btnSend);
			JScrollPane scrollPane = new JScrollPane();
			getContentPane().add(scrollPane, BorderLayout.CENTER);
			textPane.setLineWrap(true);
			scrollPane.setViewportView(textPane);
			
			spinner.setModel(new SpinnerNumberModel(5, 1, 5, 1));
			lblTimes.setEnabled(false);
			spinner.setEnabled(false);
			
			getContentPane().add(panel_1, BorderLayout.NORTH);
			panel_1.add(chckbxIgnoreResponse);
			panel_1.add(chckbxEncrypt);
			panel_1.add(chckbxRetry);
			panel_1.add(spinner);
			panel_1.add(lblTimes);
			
			chckbxRetry.addActionListener(this);
			btnSend.addActionListener(this);
		}
		catch (Throwable t)
		{
			Debug.stackTrace(t);
		}
	}
	
	private final JTextArea textPane = new JTextArea();
	private final JButton btnSend = new JButton("Send");
	private final JCheckBox chckbxIgnoreResponse = new JCheckBox("Ignore Response");
	private final JPanel panel_1 = new JPanel();
	private final JCheckBox chckbxEncrypt = new JCheckBox("Encrypt");
	private final JCheckBox chckbxRetry = new JCheckBox("Retry");
	private final JSpinner spinner = new JSpinner();
	private final JLabel lblTimes = new JLabel("time(s)");
	
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		Object source = e.getSource();
		if (source == btnSend)
		{
			String xmlStr = textPane.getText();
			boolean retry = chckbxRetry.isSelected();
			int retries = 0;
			if (retry)
			{
				retries = (int)spinner.getValue();
			}
			
			MessageSenderParams params = new MessageSenderParams(xmlStr, 0, retries);
			params.setIgnoreResponse(chckbxIgnoreResponse.isSelected());
			MessageUtil.sendMessage(params, chckbxEncrypt.isSelected());
		}
		else if (source == chckbxRetry)
		{
			boolean retry = chckbxRetry.isSelected();
			lblTimes.setEnabled(retry);
			spinner.setEnabled(retry);
		}
		else
		{
			Debug.stackTrace("Unexpected actionPerformed: " + source);
		}
	}
}
