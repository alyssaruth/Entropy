package util

import bean.LinkLabel
import java.awt.BorderLayout
import java.io.File
import javax.swing.JLabel
import javax.swing.JOptionPane
import javax.swing.JPanel
import kong.unirest.Unirest
import kong.unirest.json.JSONObject
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

        val jsonResponse = queryLatestReleaseJson(OnlineConstants.ENTROPY_REPOSITORY_URL)
        jsonResponse ?: return

        val metadata = parseUpdateMetadata(jsonResponse)
        if (metadata == null || !shouldUpdate(currentVersion, metadata)) {
            return
        }

        startUpdate(metadata.getArgs(), Runtime.getRuntime())
    }

    fun queryLatestReleaseJson(repositoryUrl: String): JSONObject? {
        try {
            DialogUtilNew.showLoadingDialog("Checking for updates...")

            val response = Unirest.get("$repositoryUrl/releases/latest").asJson()
            if (response.status != 200) {
                logger.error(
                    "updateError",
                    "Received non-success HTTP status: ${response.status} - ${response.statusText}",
                    "responseBody" to response.body,
                )
                DialogUtilNew.showError("Failed to check for updates (unable to connect).")
                return null
            }

            return response.body.`object`
        } catch (t: Throwable) {
            logger.error("updateError", "Caught $t checking for updates", t)
            DialogUtilNew.showError("Failed to check for updates (unable to connect).")
            return null
        } finally {
            DialogUtilNew.dismissLoadingDialog()
        }
    }

    fun shouldUpdate(currentVersion: String, metadata: UpdateMetadata): Boolean {
        val newVersion = metadata.version
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
                "An update is available (${metadata.version}). Would you like to download it now?",
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

    fun parseUpdateMetadata(responseJson: JSONObject): UpdateMetadata? {
        return try {
            val remoteVersion = responseJson.getString("tag_name")
            val assets = responseJson.getJSONArray("assets")
            val asset = assets.getJSONObject(0)

            val assetId = asset.getLong("id")
            val fileName = asset.getString("name")
            val size = asset.getLong("size")
            UpdateMetadata(remoteVersion, assetId, fileName, size)
        } catch (t: Throwable) {
            logger.error(
                "parseError",
                "Error parsing update response",
                t,
                "responseBody" to responseJson,
            )
            null
        }
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

data class UpdateMetadata(
    val version: String,
    val assetId: Long,
    val fileName: String,
    val size: Long,
) {
    fun getArgs() = "$size $version $fileName $assetId"
}
