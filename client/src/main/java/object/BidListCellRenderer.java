package object;

import game.BidAction;
import game.PlayerAction;
import util.StringUtil;

import java.awt.Component;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import static game.RenderingUtilKt.getCardHtml;
import static game.SuitKt.MOONS_SYMBOL;
import static utils.ColourUtilKt.getColourForPlayerNumber;

public class BidListCellRenderer extends DefaultListCellRenderer
{
	private Map<String, String> hmNameToColour = new HashMap<>();

	@Override
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) 
	{
		PlayerAction bid = (PlayerAction)value;
		String text = toHtmlString(bid);

		return super.getListCellRendererComponent(list, text, index, isSelected,
				cellHasFocus);
	}

	public void updateColours(Collection<Player> players) {
		this.hmNameToColour.clear();

		for (Player player : players) {
			hmNameToColour.put(player.getName(), player.getColour());
		}
	}

	public void updateColours(Map<String, String> map) {
		this.hmNameToColour = map;
	}

	public String toHtmlString(PlayerAction action)
	{
		String playerName = action.getPlayerName();
		playerName = StringUtil.escapeHtml(playerName);

		String colour  = hmNameToColour.get(playerName);
		String playerNamePrefix = playerName + ":&nbsp";

		if (action.getBlind())
		{
			playerNamePrefix = "[" + playerName + "]:&nbsp";
		}

		String text = "<html><b><font color=\"" + colour + "\">" + playerNamePrefix;
		text += "</b></font>";
		text += action.htmlString();

		if (action instanceof BidAction<?> bid && bid.getCardToReveal() != null) {
			text += "<i><font color=\"#5C5C3D\">&emsp(Shows:&nbsp</i></font>";
			text += getCardHtml(bid.getCardToReveal());
			text += "<i><font color=\"#5C5C3D\">)</i></font>";
		}

		//The unicode for a moon doesn't work in HTML. Also shrink to match the size of the other suits.
		text = text.replaceAll(MOONS_SYMBOL, "<font size=\"2\">&#127769</font>");
		return text;
	}
}
