package me.leon.toolsfx.plugin

import java.io.File
import me.leon.toolsfx.plugin.net.*
import tornadofx.*

class ApiPostController : Controller() {

    private fun request(
        url: String,
        method: String,
        params: MutableMap<String, Any> = mutableMapOf(),
        headers: MutableMap<String, Any> = mutableMapOf(),
    ): Response {
        println("req: $url")
        return HttpUrlUtil.request(url, method, params, headers)
    }

    fun request(
        url: String,
        method: String,
        params: MutableMap<String, Any> = mutableMapOf(),
        headers: MutableMap<String, Any> = mutableMapOf(),
        bodyType: BodyType = BodyType.RAW,
    ) =
        if (method != "POST") request(url, method, params, headers)
        else if (bodyType == BodyType.RAW) throw IllegalArgumentException("call postRaw")
        else HttpUrlUtil.post(url, params, headers, bodyType == BodyType.JSON)

    fun postRaw(
        url: String,
        data: String,
        headers: MutableMap<String, Any> = mutableMapOf(),
    ): Response {
        return HttpUrlUtil.postData(url, data, headers)
    }

    fun post(
        url: String,
        params: MutableMap<String, Any> = mutableMapOf(),
        headers: MutableMap<String, Any> = mutableMapOf(),
        isJson: Boolean = false
    ): Response {
        return HttpUrlUtil.post(url, params, headers, isJson)
    }

    fun uploadFile(
        url: String,
        files: List<File>,
        fileParamName: String,
        params: MutableMap<String, Any> = mutableMapOf(),
        headers: MutableMap<String, Any> = mutableMapOf()
    ): Response {
        return HttpUrlUtil.postFile(url, files, fileParamName, params, headers)
    }

    fun parseHeaderString(headers: String) = NetHelper.parseHeaderString(headers)
}
