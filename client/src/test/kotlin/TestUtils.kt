import com.github.alyssaburlton.swingtest.getChild
import http.ClientErrorCode
import http.FailureResponse
import http.dto.OnlineMessage
import javax.swing.JList
import online.screen.OnlineChatPanel

fun OnlineChatPanel.getMessages(): List<OnlineMessage> {
    val listModel = getChild<JList<OnlineMessage>>().model
    return (0 ..< listModel.size).map(listModel::getElementAt)
}

inline fun <reified T> makeFailureResponse(
    errorCode: ClientErrorCode = ClientErrorCode("generic.error"),
    errorMessage: String = "oh no!"
) = FailureResponse<T>(422, "", errorCode, errorMessage)
