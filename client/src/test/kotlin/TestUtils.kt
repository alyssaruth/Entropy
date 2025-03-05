import com.github.alyssaburlton.swingtest.getChild
import http.dto.OnlineMessage
import javax.swing.JList
import online.screen.OnlineChatPanel

fun OnlineChatPanel.getMessages(): List<OnlineMessage> {
    val listModel = getChild<JList<OnlineMessage>>().model
    return (0 ..< listModel.size).map(listModel::getElementAt)
}
