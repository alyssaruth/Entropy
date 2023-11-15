package util;

import java.util.ArrayList;

public class ColourGenerator 
{
	private static Object colourSynchObject = new Object();
	private static ArrayList<String> colours = null;
	
	public static String generateNextColour()
	{
		//Don't hand out the same colour twice
		synchronized (colourSynchObject)
		{
			if (colours == null
			  || colours.isEmpty())
			{
				initialiseColourList();
			}
			
			return colours.remove(0);
		}
	}
	
	private static void initialiseColourList()
	{
		colours = new ArrayList<>();
		
		colours.add("Green");
		colours.add("Red");
		colours.add("Blue");
		colours.add("Purple");
		colours.add("Maroon");
		colours.add("Orange");
	}
	
	public static void freeUpColour(String colour)
	{
		synchronized (colourSynchObject)
		{
			colours.add(colour);
		}
	}
}
