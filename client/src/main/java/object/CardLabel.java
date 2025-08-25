package object;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import preference.PreferenceSetting;
import screen.EntropyScreen;
import util.GameUtil;
import util.Registry;

import static preference.PreferenceSettingKt.getPreference;

public class CardLabel extends JLabel
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

		String deckDesign = getPreference(PreferenceSetting.DeckDesign);
		String jokerDesign = getPreference(PreferenceSetting.JokerDesign);
		String numberOfColours = getPreference(PreferenceSetting.NumberOfColours);
		String back = getPreference(PreferenceSetting.CardBacks);
		if (faded)
		{
			back = "backFaded";
		}
		
		if (!faceUp)
		{
			setIcon(new ImageIcon(getClass().getResource("/backs/" + back + ".png")));
		}
		else if (faded)
		{
			setIcon(GameUtil.getFadedImageForCard(card, deckDesign, jokerDesign));
		}
		else
		{
			setIcon(GameUtil.getImageForCard(card, deckDesign, jokerDesign, numberOfColours));
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
