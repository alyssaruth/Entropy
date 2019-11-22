package online.screen;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import object.LimitedDocument;
import online.util.AccountUtil;
import online.util.XmlBuilderClient;

import org.w3c.dom.Document;

import screen.ScreenCache;
import util.Debug;
import util.DialogUtil;
import util.EncryptionUtil;
import util.MessageUtil;

public class NewAccountDialog extends JDialog
							  implements ActionListener
{
	public NewAccountDialog() 
	{
		try
		{
			setTitle("Create Account");
			setSize(290, 223);
			getRootPane().setDefaultButton(btnOk);
			getContentPane().setLayout(null);
			JPanel okCancelPanel = new JPanel();
			okCancelPanel.setBounds(0, 140, 284, 33);
			getContentPane().add(okCancelPanel);
			okCancelPanel.add(btnOk);
			okCancelPanel.add(btnCancel);
			lblUsername.setBounds(10, 10, 120, 22);
			getContentPane().add(lblUsername);
			textFieldUsername.setBounds(130, 10, 130, 22);
			textFieldUsername.setPreferredSize(new Dimension(120, 20));
			LimitedDocument document = new LimitedDocument(10);
			textFieldUsername.setDocument(document);
			getContentPane().add(textFieldUsername);
			lblPassword.setBounds(10, 40, 120, 22);
			getContentPane().add(lblPassword);
			LimitedDocument passwordDocument = new LimitedDocument(20);
			passwordField1.setBounds(130, 40, 130, 22);
			passwordField1.setDocument(passwordDocument);
			passwordField1.setPreferredSize(new Dimension(120, 20));
			getContentPane().add(passwordField1);
			lblConfirmPassword.setBounds(10, 70, 120, 22);
			getContentPane().add(lblConfirmPassword);
			LimitedDocument passwordDocument2 = new LimitedDocument(20);
			passwordField2.setBounds(130, 70, 130, 22);
			passwordField2.setDocument(passwordDocument2);
			passwordField2.setPreferredSize(new Dimension(120, 20));
			getContentPane().add(passwordField2);
			
			lblEmail.setBounds(10, 100, 120, 22);
			getContentPane().add(lblEmail);
			textFieldEmail.setBounds(130, 100, 130, 22);
			textFieldEmail.setPreferredSize(new Dimension(120, 20));
			textFieldEmail.setDocument(new LimitedDocument(80));
			getContentPane().add(textFieldEmail);		
			btnOk.addActionListener(this);
			btnCancel.addActionListener(this);
		}
		catch (Throwable t)
		{
			Debug.stackTrace(t);
		}
	}
	
	private final JLabel lblUsername = new JLabel("Username");
	private final JTextField textFieldUsername = new JTextField();
	private final JLabel lblPassword = new JLabel("Password");
	private final JPasswordField passwordField1 = new JPasswordField();
	private final JLabel lblConfirmPassword = new JLabel("Confirm Password");
	private final JPasswordField passwordField2 = new JPasswordField();
	private final JButton btnOk = new JButton("Ok");
	private final JButton btnCancel = new JButton("Cancel");
	private final JLabel lblEmail = new JLabel("Email Address");
	private final JTextField textFieldEmail = new JTextField();

	public void initFields()
	{
		textFieldUsername.setText("");
		passwordField1.setText("");
		passwordField2.setText("");
		textFieldEmail.setText("");
		
		getRootPane().setDefaultButton(btnOk);
	}
	
	private boolean valid()
	{
		String username = textFieldUsername.getText();
		if (username == null || username.trim().isEmpty())
		{
			DialogUtil.showError("You must enter a username.");
			return false;
		}
		
		int length = username.length();
		if (length < 3)
		{
			DialogUtil.showError("The username must be at least 3 characters long.");
			return false;
		}
		
		char[] input1 = passwordField1.getPassword();
		if (input1 == null || input1.length == 0)
		{
			DialogUtil.showError("You must enter a password.");
			return false;
		}
		
		char[] input2 = passwordField2.getPassword();
		if (input2 == null || input2.length == 0)
		{
			DialogUtil.showError("You must confirm your password.");
			return false;
		}
		
		if (!AccountUtil.passwordsMatch(input1, input2))
		{
			DialogUtil.showError("The two passwords do not match.");
			return false;
		}
		
		if (!AccountUtil.passwordStrongEnough(input1))
		{
			return false;
		}
		
		String email = textFieldEmail.getText();
		if (!validEmail(email))
		{
			return false;
		}
		
		return true;
	}
	
	private boolean validEmail(String email)
	{
		if (email == null || email.isEmpty())
		{
			String question = "Are you sure you want to proceed without an email address?";
			question += "\n\nIf you forget your password you will not be able to regain access to your account.";
			int option = DialogUtil.showQuestion(question, false);
			return option == JOptionPane.YES_OPTION;
		}
		
		return AccountUtil.validateEmail(email);
	}
	
	private void sendNewAccountRequest()
	{
		String username = textFieldUsername.getText();
		
		char[] passwordArray = passwordField1.getPassword();
		String password = new String(passwordArray);
		
		String hashedPassword = EncryptionUtil.sha256Hash(password);
		if (hashedPassword.isEmpty())
		{
			//something went wrong
			return;
		}
		
		String email = textFieldEmail.getText();
		
		boolean success = XmlBuilderClient.sendSymmetricKeyRequest();
		if (success)
		{
			ScreenCache.showConnectingDialog();
			Document newAccountRequest = XmlBuilderClient.factoryNewAccountRequest(username, hashedPassword, email);
			MessageUtil.sendMessage(newAccountRequest, 0, 0);
		}
		else
		{
			DialogUtil.showErrorLater("Unable to initiate request.");
		}
	}
	
	/**
	 * ActionListener
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) 
	{
		Component source = (Component)arg0.getSource();
		if (source == btnOk)
		{
			if (valid())
			{
				sendNewAccountRequest();
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
