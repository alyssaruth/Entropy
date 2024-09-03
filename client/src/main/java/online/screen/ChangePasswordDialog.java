package online.screen;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.SwingUtilities;

import online.util.AccountUtil;
import online.util.XmlBuilderClient;

import org.w3c.dom.Document;

import util.Debug;
import util.DialogUtil;
import util.EncryptionUtil;
import util.MessageUtil;

public class ChangePasswordDialog extends JDialog
								  implements ActionListener
{
	private String username = "";
	private boolean allowCancel = true;
	
	public ChangePasswordDialog(String username, boolean allowCancel) 
	{
		this.username = username;
		this.allowCancel = allowCancel;

		setTitle("Change Password");
		getRootPane().setDefaultButton(btnOk);
		setSize(300, 200);
		setResizable(false);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		getContentPane().setLayout(null);
		lblCurrentPassword.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblCurrentPassword.setBounds(10, 10, 140, 22);
		getContentPane().add(lblCurrentPassword);
		passwordFieldOld.setBounds(150, 10, 130, 22);
		getContentPane().add(passwordFieldOld);
		lblNewPassword.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblNewPassword.setBounds(10, 40, 140, 22);
		getContentPane().add(lblNewPassword);
		passwordFieldNew.setBounds(150, 40, 130, 22);
		getContentPane().add(passwordFieldNew);
		lblConfirmNewPassword.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblConfirmNewPassword.setBounds(10, 70, 140, 22);
		getContentPane().add(lblConfirmNewPassword);
		passwordFieldConfirmNew.setBounds(150, 70, 130, 22);
		getContentPane().add(passwordFieldConfirmNew);
		buttonPanel.setBounds(0, 115, 294, 33);
		getContentPane().add(buttonPanel);
		buttonPanel.add(btnOk);
		buttonPanel.add(btnCancel);

		btnOk.addActionListener(this);
		btnCancel.addActionListener(this);

		init();
	}
	
	private final JLabel lblCurrentPassword = new JLabel("Current Password");
	private final JPasswordField passwordFieldOld = new JPasswordField();
	private final JLabel lblNewPassword = new JLabel("New Password");
	private final JPasswordField passwordFieldNew = new JPasswordField();
	private final JLabel lblConfirmNewPassword = new JLabel("Confirm New Password");
	private final JPasswordField passwordFieldConfirmNew = new JPasswordField();
	private final JPanel buttonPanel = new JPanel();
	private final JButton btnOk = new JButton("Ok");
	private final JButton btnCancel = new JButton("Cancel");
	
	private void init()
	{
		btnCancel.setEnabled(allowCancel);
		
		if (!allowCancel)
		{
			btnCancel.setToolTipText("You must reset your password at this time.");
		}
	}
	
	private boolean valid()
	{
		char[] oldPass = passwordFieldOld.getPassword();
		if (oldPass == null || oldPass.length == 0)
		{
			DialogUtil.showError("You must enter your current password.");
			return false;
		}
		
		char[] newPass = passwordFieldNew.getPassword();
		if (newPass == null || newPass.length == 0)
		{
			DialogUtil.showError("You must enter a new password.");
			return false;
		}
		
		char[] newPassConfirmed = passwordFieldConfirmNew.getPassword();
		if (newPassConfirmed == null || newPassConfirmed.length == 0)
		{
			DialogUtil.showError("You must confirm your new password.");
			return false;
		}
		
		if (!AccountUtil.passwordsMatch(newPass, newPassConfirmed))
		{
			DialogUtil.showError("The two passwords do not match.");
			return false;
		}
		
		if (AccountUtil.passwordsMatch(oldPass, newPass))
		{
			DialogUtil.showError("Your new password cannot be the same as your current one.");
			return false;
		}
		
		if (!AccountUtil.passwordStrongEnough(newPass))
		{
			return false;
		}
		
		return true;
	}
	
	private void sendChangePasswordRequest()
	{
		char[] passwordArrayOld = passwordFieldOld.getPassword();
		String passwordOld = new String(passwordArrayOld);
		
		char[] passwordArrayNew = passwordFieldNew.getPassword();
		String passwordNew = new String(passwordArrayNew);
		
		String passwordHashOld = EncryptionUtil.sha256Hash(passwordOld);
		String passwordHashNew = EncryptionUtil.sha256Hash(passwordNew);
		if (passwordHashOld.isEmpty() || passwordHashNew.isEmpty())
		{
			//something went wrong
			return;
		}
		
		Document request = XmlBuilderClient.factoryChangePasswordRequest(username, passwordHashOld, passwordHashNew);
		MessageUtil.sendMessage(request, 0, 0);
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) 
	{
		Component source = (Component)arg0.getSource();
		if (source == btnOk)
		{
			if (valid())
			{
				sendChangePasswordRequest();
			}
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
