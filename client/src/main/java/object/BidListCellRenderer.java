package object;

import util.StringUtil;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import static game.RenderingUtilKt.getCardHtml;
import static game.SuitKt.MOONS_SYMBOL;

public class BidListCellRenderer extends DefaultListCellRenderer
{
	@Override
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) 
	{
		Bid bid = (Bid)value;
		String text = toHtmlString(bid);

		return super.getListCellRendererComponent(list, text, index, isSelected,
				cellHasFocus);
	}

	public String toHtmlString(Bid bid)
	{
		String playerName = bid.getPlayer().getName();
		playerName = StringUtil.escapeHtml(playerName);

		String colour = bid.getPlayer().getColour();
		String playerNamePrefix = playerName + ":&nbsp";

		if (bid.isBlind())
		{
			playerNamePrefix = "[" + playerName + "]:&nbsp";
		}

		String text = "<html><b><font color=\"" + colour + "\">" + playerNamePrefix;
		text += "</b></font>";
		text += bid.toHtmlStringSpecific();

		if (!bid.getCardToReveal().isEmpty()
				&& !bid.isChallenge()
				&& !bid.isIllegal())
		{
			text += "<i><font color=\"#5C5C3D\">&emsp(Shows:&nbsp</i></font>";
			text += getCardHtml(bid.getCardToReveal());
			text += "<i><font color=\"#5C5C3D\">)</i></font>";
		}

		//The unicode for a moon doesn't work in HTML. Also shrink to match the size of the other suits.
		text = text.replaceAll(MOONS_SYMBOL, "<font size=\"2\">&#127769</font>");
		return text;
	}

}
