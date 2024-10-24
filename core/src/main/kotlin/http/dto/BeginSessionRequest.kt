package http.dto

import util.OnlineConstants

data class BeginSessionRequest(
    val name: String,
    val achievementCount: Int,
    val apiVersion: Int = OnlineConstants.API_VERSION,
)
