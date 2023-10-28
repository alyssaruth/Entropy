package screen;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.NumberFormatter;

import org.w3c.dom.Document;

import object.ProgressBar;
import online.util.XmlBuilderClient;
import util.Debug;
import util.LoadTester;
import util.MessageUtil;

public class LoadTesterDialog extends JFrame
							  implements DocumentListener,
							  			 ActionListener
{
	private int numberOfThreads = 0;
	private int numberOfMessages = 0;
	private int threadsCompleted = 0;
	private long loadTestStartMillis = 0;
	
	public LoadTesterDialog() 
	{
		try
		{
			setTitle("Load Tester");
			setSize(400, 320);
			setResizable(false);
			getContentPane().setLayout(null);
			textFieldNumberOfThreads.setBounds(36, 30, 80, 20);
			getContentPane().add(textFieldNumberOfThreads);
			textFieldNumberOfThreads.setValue(10);
			
			textFieldNumberOfMessages.setBounds(36, 61, 80, 20);
			getContentPane().add(textFieldNumberOfMessages);
			textFieldNumberOfMessages.setValue(100);
			
			JLabel lblThreads = new JLabel("Threads");
			lblThreads.setFont(new Font("Tahoma", Font.PLAIN, 11));
			lblThreads.setBounds(126, 30, 80, 20);
			getContentPane().add(lblThreads);
			
			JLabel lblMessages = new JLabel("Messages");
			lblMessages.setFont(new Font("Tahoma", Font.PLAIN, 11));
			lblMessages.setBounds(126, 61, 80, 20);
			getContentPane().add(lblMessages);
			
			JLabel label = new JLabel("}");
			label.setFont(new Font("Tahoma", Font.PLAIN, 32));
			label.setBounds(200, 27, 27, 51);
			getContentPane().add(label);
			
			
			lblTotalMessages.setBounds(220, 45, 175, 20);
			getContentPane().add(lblTotalMessages);
			
			scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			scrollPane.setBounds(21, 156, 340, 100);
			getContentPane().add(scrollPane);
			
			progressPanel.setPreferredSize(new Dimension(320, 50));
			scrollPane.setViewportView(progressPanel);
			progressPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			
			btnLoadTest.setBounds(217, 110, 107, 23);
			getContentPane().add(btnLoadTest);
			
			lblLoadTestTime.setHorizontalAlignment(SwingConstants.CENTER);
			lblLoadTestTime.setBounds(91, 266, 200, 20);
			getContentPane().add(lblLoadTestTime);
			btnAssignKey.setBounds(60, 110, 107, 23);
			getContentPane().add(btnAssignKey);
			
			init();
			
			btnLoadTest.addActionListener(this);
			btnAssignKey.addActionListener(this);
			textFieldNumberOfThreads.getDocument().addDocumentListener(this);
			textFieldNumberOfMessages.getDocument().addDocumentListener(this);
		}
		catch (Throwable t)
		{
			Debug.stackTrace(t);
		}
	}
	
	private final JFormattedTextField textFieldNumberOfThreads = new JFormattedTextField(getIntegerFormat());
	private final JFormattedTextField textFieldNumberOfMessages = new JFormattedTextField(getIntegerFormat());
	private final JLabel lblTotalMessages = new JLabel("1 message(s)");
	private final JButton btnAssignKey = new JButton("Assign Key");
	private final JButton btnLoadTest = new JButton("Load Test");
	private final JScrollPane scrollPane = new JScrollPane();
	private final JPanel progressPanel = new JPanel();
	private final JLabel lblLoadTestTime = new JLabel("");
	
	private NumberFormatter getIntegerFormat()
	{
		NumberFormatter integerFormat = new NumberFormatter();
		integerFormat.setMinimum(1);
		integerFormat.setAllowsInvalid(false);
		
		return integerFormat;
	}
	
	public void init()
	{
		setButtonsEnabled();
		updateTotalMessagesLabel();
	}
	
	public void setButtonsEnabled()
	{
		if (MessageUtil.symmetricKey != null)
		{
			btnAssignKey.setEnabled(false);
			btnLoadTest.setEnabled(true);
		}
		else
		{
			btnAssignKey.setEnabled(true);
			btnLoadTest.setEnabled(false);
		}
	}

	private void updateTotalMessagesLabel()
	{
		int numberOfThreads = (int)textFieldNumberOfThreads.getValue();
		int numberOfMessages = (int)textFieldNumberOfMessages.getValue();
		
		int totalMessages = numberOfThreads * numberOfMessages;
		
		String totalMessagesStr = getIntegerFormat().getFormat().format(totalMessages);
		
		String text = totalMessagesStr + " message";
		if (totalMessages > 1)
		{
			text += "s";
		}
		
		lblTotalMessages.setText(text);
	}
	
	private void doLoadTest()
	{
		synchronized (this)
		{
			loadTestStartMillis = System.currentTimeMillis();
			threadsCompleted = 0;
			numberOfThreads = (int)textFieldNumberOfThreads.getValue();
			numberOfMessages = (int)textFieldNumberOfMessages.getValue();
			
			btnLoadTest.setEnabled(false);
			textFieldNumberOfMessages.setEnabled(false);
			textFieldNumberOfThreads.setEnabled(false);
			
			progressPanel.removeAll();
			progressPanel.setPreferredSize(new Dimension(310, numberOfThreads*26));
			scrollPane.validate();
			scrollPane.repaint();
			
			Document message = XmlBuilderClient.factoryHeartbeat("Alex");
			
			for (int i=0; i<numberOfThreads; i++)
			{
				JLabel label = new JLabel("Thread " + (i+1));
				label.setPreferredSize(new Dimension(80, 20));
				ProgressBar progressBar = new ProgressBar(0, numberOfMessages, 0);
				progressBar.setPreferredSize(new Dimension(220, 20));
				
				progressPanel.add(label);
				progressPanel.add(progressBar);
				progressPanel.validate();
				progressPanel.repaint();
				
				LoadTester sender = new LoadTester(message, numberOfMessages, progressBar);
				Thread senderThread = new Thread(sender, "MessageSender" + i);
				senderThread.start();
			}
		}
	}
	
	public void threadFinished()
	{
		synchronized (this)
		{
			threadsCompleted++;
			if (numberOfThreads == threadsCompleted)
			{
				long loadTestFinishMillis = System.currentTimeMillis();
				long timeTakenMillis = loadTestFinishMillis - loadTestStartMillis;
				
				lblLoadTestTime.setText("Load test complete in " + (timeTakenMillis/1000) + "s");
				
				btnLoadTest.setEnabled(true);
				textFieldNumberOfMessages.setEnabled(true);
				textFieldNumberOfThreads.setEnabled(true);
			}
		}
	}
	
	@Override
	public void changedUpdate(DocumentEvent arg0) 
	{
		updateTotalMessagesLabel();
	}

	@Override
	public void insertUpdate(DocumentEvent arg0) 
	{
		updateTotalMessagesLabel();
	}

	@Override
	public void removeUpdate(DocumentEvent arg0) 
	{
		updateTotalMessagesLabel();
	}

	@Override
	public void actionPerformed(ActionEvent arg0) 
	{
		JButton source = (JButton)arg0.getSource();
		if (source == btnLoadTest)
		{
			doLoadTest();
		}
		else if (source == btnAssignKey)
		{
			ScreenCache.showConnectingDialog();
			
			boolean success = XmlBuilderClient.sendSymmetricKeyRequest();
			
			ScreenCache.dismissConnectingDialog();
			
			if (success)
			{
				setButtonsEnabled();
			}
		}
		else
		{
			Debug.stackTrace("Unexpected source of actionPerformed: " + source);
		}
	}
}
