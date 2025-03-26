import com.github.alyssaburlton.swingtest.getChild
import http.ApiResponse
import http.ClientErrorCode
import http.FailureResponse
import http.HttpClient
import http.dto.OnlineMessage
import io.mockk.every
import io.mockk.mockk
import javax.swing.JList
import kong.unirest.HttpMethod
import kong.unirest.HttpStatus
import online.screen.OnlineChatPanel

fun OnlineChatPanel.getMessages(): List<OnlineMessage> {
    val listModel = getChild<JList<OnlineMessage>>().model
    return (0 ..< listModel.size).map(listModel::getElementAt)
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
