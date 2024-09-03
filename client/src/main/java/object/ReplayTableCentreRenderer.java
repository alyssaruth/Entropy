package object;

import java.awt.Component;
import java.awt.Font;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

public class ReplayTableCentreRenderer extends DefaultTableCellRenderer
{
	@Override
    public Component getTableCellRendererComponent(JTable table, Object
        value, boolean isSelected, boolean hasFocus, int row, int column) 
    {
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		setHorizontalAlignment(SwingConstants.CENTER);
		setFont(new Font("Arial",Font.BOLD,15));

		if (table instanceof ReplayTable)
		{
			ReplayTable replayTable = (ReplayTable)table;
			int modelRow = replayTable.convertRowIndexToModel(row);
			setForeground(replayTable.getColorForRow(modelRow));
		}

		return this;
    }
}
