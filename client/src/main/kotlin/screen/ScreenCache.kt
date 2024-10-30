package screen

import java.awt.Container
import online.screen.ConnectingDialog

const val IN_GAME_REPLAY = "inGame"
const val FILE_REPLAY = "file"

object ScreenCache {
    private val connectingDialog: ConnectingDialog = ConnectingDialog()
    private val replayDialogs = mutableMapOf<String, ReplayDialog>()
    val hmClassToScreen = mutableMapOf<Class<out Container>, Container>()

    inline fun <reified K : Container> get() = get(K::class.java)

    @JvmStatic
    fun <K : Container> get(clazz: Class<K>) =
        hmClassToScreen.getOrPut(clazz) { clazz.getConstructor().newInstance() } as K

    @JvmStatic fun getReplayDialog(alias: String) = replayDialogs.getOrPut(alias) { ReplayDialog() }

    @JvmStatic fun getReplayDialogs() = replayDialogs.values

    fun emptyCache() {
        hmClassToScreen.clear()
        replayDialogs.clear()
    }

    @JvmStatic
    fun getConnectingDialog(): ConnectingDialog {
        return connectingDialog
    }

    fun showConnectingDialog() {
        connectingDialog.showDialog()
    }

    @JvmStatic
    fun dismissConnectingDialog() {
        connectingDialog.dismissDialog()
    }
}
