package routes.health

import routes.health.dto.HealthCheckResponse
import util.OnlineConstants

class HealthCheckService {
    fun healthCheck() = HealthCheckResponse(OnlineConstants.SERVER_VERSION)
}
