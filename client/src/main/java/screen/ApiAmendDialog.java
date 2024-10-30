package screen;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import object.ApiStrategy;
import util.ApiUtil;
import util.DialogUtil;

public class ApiAmendDialog extends JDialog
							implements ActionListener
{
	private static final int DEFAULT_PORT_NUMBER = 1153;
	
	private ApiStrategy strategy = null;
	
	
	public ApiAmendDialog()
	{
		setTitle("API Setup");
		setSize(350, 300);
		setLocationRelativeTo(null);
		setResizable(false);
		setModal(true);
		
		getContentPane().add(okCancelPanel, BorderLayout.SOUTH);
		okCancelPanel.add(btnOk);
		okCancelPanel.add(btnCancel);
		getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(null);
		lblName.setBounds(10, 10, 100, 25);
		panel.add(lblName);
		lblGameMode.setBounds(10, 45, 100, 25);
		panel.add(lblGameMode);
		rdbtnEntropy.setBounds(110, 45, 80, 25);
		panel.add(rdbtnEntropy);
		rdbtnVectropy.setBounds(190, 45, 80, 25);
		panel.add(rdbtnVectropy);
		rdbtnBoth.setBounds(270, 45, 80, 25);
		panel.add(rdbtnBoth);
		btnGroupMode.add(rdbtnEntropy);
		btnGroupMode.add(rdbtnVectropy);
		btnGroupMode.add(rdbtnBoth);
		textFieldName.setBounds(110, 10, 120, 25);
		panel.add(textFieldName);
		textFieldName.setColumns(10);
		lblPort.setBounds(10, 80, 100, 25);
		panel.add(lblPort);
		textFieldPort.setBounds(110, 80, 80, 25);
		panel.add(textFieldPort);
		textFieldPort.setColumns(10);
		lblMessageType.setBounds(10, 115, 100, 25);
		panel.add(lblMessageType);
		rdbtnXml.setBounds(110, 116, 80, 23);
		panel.add(rdbtnXml);
		rdbtnJson.setBounds(190, 116, 109, 23);
		panel.add(rdbtnJson);
		btnGroupMessageType.add(rdbtnXml);
		btnGroupMessageType.add(rdbtnJson);
		btnTest.setBounds(120, 172, 89, 23);
		panel.add(btnTest);
		
		btnOk.addActionListener(this);
		btnCancel.addActionListener(this);
		btnTest.addActionListener(this);
	}
	
	private final JPanel okCancelPanel = new JPanel();
	private final JButton btnOk = new JButton("Ok");
	private final JButton btnCancel = new JButton("Cancel");
	private final JPanel panel = new JPanel();
	private final JLabel lblName = new JLabel("Name");
	private final JTextField textFieldName = new JTextField();
	private final JLabel lblGameMode = new JLabel("Game Mode");
	private final JRadioButton rdbtnEntropy = new JRadioButton("Entropy");
	private final JRadioButton rdbtnVectropy = new JRadioButton("Vectropy");
	private final JRadioButton rdbtnBoth = new JRadioButton("Both");
	private final ButtonGroup btnGroupMode = new ButtonGroup();
	private final JLabel lblPort = new JLabel("Port");
	private final JTextField textFieldPort = new JTextField();
	private final JLabel lblMessageType = new JLabel("Messaging");
	private final JRadioButton rdbtnXml = new JRadioButton(ApiUtil.MESSAGE_TYPE_XML);
	private final JRadioButton rdbtnJson = new JRadioButton(ApiUtil.MESSAGE_TYPE_JSON);
	private final ButtonGroup btnGroupMessageType = new ButtonGroup();
	private final JButton btnTest = new JButton("Test");
	
	public void init(ApiStrategy strategy)
	{
		this.strategy = strategy;
		
		if (strategy == null)
		{
			//Clear fields
			textFieldName.setText("");
			textFieldPort.setText("" + DEFAULT_PORT_NUMBER);
			rdbtnEntropy.setSelected(true);
			rdbtnXml.setSelected(true);
		}
		else
		{
			//Populate fields
			textFieldName.setText(strategy.getName());
			textFieldPort.setText("" + strategy.getPortNumber());
			
			boolean entropy = strategy.getEntropy();
			boolean vectropy = strategy.getVectropy();
			
			rdbtnEntropy.setSelected(entropy);
			rdbtnVectropy.setSelected(vectropy);
			rdbtnBoth.setSelected(entropy && vectropy);
			
			String messageType = strategy.getMessageType();
			rdbtnXml.setSelected(messageType.equals(ApiUtil.MESSAGE_TYPE_XML));
			rdbtnJson.setSelected(messageType.equals(ApiUtil.MESSAGE_TYPE_JSON));
		}
		
		//rdbtnJson.setEnabled(false);
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		JButton source = (JButton)arg0.getSource();
		if (source == btnOk)
		{
			if (valid())
			{
				saveData();
			}
		}
		else if (source == btnCancel)
		{
			dispose();
		}
		else if (source == btnTest)
		{
			sendTestMessage();
		}
	}
	
	private boolean valid()
	{
		String name = textFieldName.getText();
		if (name.isEmpty())
		{
			DialogUtil.showError("You must enter a name for this setup.");
			return false;
		}
		
		return true;
	}
	
	private void saveData()
	{
		if (strategy == null)
		{
			strategy = new ApiStrategy();
		}
		
		String name = textFieldName.getText();
		strategy.setName(name);
		
		String portStr = textFieldPort.getText();
		int port = Integer.parseInt(portStr);
		strategy.setPortNumber(port);
		
		boolean entropy = rdbtnEntropy.isSelected() || rdbtnBoth.isSelected();
		boolean vectropy = rdbtnVectropy.isSelected() || rdbtnBoth.isSelected();
		
		strategy.setEntropy(entropy);
		strategy.setVectropy(vectropy);
		
		if (rdbtnXml.isSelected())
		{
			strategy.setMessageType(ApiUtil.MESSAGE_TYPE_XML);
		}
		else
		{
			strategy.setMessageType(ApiUtil.MESSAGE_TYPE_JSON);
		}
		
		dispose();
	}
	
	private void sendTestMessage()
	{
		String infoMsg = "About to send a test message on port " + textFieldPort.getText()
					   + "\n\nEnsure that the third-party software is running and listening on this port.";
		
		DialogUtil.showInfo(infoMsg);
		
		String portStr = textFieldPort.getText();
		int port = Integer.parseInt(portStr);
		
		ApiUtil.sendTestMessage(port, rdbtnXml.isSelected());
	}
	
	public ApiStrategy getApiStrategy()
	{
		return strategy;
	}
	
	public static ApiStrategy createStrategy()
	{
		ApiAmendDialog dialog = new ApiAmendDialog();
		dialog.init(null);
		dialog.setVisible(true);
		
		return dialog.getApiStrategy();
	}
	
	public static void amendStrategy(ApiStrategy strategy)
	{
		ApiAmendDialog dialog = new ApiAmendDialog();
		dialog.init(strategy);
		dialog.setVisible(true);
	}
}
