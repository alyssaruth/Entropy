import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

val CURRENT_TIME: Instant = Instant.parse("2020-04-13T11:04:00.00Z")
val CURRENT_TIME_STRING: String =
    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        .withLocale(Locale.UK)
        .withZone(ZoneId.systemDefault())
        .format(CURRENT_TIME)
