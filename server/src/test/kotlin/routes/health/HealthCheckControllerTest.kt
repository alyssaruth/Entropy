package routes.health

import http.Routes
import io.kotest.matchers.shouldBe
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.jupiter.api.Test
import testCore.AbstractTest
import testCore.shouldMatchJson
import util.OnlineConstants

class HealthCheckControllerTest : AbstractTest() {
    @Test
    fun `Should respond to a health check request`() = testApplication {
        val response = client.get(Routes.HEALTH_CHECK)
        response.status shouldBe HttpStatusCode.OK
        response.bodyAsText() shouldMatchJson
            """
            {
                "version": "${OnlineConstants.SERVER_VERSION}"
            }
        """
                .trimIndent()
    }
}
