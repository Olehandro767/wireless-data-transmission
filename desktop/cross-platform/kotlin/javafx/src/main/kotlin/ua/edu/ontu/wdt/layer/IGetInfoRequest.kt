package ua.edu.ontu.wdt.layer

import ua.edu.ontu.wdt.layer.dto.GetInfoDto

fun interface IGetInfoRequest {

    fun doRequest(ip: String): GetInfoDto
}