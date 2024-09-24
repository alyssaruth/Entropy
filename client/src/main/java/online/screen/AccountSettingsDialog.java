package online.screen;

import util.Debug;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AccountSettingsDialog extends JDialog
								   implements ActionListener
{
	private String username;
	
	public AccountSettingsDialog(String username)
	{
		this.username = username;

		getRootPane().setDefaultButton(btnOk);
		setTitle("Account Settings");
		setSize(300, 150);
		setResizable(false);
		getContentPane().setLayout(null);
		textFieldEmail.setBounds(86, 40, 198, 22);
		getContentPane().add(textFieldEmail);
		textFieldEmail.setColumns(10);
		JLabel lblUsername = new JLabel("Username");
		lblUsername.setBounds(10, 10, 66, 22);
		getContentPane().add(lblUsername);
		textFieldUsername.setBounds(86, 10, 198, 22);
		getContentPane().add(textFieldUsername);
		textFieldUsername.setColumns(10);
		JLabel lblEmail = new JLabel("Email");
		lblEmail.setBounds(10, 40, 46, 22);
		getContentPane().add(lblEmail);
		okCancelPanel.setLocation(0, 77);
		okCancelPanel.setSize(294, 33);
		getContentPane().add(okCancelPanel);
		okCancelPanel.add(btnOk);
		okCancelPanel.add(btnCancel);
		textFieldUsername.setEditable(false);
		textFieldEmail.setEditable(false);

		btnOk.addActionListener(this);
		btnCancel.addActionListener(this);

		init();
	}
	
	private final JTextField textFieldUsername = new JTextField();
	private final JTextField textFieldEmail = new JTextField();
	private final JPanel okCancelPanel = new JPanel();
	private final JButton btnOk = new JButton("Ok");
	private final JButton btnCancel = new JButton("Cancel");
	
	private void init()
	{
		textFieldUsername.setText(username);
		textFieldEmail.setText("");
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) 
	{
		Component source = (Component)arg0.getSource();
		if (source == btnOk)
		{
			dispose();
		}
		else if (source == btnCancel)
		{
			dispose();
		}
		else
		{
			Debug.stackTrace("Unexpected actionPerformed: " + source);
		}
	}
}
