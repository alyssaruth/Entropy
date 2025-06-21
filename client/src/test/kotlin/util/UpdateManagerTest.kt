package util

import bean.LinkLabel
import ch.qos.logback.classic.Level
import com.github.alyssaburlton.swingtest.clickNo
import com.github.alyssaburlton.swingtest.clickOk
import com.github.alyssaburlton.swingtest.clickYes
import com.github.alyssaburlton.swingtest.findWindow
import com.github.alyssaburlton.swingtest.flushEdt
import com.github.alyssaburlton.swingtest.getChild
import com.github.alyssaburlton.swingtest.shouldNotBeVisible
import getDialogMessage
import getErrorDialog
import getInfoDialog
import getQuestionDialog
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldStartWith
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.every
import io.mockk.mockk
import java.io.File
import java.io.IOException
import java.util.concurrent.atomic.AtomicBoolean
import javax.swing.SwingUtilities
import kong.unirest.Unirest
import kong.unirest.UnirestException
import logging.errorObject
import logging.findLogField
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import runAsync
import screen.LoadingDialog
import testCore.assertDoesNotExit
import testCore.assertExits

class UpdateManagerTest : AbstractClientTest() {
    @BeforeEach
    fun resetUnirest() {
        Unirest.config().reset()
        Unirest.config().socketTimeout(2000)
    }

    /** Communication */
    @Test
    fun `Should log out an unexpected HTTP response, along with the full JSON payload`() {
        val server = MockWebServer()
        server.start()
        server.enqueue(MockResponse().setResponseCode(404).setBody("Not Found"))

        val errorMessage = queryLatestReleastJsonExpectingError(server.url("root").toString())
        errorMessage shouldBe "Failed to check for updates (unexpected error)."

        val log = verifyLog("updateError", Level.ERROR)
        log.message shouldContain "Received non-success HTTP status: 404"
        log.findLogField("responseBody").toString() shouldContain "Not Found"

        findWindow<LoadingDialog>()!!.shouldNotBeVisible()
    }

    @Test
    fun `Should catch and log any exceptions communicating over HTTPS`() {
        Unirest.config().socketTimeout(100)

        val server = MockWebServer()
        server.start()

        val errorMessage = queryLatestReleastJsonExpectingError(server.url("root").toString())
        errorMessage shouldBe "Failed to check for updates (unable to connect)."

        val errorLog = verifyLog("updateError", Level.ERROR)
        errorLog.errorObject().shouldBeInstanceOf<UnirestException>()

        findWindow<LoadingDialog>()!!.shouldNotBeVisible()
    }

    private fun queryLatestReleastJsonExpectingError(repositoryUrl: String): String {
        val result = runAsync { UpdateManager().queryLatestRelease(repositoryUrl) }

        val error = getErrorDialog()
        val errorText = error.getDialogMessage()

        error.clickOk()
        flushEdt()

        result shouldBe null

        return errorText
    }

    @Test
    @Tag("integration")
    fun `Should retrieve a valid latest asset from the remote repo`() {
        val responseJson =
            UpdateManager().queryLatestRelease(OnlineConstants.ENTROPY_REPOSITORY_URL)!!

        responseJson.tag_name.shouldStartWith("v")
        responseJson.assets.size shouldBeGreaterThan 0
    }

    /** Parsing */
    @Test
    fun `Should parse correctly formed JSON`() {
        val json =
            """{
                    "tag_name": "foo",
                    "assets": [
                    {
                        "id": 123456,
                        "name": "Dartzee_v_foo.jar",
                        "size": 1
                    }
                    ]
                }"""

        val server = MockWebServer()
        server.start()
        server.enqueue(MockResponse().setBody(json))

        val expectedMetadata =
            UpdateMetadata("foo", listOf(ReleaseAsset(123456L, "Dartzee_v_foo.jar", 1)))

        val metadata = UpdateManager().queryLatestRelease(server.url("root").toString())
        metadata shouldBe expectedMetadata
    }

