package util

import utils.CoreGlobals.logger

/** N.B. will likely only work on linux */
fun launchUrl(url: String, runtime: Runtime = Runtime.getRuntime()) {
    try {
        runtime.exec("xdg-open $url")
    } catch (e: Exception) {
        logger.error("urlError", "Failed to launch $url", e)
    }
}
