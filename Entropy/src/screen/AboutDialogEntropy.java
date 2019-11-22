package screen;

import java.awt.Window;

import util.OnlineConstants;

public class AboutDialogEntropy extends AbstractAboutDialog 
{
	@Override
	public String getProductDesc()
	{
		return "Entropy " + OnlineConstants.ENTROPY_VERSION_NUMBER;
	}

	@Override
	public Window getChangeLog()
	{
		return new ChangeLog();
	}
	
}