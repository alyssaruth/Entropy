package object;

public class OnlineUsername 
{
	private static final String MOBILE_PHONE_UNICODE = "\uD83D\uDCF1";
	
	private String username = "";
	private String colour = "";
	private int achievementCount = 0;
	private boolean mobile = false;
	
	public OnlineUsername(String username, String colour, int achievementCount, boolean mobile)
	{
		this.username = username;
		this.colour = colour;
		this.achievementCount = achievementCount;
		this.mobile = mobile;
	}
	
	public String getUsername()
	{
		return username;
	}
	public String getColour()
	{
		return colour;
	}
	public int getAchievementCount()
	{
		return achievementCount;
	}
	public boolean getMobile()
	{
		return mobile;
	}
	
	@Override
	public String toString()
	{
		String desc = username + " (" + achievementCount + ") ";
		
		if (mobile)
		{
			desc += MOBILE_PHONE_UNICODE;
		}
		
		return desc;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + achievementCount;
		result = prime * result + ((colour == null) ? 0 : colour.hashCode());
		result = prime * result
				+ ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof OnlineUsername))
			return false;
		OnlineUsername other = (OnlineUsername) obj;
		if (achievementCount != other.achievementCount)
			return false;
		if (colour == null) {
			if (other.colour != null)
				return false;
		} else if (!colour.equals(other.colour))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}
}
