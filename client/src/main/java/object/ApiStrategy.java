package object;

/**
 * Wrapper class for an API strategy
 */
public class ApiStrategy
{
	private String name = "";
	private boolean entropy = false;
	private boolean vectropy = false;
	private int portNumber = -1;
	private String messageType = "";
	private String error = "";
	
	public Object[] getTableModelRow()
	{
		boolean enabled = error.isEmpty();
		Object[] row = {name, Integer.valueOf(portNumber), getGameModeDesc(), messageType, Boolean.valueOf(enabled)};
		return row;
	}
	
	public boolean isEnabled()
	{
		return error.isEmpty();
	}
	
	private String getGameModeDesc()
	{
		if (entropy
		  && vectropy)
		{
			return "Both";
		}
		else if (entropy)
		{
			return "Entropy";
		}
		
		return "Vectropy";
	}
	
	/**
	 * Gets / sets
	 */
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public boolean getEntropy()
	{
		return entropy;
	}
	public void setEntropy(boolean entropy)
	{
		this.entropy = entropy;
	}
	public boolean getVectropy()
	{
		return vectropy;
	}
	public void setVectropy(boolean vectropy)
	{
		this.vectropy = vectropy;
	}
	public int getPortNumber()
	{
		return portNumber;
	}
	public void setPortNumber(int portNumber)
	{
		this.portNumber = portNumber;
	}
	public String getMessageType()
	{
		return messageType;
	}
	public void setMessageType(String messageType)
	{
		this.messageType = messageType;
	}
	public String getError()
	{
		return error;
	}
	public void setError(String error)
	{
		this.error = error;
	}
}
