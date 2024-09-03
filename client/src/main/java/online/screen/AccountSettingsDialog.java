package online.screen;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import online.util.AccountUtil;
import online.util.XmlBuilderClient;

import org.w3c.dom.Document;

import screen.ScreenCache;
import util.Debug;
import util.DialogUtil;
import util.MessageUtil;

public class AccountSettingsDialog extends JDialog
								   implements ActionListener
{
	private String username = "";
	private String email = "";
	
	public AccountSettingsDialog(String username, String email) 
	{
		this.username = username;
		this.email = email;

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
		okCancelPanel.add(btnChangePassword);
		okCancelPanel.add(btnCancel);
		textFieldUsername.setEditable(false);
		textFieldEmail.setEditable(false);

		btnChangePassword.addActionListener(this);
		btnOk.addActionListener(this);
		btnCancel.addActionListener(this);

		init();
	}
	
	private final JTextField textFieldUsername = new JTextField();
	private final JTextField textFieldEmail = new JTextField();
	private final JPanel okCancelPanel = new JPanel();
	private final JButton btnOk = new JButton("Ok");
	private final JButton btnChangePassword = new JButton("Change Password");
	private final JButton btnCancel = new JButton("Cancel");
	
	private void init()
	{
		textFieldUsername.setText(username);
		textFieldEmail.setText(email);
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) 
	{
		Component source = (Component)arg0.getSource();
		if (source == btnChangePassword)
		{
			ChangePasswordDialog dialog = new ChangePasswordDialog(username, true);
			ScreenCache.setChangePasswordDialog(dialog);
			dialog.setModal(true);
			dialog.setLocationRelativeTo(this);
			dialog.setVisible(true);
		}
		else if (source == btnOk)
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
