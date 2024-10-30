package help

import java.awt.Color
import java.awt.Cursor
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionAdapter
import java.util.*
import javax.swing.JPanel
import javax.swing.JTextPane
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter
import javax.swing.text.Highlighter
import screen.HelpDialog
import screen.ScreenCache
import util.Registry
import utils.CoreGlobals.logger
import utils.getAllChildComponentsForType

/** Object representing a 'page' of the help dialog. */
abstract class HelpPanel : JPanel() {
    abstract val nodeName: String

    protected open fun searchTermsToExclude(): List<String> {
        return emptyList()
    }

    protected fun finaliseComponents() {
        addMouseListeners(searchTermsToExclude())
        setTextFieldsReadOnly()
    }

    private fun getTextFields() = getAllChildComponentsForType(JTextPane::class.java)

    fun contains(searchStr: String): Boolean {
        val searchStrLowerCase = searchStr.lowercase(Locale.getDefault())

        for (fieldToCheck in getTextFields()) {
            val fieldLength = fieldToCheck.document.length
            val fieldText = fieldToCheck.document.getText(0, fieldLength)
            val fieldTextLowerCase = fieldText.lowercase(Locale.getDefault())

            if (fieldTextLowerCase.contains(searchStrLowerCase)) {
                return true
            }
        }

        return false
    }

    fun highlight(searchStr: String) {
        val termLength = searchStr.length

        val hlp: Highlighter.HighlightPainter = DefaultHighlightPainter(Color.YELLOW)

        for (pane in getTextFields()) {
            val highlighter = pane.highlighter
            val paneLength = pane.document.length

            try {
                highlighter.removeAllHighlights()
                val paneText = getDocumentText(pane)

                for (i in 0..paneLength - termLength) {
                    val potentialSearchStr = paneText.substring(i, i + termLength)

                    if (
                        potentialSearchStr.equals(searchStr, ignoreCase = true) &&
                            searchStr.isNotEmpty()
                    ) {
                        highlighter.addHighlight(i, i + termLength, hlp)
                    }
                }
            } catch (e: Throwable) {
                logger.error("highlightError", "Error highlighting pane", e)
            }
        }
    }

    private fun setTextFieldsReadOnly() {
        for (pane in getTextFields()) {
            pane.isEditable = false
        }
    }

    private fun addMouseListeners(wordsToExclude: List<String>) {
        for (pane in getTextFields()) {
            pane.addMouseListener(
                object : MouseAdapter() {
                    override fun mouseClicked(arg0: MouseEvent) {
                        val text = getDocumentText(pane)
                        val pt = arg0.point
                        val pos = pane.viewToModel(pt)

                        val word = getWordFromPosition(text, pos)

                        if (isKeyWord(word, wordsToExclude)) {
                            navigateToPageBasedOnKeyWord(word)
                        }
                    }

                    override fun mouseEntered(arg0: MouseEvent) {
                        mouseHovered(pane, arg0, wordsToExclude)
                    }
                }
            )

            pane.addMouseMotionListener(
                object : MouseMotionAdapter() {
                    override fun mouseMoved(arg0: MouseEvent) {
                        mouseHovered(pane, arg0, wordsToExclude)
                    }
                }
            )
        }
    }

    private fun mouseHovered(pane: JTextPane, arg0: MouseEvent, wordsToExclude: List<String>) {
        val text = getDocumentText(pane)
        val pt = arg0.point
        val pos = pane.viewToModel(pt)

        val word = getWordFromPosition(text, pos)

        cursor =
            if (isKeyWord(word, wordsToExclude)) {
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
            } else {
                Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)
            }
    }

    private fun getWordFromPosition(text: String, position: Int): String {
        if (position < 1) {
            return ""
        }

        val character = text.substring(position - 1, position)

        if (!isLetter(character)) {
            return ""
        }

        var word = character

        if (position > 1) {
            var i = 1
            var characterToTheLeft = text.substring(position - i - 1, position - i)

            while (isLetter(characterToTheLeft)) {
                try {
                    word = characterToTheLeft + word
                    i++
                    characterToTheLeft = text.substring(position - i - 1, position - i)
                } catch (e: IndexOutOfBoundsException) {
                    characterToTheLeft = ""
                }
            }
        }

        if (position < text.length) {
            var i = 1
            var characterToTheRight = text.substring(position + i - 1, position + i)

            while (isLetter(characterToTheRight)) {
                try {
                    word += characterToTheRight
                    i++
                    characterToTheRight = text.substring(position + i - 1, position + i)
                } catch (e: IndexOutOfBoundsException) {
                    characterToTheRight = ""
                }
            }
        }

        return word.lowercase(Locale.getDefault())
    }

    private fun isLetter(letter: String): Boolean {
        return letter.matches("^[a-zA-Z]+$".toRegex())
    }

    private fun navigateToPageBasedOnKeyWord(keyWord: String) {
        logger.info("navigatedForWord", "Navigated for word $keyWord")
        val helpDialog = ScreenCache.get<HelpDialog>()

        if (keyWord.startsWith("bidd")) {
            if (javaClass.simpleName.contains("Entropy")) {
                helpDialog.selectPane<RulesEntropyBidding>()
            } else if (javaClass.simpleName.contains("Vectropy")) {
                helpDialog.selectPane<RulesVectropyBidding>()
            }
        } else if (keyWord.startsWith("chall")) {
            if (javaClass.simpleName.contains("Entropy")) {
                helpDialog.selectPane<RulesEntropyChallenging>()
            } else if (javaClass.simpleName.contains("Vectropy")) {
                helpDialog.selectPane<RulesVectropyChallenging>()
            }
        } else if (keyWord == "order") {
            helpDialog.selectPane<FundamentalsTheDeck>()
        } else if (keyWord == "perfect") {
            helpDialog.selectPane<FundamentalsGlossary>()
        }
    }

    private fun getDocumentText(pane: JTextPane): String {
        val length = pane.document.length
        return pane.document.getText(0, length)
    }

    private fun isKeyWord(word: String, wordsToExclude: List<String>): Boolean {
        for (wordToExclude in wordsToExclude) {
            if (word.startsWith(wordToExclude) && wordToExclude.isNotEmpty()) {
                return false
            }
        }

        return word == "bidding" ||
            word.startsWith("challeng") ||
            word == "order" ||
            word == "perfect"
    }

    override fun toString() = nodeName

    protected fun useFourColours() =
        Registry.prefs[Registry.PREFERENCES_STRING_NUMBER_OF_COLOURS, Registry.TWO_COLOURS] ==
            Registry.FOUR_COLOURS

    open fun refresh() {
        // to be overridden by any pages that have dynamic content
    }
}
