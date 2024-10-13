import java.util.*
import logging.KEY_APP_VERSION
import logging.KEY_DEVICE_ID
import logging.KEY_DEV_MODE
import logging.KEY_OPERATING_SYSTEM
import logging.KEY_USERNAME
import util.ClientGlobals
import util.ClientUtil
import util.CoreRegistry.INSTANCE_STRING_DEVICE_ID
import util.CoreRegistry.instance
import util.OnlineConstants
import utils.CoreGlobals
import utils.CoreGlobals.logger
import utils.getUsername

fun configureLogging() {
    CoreGlobals.slf4jLogger.addAppender(ClientGlobals.consoleAppender)
    ClientGlobals.consoleAppender.start()
    logger.addContextListener(ClientGlobals.loggingConsole)

    logger.addToContext(KEY_USERNAME, getUsername())
    logger.addToContext(KEY_APP_VERSION, OnlineConstants.ENTROPY_VERSION_NUMBER)
    logger.addToContext(KEY_OPERATING_SYSTEM, ClientUtil.operatingSystem)
    logger.addToContext(KEY_DEVICE_ID, getDeviceId())
    logger.addToContext(KEY_DEV_MODE, ClientUtil.devMode.toString())
}

fun getDeviceId() = instance.get(INSTANCE_STRING_DEVICE_ID, null) ?: setDeviceId()

private fun setDeviceId(): String {
    val deviceId = UUID.randomUUID().toString()
    instance.put(INSTANCE_STRING_DEVICE_ID, deviceId)
    return deviceId
}
