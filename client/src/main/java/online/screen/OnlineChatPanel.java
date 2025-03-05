package online.screen;

import achievement.AchievementUtilKt;
import http.dto.OnlineMessage;
import object.LimitedDocument;
import screen.ReplayDialog;
import util.Registry;
import util.StringUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.prefs.Preferences;

import static util.ClientGlobals.chatApi;

public class OnlineChatPanel extends JPanel
						     implements ActionListener, Registry
{
	private String roomId = null;
	
	private int wrapWidth = 234;
	
	private boolean initted = false;
	
	public OnlineChatPanel(String roomId, boolean readOnly)
	{
		this.roomId = roomId;
		
		setLayout(new BorderLayout(0, 0));
		add(scrollPane, BorderLayout.CENTER);
		chatBox.setCellRenderer(messageRenderer);
		chatBox.setFont(chatBox.getFont().deriveFont(Font.PLAIN));
		scrollPane.setViewportView(chatBox);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBar(null);
		
		if (readOnly)
		{
			add(textField, BorderLayout.SOUTH);
			textField.setDocument(new LimitedDocument(100));
			textField.addActionListener(this);
		}
	}
	
	private final JScrollPane scrollPane = new JScrollPane();
	private final DefaultListModel<OnlineMessage> listmodel = new DefaultListModel<>();
	private final OnlineMessageRenderer messageRenderer = new OnlineMessageRenderer();
	private final JList<OnlineMessage> chatBox = new JList<>(listmodel);
	private final JTextField textField = new JTextField();
	
	public void init()
	{
		synchronized (this)
		{
			clear();
			initted = true;
		}
	}
	
	public void clear()
	{
		initted = false;
		listmodel.removeAllElements();
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) 
	{
		String text = textField.getText();
		if (text.isEmpty())
		{
			return;
		}
		
		textField.setText("");

		chatApi.sendChat(text, roomId);

		AchievementUtilKt.updateAndUnlockChatty();
	}
	
	public void updateChatBox(List<OnlineMessage> messages)
	{
		for (int i=0; i<messages.size(); i++)
		{
			OnlineMessage message = messages.get(i);
			updateChatBox(message);
		}
	}
	
	public void updateChatBox(final OnlineMessage message)
	{
		Runnable updateRunnable = new Runnable()
		{
			@Override
			public void run() 
			{
				listmodel.addElement(message);
				chatBox.ensureIndexIsVisible(listmodel.size() - 1);
			}
		};
		
		SwingUtilities.invokeLater(updateRunnable);
	}
	
	public void scrollToBottom()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				chatBox.ensureIndexIsVisible(listmodel.size() - 1);
			}
		});
	}
	
	public boolean getInitted()
	{
		return initted;
	}
	
	public void saveRecentChat(Preferences replay, int roundNumber)
	{
		int startIndex = listmodel.size() - 1;
		
		for (int i=startIndex; i >= startIndex-ReplayDialog.RECENT_CHAT_MESSAGES_TO_SHOW; i--)
		{
			if (i < 0)
			{
				return;
			}
			
			int messageNumber = startIndex - i;
			OnlineMessage message = listmodel.get(i);
			String messageColour = message.getColour();
			String messageUsername = message.getUsername();
			String text = message.getText();
			
			replay.put(roundNumber + Registry.REPLAY_STRING_CHAT_COLOUR + messageNumber, messageColour);
			replay.put(roundNumber + Registry.REPLAY_STRING_CHAT_USERNAME + messageNumber, messageUsername);
			replay.put(roundNumber + Registry.REPLAY_STRING_CHAT_CONTENT + messageNumber, text);
			
		}
	}
	
	public void setWrapWidth(int wrapWidth)
	{
		this.wrapWidth = wrapWidth;
	}
	
	class OnlineMessageRenderer extends DefaultListCellRenderer 
	{
		@Override
		@SuppressWarnings("rawtypes")
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) 
		{
			OnlineMessage message = (OnlineMessage)value;
			String messageText = message.getText();
			String messageUsername = message.getUsername();
			String messageColour = message.getColour();

			String text = "<html><body style='width: " + wrapWidth + "px'>";
			text += "<font color=\"";
			text += StringUtil.escapeHtml(messageColour);
			text += "\"><b>";
			text += StringUtil.escapeHtml(messageUsername);
			text += "</b>: ";
			text += StringUtil.escapeHtml(messageText);
			text += "</font></html>";

			return super.getListCellRendererComponent(list, text, index, isSelected,
					cellHasFocus);
		}
	}
	
	public void setBackgroundColour(Color arg0) 
	{
		chatBox.setBackground(arg0);
		textField.setBackground(arg0);
	}
}
