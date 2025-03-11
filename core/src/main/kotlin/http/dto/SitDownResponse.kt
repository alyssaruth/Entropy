package http.dto

data class SitDownResponse(val players: Map<Int, String>, val formerPlayers: Map<Int, String>)
