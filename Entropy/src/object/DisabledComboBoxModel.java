package object;

import java.util.Vector;

import javax.swing.DefaultComboBoxModel;

import bean.ComboBoxItem;
import util.Debug;

public class DisabledComboBoxModel<E> extends DefaultComboBoxModel<ComboBoxItem<E>>
{
	public DisabledComboBoxModel(Vector<ComboBoxItem<E>> vector)
	{
		super(vector);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void setSelectedItem(Object arg0)
	{
		if (!(arg0 instanceof ComboBoxItem))
		{
			Debug.stackTrace("Calling setSelectedItem on an object which is not a ComboBoxItem: " + arg0);
			return;
		}
		
		try
		{
			ComboBoxItem<E> item = (ComboBoxItem<E>)arg0;
			setSelectedComboBoxItem(item);
		}
		catch (ClassCastException cce)
		{
			Debug.stackTrace(cce);
		}
	}
	public void setSelectedComboBoxItem(ComboBoxItem<E> comboBoxItem)
	{
		if (comboBoxItem != null)
		{
			if (comboBoxItem.isEnabled())
			{
				super.setSelectedItem(comboBoxItem);
			}
		}
	}
}
