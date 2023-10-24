package object;

import javax.swing.JLabel;

import util.StringUtil;

public class PlayerLabel extends JLabel 
{
	private String colour = "";
	private String playerName = "";
	
	public PlayerLabel(String text)
	{
		super(text);
	}
	
	public String getColour()
	{
		return colour;
	}
	public void setColour(String colour)
	{
		this.colour = colour;
		setText(playerName);
	}
	
	public String getPlayerName()
	{
		return playerName;
	}
	
	@Override
	public void setText(String arg0) 
	{
		playerName = arg0;
		super.setText("<html><font color=\"" + colour + "\">" + StringUtil.escapeHtml(playerName) + "</font></html>");
	}
}
