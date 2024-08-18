package help;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.util.List;

import javax.swing.*;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;

import screen.HelpDialog;
import screen.ScreenCache;
import utils.SwingUtilsKt;

import static utils.InjectedThings.logger;

/**
 * Object representing a 'page' of the help dialog.
 */
public abstract class HelpPanel extends JPanel
{
	private String panelName = null;
	private String nodeName = null;

	public HelpPanel()
	{

	}

	private List<JTextPane> getTextFields() {
		return SwingUtilsKt.getAllChildComponentsForType(this, JTextPane.class);
	}

	public boolean contains(String searchStr)
	{
		String searchStrLowerCase = searchStr.toLowerCase();

		for (var fieldToCheck : getTextFields()) {
			try
			{
				int fieldLength = fieldToCheck.getDocument().getLength();
				String fieldText = fieldToCheck.getDocument().getText(0, fieldLength);
				String fieldTextLowerCase = fieldText.toLowerCase();

				if (fieldTextLowerCase.contains(searchStrLowerCase))
				{
					return true;
				}
			}
			catch (Throwable t)
			{
				logger.error("searchError", "Encountered error searching for " + searchStr, t);
			}
		}

		return false;
	}

	public void highlight(String searchStr)
	{
		int termLength = searchStr.length();

		HighlightPainter hlp = new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);

		for (var pane : getTextFields())
		{
			Highlighter highlighter = pane.getHighlighter();
			int paneLength = pane.getDocument().getLength();

			try
			{
				highlighter.removeAllHighlights();
				String paneText = getDocumentText(pane);

				for (int i=0; i<paneLength - termLength + 1; i++)
				{
					String potentialSearchStr = paneText.substring(i, i+termLength);

					if (potentialSearchStr.equalsIgnoreCase(searchStr) && !searchStr.isEmpty())
					{
						highlighter.addHighlight(i, i+termLength, hlp);
					}
				}
			}
			catch (Throwable e)
			{
				logger.error("highlightError", "Error highlighting pane", e);
			}
		}
	}

	public void setTextFieldsEditable(boolean editable)
	{
		for (var pane: getTextFields()) {
			pane.setEditable(editable);
		}
	}

	public void addMouseListeners(final String... wordsToExclude)
	{
		for (var pane: getTextFields()) {
			pane.addMouseListener(new MouseListener()
			{
				@Override
				public void mouseClicked(MouseEvent arg0) 
				{
					String text = getDocumentText(pane);
					Point pt = arg0.getPoint();
					int pos = pane.viewToModel(pt);

					String word = getWordFromPosition(text, pos);

					if (isKeyWord(word, wordsToExclude))
					{
						navigateToPageBasedOnKeyWord(word);
					}
				}

				@Override
				public void mouseEntered(MouseEvent arg0) 
				{
					mouseHovered(pane, arg0, wordsToExclude);
				}

				@Override
				public void mouseExited(MouseEvent arg0) 
				{

				}

				@Override
				public void mousePressed(MouseEvent arg0) 
				{

				}

				@Override
				public void mouseReleased(MouseEvent arg0) 
				{

				}

			});

			pane.addMouseMotionListener(new MouseMotionAdapter() {
				@Override
				public void mouseMoved(MouseEvent arg0) 
				{
					mouseHovered(pane, arg0, wordsToExclude);
				}

			});
		}
	}

	private void mouseHovered(JTextPane pane, MouseEvent arg0, String[] wordsToExclude)
	{
		String text = getDocumentText(pane);
		Point pt = arg0.getPoint();
		int pos = pane.viewToModel(pt);

		String word = getWordFromPosition(text, pos);

		if (isKeyWord(word, wordsToExclude))
		{
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}
		else
		{
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}

	private String getWordFromPosition(String text, int position)
	{
		if (position < 1)
		{
			return "";
		}

		int length = text.length();

		String character = text.substring(position-1, position);

		if (!isLetter(character))
		{
			return "";
		}

		String word = character;

		if (position > 1)
		{
			int i = 1;
			String characterToTheLeft = text.substring(position-i-1, position-i);

			while (isLetter(characterToTheLeft))
			{
				try
				{
					word = characterToTheLeft + word;
					i++;
					characterToTheLeft = text.substring(position-i-1, position-i);
				}
				catch (IndexOutOfBoundsException e)
				{
					characterToTheLeft = "";
				}
			}
		}

		if (position < length)
		{
			int i = 1;
			String characterToTheRight = text.substring(position+i-1, position+i);

			while (isLetter(characterToTheRight))
			{
				try
				{
					word += characterToTheRight;
					i++;
					characterToTheRight = text.substring(position+i-1, position+i);
				}
				catch (IndexOutOfBoundsException e)
				{
					characterToTheRight = "";
				}
			}
		}

		return word.toLowerCase();
	}

	private boolean isLetter(String letter)
	{
		return letter.matches("^[a-zA-Z]+$");
	}

	private void navigateToPageBasedOnKeyWord(String keyWord)
	{
		logger.info("navigatedForWord", "Navigated for word " + keyWord);
		HelpDialog helpDialog = ScreenCache.getHelpDialog();

		if (keyWord.startsWith("bidd"))
		{
			if (panelName.contains("Entropy"))
			{
				helpDialog.setSelectionForWord("RulesEntropyBidding");
			}
			else if (panelName.contains("Vectropy"))
			{
				helpDialog.setSelectionForWord("RulesVectropyBidding");
			}
		}
		else if (keyWord.startsWith("chall"))
		{
			if (panelName.contains("Entropy"))
			{
				helpDialog.setSelectionForWord("RulesEntropyChallenging");
			}
			else if (panelName.contains("Vectropy"))
			{
				helpDialog.setSelectionForWord("RulesVectropyChallenging");
			}
		}
		else if (keyWord.equals("order"))
		{
			helpDialog.setSelectionForWord("FundamentalsTheDeck");
		}
		else if (keyWord.equals("perfect"))
		{
			helpDialog.setSelectionForWord("FundamentalsGlossary");
		}
	}

	private String getDocumentText(JTextPane pane)
	{
		try
		{
			int length = pane.getDocument().getLength();
			return pane.getDocument().getText(0, length);
		}
		catch (Throwable e)
		{
			logger.error("textPaneError", "Error getting text from pane", e);
			return null;
		}
	}

	private boolean isKeyWord(String word, String[] wordsToExclude)
	{
		for (String wordToExclude : wordsToExclude) {
            if (word.startsWith(wordToExclude) && !wordToExclude.isEmpty()) {
                return false;
            }
        }

		return word.equals("bidding") 
		  || word.startsWith("challeng") 
		  || word.equals("order")
		  || word.equals("perfect");
	}

	@Override
	public String toString()
	{
		return nodeName;
	}

	public String getPanelName()
	{
		return panelName;
	}
	public void setPanelName(String panelName)
	{
		this.panelName = panelName;
	}
	public void setNodeName(String nodeName)
	{
		this.nodeName = nodeName;
	}

	public void fireAppearancePreferencesChange() {

	}
	
	public void refresh()
	{
		//to be overridden by any pages that have dynamic content
	}
}