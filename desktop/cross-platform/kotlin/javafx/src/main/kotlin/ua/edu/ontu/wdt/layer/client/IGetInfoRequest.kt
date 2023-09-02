package ua.edu.ontu.wdt.layer.client

import ua.edu.ontu.wdt.layer.dto.GetInfoDto

@Deprecated("implement IRequest")
fun interface IGetInfoRequest {

    companion object {
        const val TIMEOUT_MS = 1000
    }

    @Deprecated("implement IRequest")
    fun doRequest(ip: String): GetInfoDto
}