package auth

import java.util.concurrent.atomic.AtomicInteger
import javax.crypto.SecretKey
import `object`.NotificationSocket
import org.w3c.dom.Document
import server.EntropyServer
import server.NotificationRunnable
import util.ColourGenerator
import util.Debug
import util.EncryptionUtil
import util.XmlConstants

data class UserConnection(val ipAddress: String, val symmetricKey: SecretKey?) {
    var username: String? = null
    var colour: String? = null
    var lastActive: Long = -1
        private set

    var mobile: Boolean = false
        private set

    private val hmNotificationQueueBySocketName = HashMap<String, ArrayList<Document>>()
    private val hmSocketBySocketName = HashMap<String, NotificationSocket?>()
    private val hmWaitObjBySocketName = HashMap<String, Object>()

    /** When a connection is initiated */
    init {
        initialiseSocketHashMaps()
    }

    /** When the logon process is completed */
    fun update(username: String, mobile: Boolean) {
        this.username = username
        this.mobile = mobile

        colour = ColourGenerator.generateNextColour()
        setLastActiveNow()

        Debug.append("New user connected: $username")
    }

    fun setLastActiveNow() {
        this.lastActive = System.currentTimeMillis()
    }

    fun destroyNotificationSockets() {
        val it: Iterator<String> = hmSocketBySocketName.keys.iterator()
        while (it.hasNext()) {
            val socketType = it.next()
            replaceNotificationSocket(socketType, null)
        }
    }

    /**
     * Synchronize on the wait object so we wait for any current attempt to send a Notification to
     * go into its wait() block. This way we ensure it gets our notify() call. If we sneak in here
     * before it that's fine too, as when it tries to send there'll be a fresh new socket.
     */
    fun replaceNotificationSocket(socketType: String, socket: NotificationSocket?) {
        val notificationWaitObj = hmWaitObjBySocketName.getValue(socketType)

        synchronized(notificationWaitObj) {
            val existingSocket = hmSocketBySocketName[socketType]
            existingSocket?.closeResources()

            hmSocketBySocketName[socketType] = socket
            notificationWaitObj.notify()
        }
    }

    fun getNotificationSocket(socketType: String): NotificationSocket? {
        return hmSocketBySocketName[socketType]
    }

    override fun toString(): String {
        var desc = "$username @ $ipAddress"

        if (mobile) {
            desc += " (mob)"
        }

        if (symmetricKey != null) {
            desc += ", " + EncryptionUtil.convertSecretKeyToString(symmetricKey)
        }

        return desc
    }

    fun sendNotificationInWorkerPool(
        message: Document?,
        server: EntropyServer,
        socketName: String?,
        counter: AtomicInteger?
    ) {
        val runnable = NotificationRunnable(server, message, this, counter, socketName)
        server.executeInWorkerPool(runnable)
    }

    fun addNotificationToQueue(socketType: String, message: Document) {
        val notificationQueue = hmNotificationQueueBySocketName.getValue(socketType)
        notificationQueue.add(message)
    }

    fun getNotificationQueue(socketName: String): ArrayList<Document> {
        return hmNotificationQueueBySocketName[socketName]!!
    }

    fun getNextNotificationToSend(socketType: String): Document? {
        val notificationQueue = hmNotificationQueueBySocketName.getValue(socketType)
        val size = notificationQueue.size
        return if (size > 0) {
            notificationQueue.removeAt(0)
        } else {
            null
        }
    }

    fun getNotificationQueueSize(socketName: String): Int {
        val notificationQueue = hmNotificationQueueBySocketName.getValue(socketName)
        return notificationQueue.size
    }

    fun waitForNewNotificationSocket(socketName: String) {
        val waitObj = hmWaitObjBySocketName.getValue(socketName)

        try {
            waitObj.wait()
        } catch (t: InterruptedException) {
            // Not expecting interruptions
            Debug.stackTrace(t)
        }
    }

    private fun initialiseSocketHashMaps() {
        initialiaseHashMaps(XmlConstants.SOCKET_NAME_CHAT)
        initialiaseHashMaps(XmlConstants.SOCKET_NAME_LOBBY)
        initialiaseHashMaps(XmlConstants.SOCKET_NAME_GAME)
    }

    private fun initialiaseHashMaps(socketType: String) {
        hmWaitObjBySocketName[socketType] = Object()
        hmNotificationQueueBySocketName[socketType] = ArrayList()
    }

    fun getNotificationWaitObject(socketType: String): Any? {
        return hmWaitObjBySocketName[socketType]
    }
}
