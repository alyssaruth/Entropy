package object;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import screen.EntropyScreen;
import util.GameUtil;
import util.Registry;

public class CardLabel extends JLabel
					   implements Registry
{
	private String card = "";
	private boolean faceUp = false;
	private boolean faded = false;
	
	public void refreshIcon()
	{
		if ((card == null || card.isEmpty())
		  && faceUp)
		{
			return;
		}
		
		String deckDirectory = prefs.get(PREFERENCES_STRING_DECK_DIRECTORY, Registry.DECK_DIRECTORY_CLASSIC);
		String jokerDirectory = prefs.get(PREFERENCES_STRING_JOKER_DIRECTORY, Registry.JOKER_DIRECTORY_CLASSIC);
		String numberOfColours = prefs.get(PREFERENCES_STRING_NUMBER_OF_COLOURS, Registry.TWO_COLOURS);
		String back = prefs.get(PREFERENCES_STRING_CARD_BACKS, Registry.BACK_CODE_CLASSIC_BLUE);
		if (faded)
		{
			back = "backFaded";
		}
		
		if (!faceUp)
		{
			setIcon(new ImageIcon(EntropyScreen.class.getResource("/backs/" + back + ".png")));
		}
		else if (faded)
		{
			setIcon(GameUtil.getFadedImageForCard(card, deckDirectory, jokerDirectory));
		}
		else
		{
			setIcon(GameUtil.getImageForCard(card, deckDirectory, jokerDirectory, numberOfColours));
		}
	}
	
	/**
	 * Gets / sets
	 */
	public String getCard()
	{
		return card;
	}
	public void setCard(String card)
	{
		this.card = card;
	}
	public boolean isFaceUp()
	{
		return faceUp;
	}
	public void setFaceUp(boolean faceUp)
	{
		this.faceUp = faceUp;
	}
	public boolean isFaded()
	{
		return faded;
	}
	public void setFaded(boolean faded)
	{
		this.faded = faded;
	}
}
