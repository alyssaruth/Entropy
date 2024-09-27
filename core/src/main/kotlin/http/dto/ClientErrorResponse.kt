package http.dto

import http.ClientErrorCode

data class ClientErrorResponse(val errorCode: ClientErrorCode, val errorMessage: String)
