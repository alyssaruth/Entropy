package game

import com.fasterxml.jackson.module.kotlin.readValue
import java.util.prefs.Preferences
import javax.swing.DefaultListModel
import util.Registry
import utils.CoreGlobals

fun writeActions(node: Preferences, model: DefaultListModel<PlayerAction>, round: Int?) {
    val prefix = round?.let { "$round" } ?: ""
    val historySize: Int = model.size()
    node.putInt(prefix + Registry.SHARED_INT_HISTORY_SIZE, historySize)
    for (i in 0 until historySize) {
        val bid: PlayerAction = model.get(i)
        node.put(
            prefix + Registry.SHARED_STRING_LISTMODEL + i,
            CoreGlobals.jsonMapper.writeValueAsString(bid),
        )
    }
}

fun populateActions(node: Preferences, model: DefaultListModel<PlayerAction>, round: Int?) {
    model.clear()

    val prefix = round?.let { "$round" } ?: ""
    val historySize: Int = node.getInt(prefix + Registry.SHARED_INT_HISTORY_SIZE, 0)
    for (i in 0 until historySize) {
        val modelItem: String = node.get(prefix + Registry.SHARED_STRING_LISTMODEL + i, "")
        val action = CoreGlobals.jsonMapper.readValue<PlayerAction>(modelItem)
        model.addElement(action)
    }
}
