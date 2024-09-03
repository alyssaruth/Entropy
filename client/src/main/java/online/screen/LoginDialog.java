package online.screen;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import online.util.XmlBuilderClient;

import org.w3c.dom.Document;

import screen.ScreenCache;
import util.Debug;
import util.DialogUtil;
import util.EncryptionUtil;
import util.MessageUtil;

public class LoginDialog extends JDialog
						 implements MouseListener,
						 			ActionListener
{
	public LoginDialog() 
	{
		setTitle("Log In");
		setSize(268, 216);
		getRootPane().setDefaultButton(btnLogin);
		getContentPane().setLayout(null);
		JPanel panelOkCancel = new JPanel();
		panelOkCancel.setBounds(0, 144, 252, 33);
		getContentPane().add(panelOkCancel);
		panelOkCancel.add(btnLogin);
		panelOkCancel.add(btnCancel);
		lblUsername.setSize(80, 22);
		lblUsername.setLocation(10, 10);
		getContentPane().add(lblUsername);
		textFieldUsername.setLocation(100, 10);
		textFieldUsername.setSize(130, 22);
		textFieldUsername.setPreferredSize(new Dimension(100, 20));
		getContentPane().add(textFieldUsername);
		lblPassword.setLocation(10, 40);
		lblPassword.setSize(80, 22);
		getContentPane().add(lblPassword);
		passwordField.setLocation(100, 40);
		passwordField.setSize(130, 22);
		passwordField.setPreferredSize(new Dimension(100, 20));
		getContentPane().add(passwordField);
		lblCreateAccount.setHorizontalAlignment(SwingConstants.CENTER);
		lblCreateAccount.setLocation(66, 80);
		lblCreateAccount.setSize(120, 20);
		getContentPane().add(lblCreateAccount);
		lblCreateAccount.setForeground(Color.BLUE);
		lblCreateAccount.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblForgottenPassword.setHorizontalAlignment(SwingConstants.CENTER);
		lblForgottenPassword.setForeground(Color.BLUE);
		lblForgottenPassword.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblForgottenPassword.setBounds(51, 111, 150, 20);
		getContentPane().add(lblForgottenPassword);

		lblCreateAccount.addMouseListener(this);
		lblForgottenPassword.addMouseListener(this);
		btnLogin.addActionListener(this);
		btnCancel.addActionListener(this);
	}
	
	private final JLabel lblUsername = new JLabel("Username");
	private final JTextField textFieldUsername = new JTextField();
	private final JLabel lblPassword = new JLabel("Password");
	private final JPasswordField passwordField = new JPasswordField();
	private final JLabel lblCreateAccount = new JLabel("<html><u>Create Account</u></html>");
	private final JLabel lblForgottenPassword = new JLabel("<html><u>Forgotten Password</u></html>");
	private final JButton btnLogin = new JButton("Log In");
	private final JButton btnCancel = new JButton("Cancel");
	
	public void init()
	{
		textFieldUsername.setText("");
		passwordField.setText("");
		
		//have to do this again, not really sure why
		getRootPane().setDefaultButton(btnLogin);
	}
	
	private boolean valid()
	{
		String username = textFieldUsername.getText();
		if (username == null || username.isEmpty())
		{
			DialogUtil.showError("You must enter a username.");
			return false;
		}
		
		char[] input = passwordField.getPassword();
		if (input == null || input.length == 0)
		{
			DialogUtil.showError("You must enter a password.");
			return false;
		}
		
		return true;
	}
	
	private void sendLoginMessage()
	{
		String username = textFieldUsername.getText();
		
		char[] passwordArray = passwordField.getPassword();
		String password = new String(passwordArray);
		
		String hashedPassword = EncryptionUtil.sha256Hash(password);
		if (hashedPassword.isEmpty())
		{
			//something went wrong
			return;
		}
		
		boolean success = XmlBuilderClient.sendSymmetricKeyRequest();
		if (success)
		{
			ScreenCache.showConnectingDialog();
			Document connectRequest = XmlBuilderClient.factoryConnectionRequest(username, hashedPassword, false);
			MessageUtil.sendMessage(connectRequest, 0, 0);
		}
		else
		{
			DialogUtil.showErrorLater("Unable to initiate request.");
		}
	}
	
	private void resetPassword()
	{
		String username = "";
		while (username == null || username.isEmpty())
		{
			if (username == null)
			{
				return;
			}
			
			username = JOptionPane.showInputDialog(this, "Please enter your username", "Reset Password", JOptionPane.PLAIN_MESSAGE);
		}
		
		String email = "";
		while (email == null || email.isEmpty())
		{
			if (email == null)
			{
				return;
			}
			
			email = JOptionPane.showInputDialog(this, "Please enter the email for this account", "Reset Password", JOptionPane.PLAIN_MESSAGE);
		}
		
		boolean success = XmlBuilderClient.sendSymmetricKeyRequest();
		if (success)
		{
			ScreenCache.showConnectingDialog();
			Document resetPasswordRequest = XmlBuilderClient.factoryResetPasswordRequest(username, email);
			MessageUtil.sendMessage(resetPasswordRequest, 0, 0);
		}
		else
		{
			DialogUtil.showErrorLater("Unable to initiate request.");
		}
	}
	
	
	
	/**
	 * MouseListener
	 */
	@Override
	public void mouseClicked(MouseEvent arg0) 
	{
		Component source = (Component)arg0.getSource();
		if (source == lblCreateAccount)
		{
			dispose();
			
			NewAccountDialog dialog = ScreenCache.getNewAccountDialog();
			dialog.initFields();
			dialog.setLocationRelativeTo(null);
			dialog.setModal(true);
			dialog.setResizable(false);
			dialog.setVisible(true);
		}
		else if (source == lblForgottenPassword)
		{
			resetPassword();
		}
	}
	@Override
	public void mouseEntered(MouseEvent arg0) 
	{
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	}
	@Override
	public void mouseExited(MouseEvent arg0) 
	{
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}
	@Override
	public void mousePressed(MouseEvent arg0) {}
	@Override
	public void mouseReleased(MouseEvent arg0) {}

	/**
	 * ActionListener
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) 
	{
		Component source = (Component)arg0.getSource();
		if (source == btnLogin)
		{
			if (valid())
			{
				sendLoginMessage();
			}
		}
		else if (source == btnCancel)
		{
			dispose();
		}
		else
		{
			Debug.stackTrace("Unexpected source for ActionListener: " + source);
		}
	}
	
	public void disposeLater()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				dispose();
			}
		});
	}
}
