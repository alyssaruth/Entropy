package object;

import util.Registry;

public class SuitWrapper
{
	private String twoColour = "";
	private String fourColour = "";
	private String unicodeSymbol = "";
	private String name = "";
	private String suitShort = "";
	
	public SuitWrapper(String colour, String unicodeSymbol, String name, String suitShort)
	{
		this(colour, colour, unicodeSymbol, name, suitShort);
	}
	public SuitWrapper(String twoColour, String fourColour, String unicodeSymbol, String name, String suitShort)
	{
		this.twoColour = twoColour;
		this.fourColour = fourColour;
		this.unicodeSymbol = unicodeSymbol;
		this.name = name;
		this.suitShort = suitShort;
	}
	
	public String getColour()
	{
		String numberOfColoursStr = Registry.prefs.get(Registry.PREFERENCES_STRING_NUMBER_OF_COLOURS, Registry.TWO_COLOURS);
		boolean fourColours = (numberOfColoursStr.equals(Registry.FOUR_COLOURS));
		if (fourColours)
		{
			return fourColour;
		}
		
		return twoColour;
	}
	public String getUnicodeSymbol()
	{
		return unicodeSymbol;
	}
	public String getName()
	{
		return name;
	}
	public String getSuitShort()
	{
		return suitShort;
	}
}
