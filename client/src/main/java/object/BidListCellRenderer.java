package object;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

public class BidListCellRenderer extends DefaultListCellRenderer
{
	@Override
	@SuppressWarnings("rawtypes")
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) 
	{
		Bid bid = (Bid)value;
		String text = bid.toHtmlString();

		return super.getListCellRendererComponent(list, text, index, isSelected,
				cellHasFocus);
	}

}
