package object;

public class OnlineMessage 
{
	private String colour;
	private String text;
	private String username;
	
	public OnlineMessage(String colour, String text, String username)
	{
		this.colour = colour;
		this.text = text;
		this.username = username;
	}
	
	public String getColour()
	{
		return colour;
	}
	public String getText()
	{
		return text;
	}
	public String getUsername()
	{
		return username;
	}
	
	@Override
	public String toString()
	{
		return username + ": " + text;
	}
}
