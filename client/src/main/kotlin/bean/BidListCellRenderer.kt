package bean

import game.BidAction
import game.MOONS_SYMBOL
import game.PlayerAction
import game.getCardHtml
import game.htmlString
import java.awt.Component
import javax.swing.DefaultListCellRenderer
import javax.swing.JList
import `object`.Player
import util.StringUtil

class BidListCellRenderer : DefaultListCellRenderer() {
    private var hmNameToColour: Map<String, String> = mapOf()

    override fun getListCellRendererComponent(
        list: JList<*>?,
        value: Any?,
        index: Int,
        isSelected: Boolean,
        cellHasFocus: Boolean,
    ): Component? {
        val bid = value as PlayerAction
        val text = toHtmlString(bid)

        return super.getListCellRendererComponent(list, text, index, isSelected, cellHasFocus)
    }

    fun updateColours(players: MutableCollection<Player>) {
        this.hmNameToColour = players.associate { it.name to it.colour }
    }

    fun updateColours(map: Map<String, String>) {
        this.hmNameToColour = map
    }

    fun toHtmlString(action: PlayerAction): String {
        var playerName = action.playerName
        playerName = StringUtil.escapeHtml(playerName)

        val colour = hmNameToColour.get(playerName)
        var playerNamePrefix = "$playerName:&nbsp"

        if (action.blind) {
            playerNamePrefix = "[$playerName]:&nbsp"
        }

        var text = "<html><b><font color=\"$colour\">$playerNamePrefix</b></font>"
        text += action.htmlString()

        if (action is BidAction<*> && action.cardToReveal != null) {
            text += "<i><font color=\"#5C5C3D\">&emsp(Shows:&nbsp</i></font>"
            text += getCardHtml(action.cardToReveal!!)
            text += "<i><font color=\"#5C5C3D\">)</i></font>"
        }

        // The unicode for a moon doesn't work in HTML. Also shrink to match the size of the other
        // suits.
        text = text.replace(MOONS_SYMBOL.toRegex(), "<font size=\"2\">&#127769</font>")
        text += "</html>"
        return text
    }
}
