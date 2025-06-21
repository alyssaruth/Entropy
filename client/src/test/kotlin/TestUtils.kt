import com.github.alyssaburlton.swingtest.findAll
import com.github.alyssaburlton.swingtest.findWindow
import com.github.alyssaburlton.swingtest.flushEdt
import com.github.alyssaburlton.swingtest.getChild
import game.GameSettings
import http.ApiResponse
import http.ClientErrorCode
import http.FailureResponse
import http.HttpClient
import http.dto.OnlineMessage
import io.mockk.every
import io.mockk.mockk
import javax.swing.JDialog
import javax.swing.JLabel
import javax.swing.JList
import javax.swing.SwingUtilities
import kong.unirest.HttpMethod
import kong.unirest.HttpStatus
import online.screen.OnlineChatPanel
import testCore.makeGameSettings
import util.CpuStrategies
import util.SimulationParams

fun getInfoDialog() = getOptionPaneDialog("Information")

fun getQuestionDialog() = getOptionPaneDialog("Question")

fun getErrorDialog() = getOptionPaneDialog("Error")

private fun getOptionPaneDialog(title: String) = findWindow<JDialog> { it.title == title }!!

fun JDialog.getDialogMessage(): String {
    val messageLabels = findAll<JLabel>().filter { it.name == "OptionPane.label" }
    return messageLabels.joinToString("\n\n") { it.text }
}

fun <T> runAsync(block: () -> T?): T? {
    var result: T? = null
    SwingUtilities.invokeLater { result = block() }

    flushEdt()
    return result
}

fun OnlineChatPanel.getMessages(): List<OnlineMessage> {
    val listModel = getChild<JList<OnlineMessage>>().model
    return (0..<listModel.size).map(listModel::getElementAt)
}

inline fun <reified T : Any> mockHttpClient(
    response: ApiResponse<T>,
    method: HttpMethod,
    route: String,
): HttpClient {
    val httpClient = mockk<HttpClient>(relaxed = true)
    every { httpClient.doCall<T>(method, route, any()) } returns response

    return httpClient
}

inline fun <reified T : Any> makeFailureResponse(
    status: Int = HttpStatus.CONFLICT,
    body: String = "Conflict",
    clientErrorCode: ClientErrorCode = ClientErrorCode("nope"),
    errorMessage: String = "I'm afraid I can't let you do that",
) = FailureResponse<T>(status, body, clientErrorCode, errorMessage)

fun makeSimulationParams(
    settings: GameSettings = makeGameSettings(),
    opponentTwoEnabled: Boolean = true,
    opponentThreeEnabled: Boolean = true,
    opponentZeroStrategy: String = CpuStrategies.STRATEGY_BASIC,
    opponentOneStrategy: String = CpuStrategies.STRATEGY_BASIC,
    opponentTwoStrategy: String = CpuStrategies.STRATEGY_BASIC,
    opponentThreeStrategy: String = CpuStrategies.STRATEGY_BASIC,
    enableLogging: Boolean = false,
    randomiseOrder: Boolean = false,
    forceStart: Boolean = false,
) =
    SimulationParams(
        settings,
        opponentTwoEnabled,
        opponentThreeEnabled,
        opponentZeroStrategy,
        opponentOneStrategy,
        opponentTwoStrategy,
        opponentThreeStrategy,
        enableLogging,
        randomiseOrder,
        forceStart,
    )