    @Test
    fun `Should log an error if no tag_name is present`() {
        val json = "{\"other_tag\":\"foo\"}"
        val server = MockWebServer()
        server.start()
        server.enqueue(MockResponse().setBody(json))

        val errorMessage = queryLatestReleastJsonExpectingError(server.url("root").toString())
        errorMessage shouldBe "Failed to check for updates (unexpected error)."

        verifyLog("responseParseError", Level.ERROR)
    }

    /** Should update? */
    @Test
    fun `Should not proceed with the update if the versions match`() {
        val asset = ReleaseAsset(123456, "EntropyLive_x_y.jar", 100)
        val metadata = UpdateMetadata(OnlineConstants.ENTROPY_VERSION_NUMBER, listOf(asset))

        UpdateManager().shouldUpdate(OnlineConstants.ENTROPY_VERSION_NUMBER, metadata) shouldBe
            false
        val log = verifyLog("updateResult")
        log.message shouldBe "Up to date"
    }

    @Test
    fun `Should show an info and not proceed to auto update if OS is not windows`() {
        ClientUtil.operatingSystem = "foo"

        val asset = ReleaseAsset(123456, "EntropyLive_x_y.jar", 100)
        val metadata = UpdateMetadata("v100", listOf(asset))
        shouldUpdateAsync(OnlineConstants.ENTROPY_VERSION_NUMBER, metadata).get() shouldBe false

        val log = verifyLog("updateAvailable")
        log.message shouldBe "Newer release available - v100"

        val info = getInfoDialog()
        val linkLabel = info.getChild<LinkLabel>()
        linkLabel.text shouldBe
            "<html><u>${OnlineConstants.ENTROPY_MANUAL_DOWNLOAD_URL}/tag/v100</u></html>"
    }

    @Test
    fun `Should not proceed with the update if user selects 'No'`() {
        ClientUtil.operatingSystem = "windows"

        val asset = ReleaseAsset(123456, "EntropyLive_x_y.jar", 100)
        val metadata = UpdateMetadata("foo", listOf(asset))
        val result = shouldUpdateAsync("bar", metadata)

        val question = getQuestionDialog()
        question.getDialogMessage() shouldBe
            "An update is available (foo). Would you like to download it now?"
        question.clickNo()
        flushEdt()

        result.get() shouldBe false
    }

    @Test
    fun `Should proceed with the update if user selects 'Yes'`() {
        ClientUtil.operatingSystem = "windows"

        val asset = ReleaseAsset(123456, "EntropyLive_x_y.jar", 100)
        val metadata = UpdateMetadata("foo", listOf(asset))
        val result = shouldUpdateAsync("bar", metadata)

        val question = getQuestionDialog()
        question.getDialogMessage() shouldBe
            "An update is available (foo). Would you like to download it now?"
        question.clickYes()
        flushEdt()

        result.get() shouldBe true
    }

    private fun shouldUpdateAsync(currentVersion: String, metadata: UpdateMetadata): AtomicBoolean {
        val result = AtomicBoolean(false)
        SwingUtilities.invokeLater {
            result.set(UpdateManager().shouldUpdate(currentVersion, metadata))
        }

        flushEdt()
        return result
    }

    /** Prepare batch file */
    @Test
    fun `Should overwrite existing batch file with the correct contents`() {
        val updateFile = File("update.bat")
        updateFile.writeText("blah")

        UpdateManager().prepareBatchFile()

        updateFile.readText() shouldBe javaClass.getResource("/update/update.bat")?.readText()
        updateFile.delete()
    }

    /** Run update */
    @Test
    fun `Should log an error and not exit if batch file goes wrong`() {
        val runtime = mockk<Runtime>()
        val error = IOException("Argh")
        every { runtime.exec(any<String>()) } throws error

        runAsync { assertDoesNotExit { UpdateManager().startUpdate("foo", runtime) } }

        val errorDialog = getErrorDialog()
        errorDialog.getDialogMessage() shouldBe
            "Failed to launch update.bat - call the following manually to perform the update: \n\nupdate.bat foo"

        val log = verifyLog("batchError", Level.ERROR)
        log.errorObject() shouldBe error
    }

    @Test
    fun `Should exit normally if batch file succeeds`() {
        val runtime = mockk<Runtime>(relaxed = true)

        assertExits(0) { UpdateManager().startUpdate("foo", runtime) }
    }
}
