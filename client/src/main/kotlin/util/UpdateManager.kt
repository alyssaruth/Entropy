package util

import bean.LinkLabel
import http.CommunicationError
import http.FailureResponse
import http.HttpClient
import http.SuccessResponse
import java.awt.BorderLayout
import java.io.File
import javax.swing.JLabel
import javax.swing.JOptionPane
import javax.swing.JPanel
import kong.unirest.HttpMethod
import kotlin.system.exitProcess
import utils.CoreGlobals.logger

/**
 * Automatically check for and download updates using the Github API
 *
 * https://developer.github.com/v3/repos/releases/#get-the-latest-release
 */
class UpdateManager {
    fun checkForUpdates(currentVersion: String) {
        // Show this here, checking the CRC can take time
        logger.info("updateCheck", "Checking for updates - my version is $currentVersion")

        val metadata = queryLatestRelease(OnlineConstants.ENTROPY_REPOSITORY_URL)
        metadata ?: return

        if (!shouldUpdate(currentVersion, metadata)) {
            return
        }

        startUpdate(metadata.toScriptArgs(), Runtime.getRuntime())
    }

    fun queryLatestRelease(repositoryUrl: String): UpdateMetadata? {
        try {
            DialogUtilNew.showLoadingDialog("Checking for updates...")

            val client = HttpClient(repositoryUrl)
            val result = client.doCall<UpdateMetadata>(HttpMethod.GET, "/releases/latest")
            return when (result) {
                is CommunicationError -> {
                    logger.error(
                        "updateError",
                        "Caught ${result.unirestException} checking for updates",
                        result.unirestException,
                    )
                    DialogUtilNew.showError("Failed to check for updates (unable to connect).")
                    null
                }
                is FailureResponse -> {
                    logger.error(
                        "updateError",
                        "Received non-success HTTP status: ${result.statusCode} - ${result.body}",
                        "responseBody" to result.body,
                    )
                    DialogUtilNew.showError("Failed to check for updates (unexpected error).")
                    null
                }
                is SuccessResponse -> result.body
            }
        } finally {
            DialogUtilNew.dismissLoadingDialog()
        }
    }

    fun shouldUpdate(currentVersion: String, metadata: UpdateMetadata): Boolean {
        val newVersion = metadata.tag_name
        if (newVersion == currentVersion) {
            logger.info("updateResult", "Up to date")
            return false
        }

        // An update is available
        logger.info("updateAvailable", "Newer release available - $newVersion")

        if (!ClientUtil.isWindowsOs()) {
            showManualDownloadMessage(newVersion)
            return false
        }

        val answer =
            DialogUtilNew.showQuestion(
                "An update is available (${metadata.tag_name}). Would you like to download it now?",
                false,
            )
        return answer == JOptionPane.YES_OPTION
    }

    private fun showManualDownloadMessage(newVersion: String) {
        val fullUrl = "${OnlineConstants.ENTROPY_MANUAL_DOWNLOAD_URL}/tag/$newVersion"
        val panel = JPanel()
        panel.layout = BorderLayout(0, 0)
        val lblOne =
            JLabel("An update is available ($newVersion). You can download it manually from:")
        val linkLabel = LinkLabel(fullUrl) { launchUrl(fullUrl) }

        panel.add(lblOne, BorderLayout.NORTH)
        panel.add(linkLabel, BorderLayout.SOUTH)

        DialogUtilNew.showCustomMessage(panel)
    }

    fun startUpdate(args: String, runtime: Runtime) {
        prepareBatchFile()

        try {
            runtime.exec("cmd /c start update.bat $args")
        } catch (t: Throwable) {
            logger.error("batchError", "Error running update.bat", t)
            val manualCommand = "update.bat $args"

            val msg =
                "Failed to launch update.bat - call the following manually to perform the update: \n\n$manualCommand"
            DialogUtilNew.showError(msg)
            return
        }

        exitProcess(0)
    }

    fun prepareBatchFile() {
        val updateFile = File("update.bat")

        updateFile.delete()
        val updateScript = javaClass.getResource("/update/update.bat").readText()
        updateFile.writeText(updateScript)
    }
}
