import com.github.alyssaburlton.swingtest.findAll
import com.github.alyssaburlton.swingtest.findWindow
import com.github.alyssaburlton.swingtest.flushEdt
import com.github.alyssaburlton.swingtest.getChild
import game.BidAction
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
import strategy.IRandom
import strategy.IStrategy
import strategy.InBuiltStrategy
import strategy.StrategyParams
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
    opponentZeroStrategy: IStrategy = InBuiltStrategy(CpuStrategies.STRATEGY_BASIC),
    opponentOneStrategy: IStrategy = InBuiltStrategy(CpuStrategies.STRATEGY_BASIC),
    opponentTwoStrategy: IStrategy = InBuiltStrategy(CpuStrategies.STRATEGY_BASIC),
    opponentThreeStrategy: IStrategy = InBuiltStrategy(CpuStrategies.STRATEGY_BASIC),
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

fun makeStrategyParams(
    settings: GameSettings = makeGameSettings(),
    cardsInPlay: Int = 4,
    opponentCardsOnPlay: List<String> = emptyList(),
    lastBid: BidAction<*>? = null,
    logging: Boolean = true,
) = StrategyParams(settings, cardsInPlay, opponentCardsOnPlay, lastBid, logging)

class TestRandom(vararg sequence: Int) : IRandom {
    private val numbers = sequence.toMutableList()

    override fun nextInt(until: Int): Int {
        val result = numbers.removeFirst()
        if (result >= until) {
            throw IllegalStateException(
                "Next choice is greater than passed limit: $result > $until"
            )
        }

        return result
    }
}
