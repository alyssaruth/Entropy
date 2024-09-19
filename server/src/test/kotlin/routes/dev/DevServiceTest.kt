package routes.dev

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import routes.ClientException
import testCore.AbstractTest

class DevServiceTest : AbstractTest() {
    @Test
    fun `throws a client exception if supplied with an invalid commmand`() {
        val service = DevService()

        val ex = shouldThrow<ClientException> { service.processCommand("invalid") }
        ex.errorCode shouldBe "invalid.command"
    }

    @Test
    fun `should not raise an error for any valid commands`() {
        val service = DevService()

        DevCommand.entries.forEach { command ->
            shouldNotThrowAny { service.processCommand(command.value) }
        }
    }
}
