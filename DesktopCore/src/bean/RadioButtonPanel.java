package bean;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class RadioButtonPanel extends JPanel
							  implements ChangeListener
{
	private ButtonGroup bg = new ButtonGroup();
	private JRadioButton selection = null;
	
	@Override
	public Component add(Component arg0)
	{
		if (arg0 instanceof JRadioButton)
		{
			JRadioButton rdbtn = (JRadioButton)arg0;
			rdbtn.addChangeListener(this);
			
			bg.add(rdbtn);
			if (bg.getButtonCount() == 1)
			{
				//Ensure this is selected
				rdbtn.setSelected(true);
			}
		}
		
		return super.add(arg0);
	}
	
	public JRadioButton getSelection()
	{
		return selection;
	}
	public String getSelectionStr()
	{
		return selection.getText();
	}
	
	public void addActionListener(ActionListener listener)
	{
		Enumeration<AbstractButton> buttons = bg.getElements();
		while (buttons.hasMoreElements())
		{
			AbstractButton button = buttons.nextElement();
			button.addActionListener(listener);
		}
	}
	
	public boolean isEventSource(ActionEvent evt)
	{
		Object source = evt.getSource();
		
		Enumeration<AbstractButton> buttons = bg.getElements();
		while (buttons.hasMoreElements())
		{
			AbstractButton button = buttons.nextElement();
			if (source == button)
			{
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public void stateChanged(ChangeEvent arg0)
	{
		JRadioButton src = (JRadioButton)arg0.getSource();
		if (src.isSelected())
		{
			selection = src;
		}
	}
}
