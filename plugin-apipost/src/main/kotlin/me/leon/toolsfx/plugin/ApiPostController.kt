package me.leon.toolsfx.plugin

import java.io.File
import me.leon.toolsfx.plugin.net.*
import tornadofx.*

class ApiPostController : Controller() {

    private fun replacePlaceHolder(maps: MutableMap<String, Any>): MutableMap<String, Any> {
        maps.entries.filter { it.value is String }.forEach { entry ->
            maps[entry.key] = (entry.value as String).replacePlaceHolders()
        }
        return maps
    }

    private fun request(
        url: String,
        method: String,
        params: MutableMap<String, Any> = mutableMapOf(),
        headers: MutableMap<String, Any> = mutableMapOf(),
    ): Response {
        println("req: $url")
        return HttpUrlUtil.request(
            url.replacePlaceHolders(),
            method,
            replacePlaceHolder(params),
            replacePlaceHolder(headers)
        )
    }

    fun request(
        url: String,
        method: String,
        params: MutableMap<String, Any> = mutableMapOf(),
        headers: MutableMap<String, Any> = mutableMapOf(),
        bodyType: BodyType = BodyType.RAW,
    ) =
        if (method != "POST") request(url, method, params, headers)
        else if (bodyType == BodyType.RAW) kotlin.error("call postRaw")
        else {
            HttpUrlUtil.post(
                url.replacePlaceHolders(),
                replacePlaceHolder(params),
                replacePlaceHolder(headers),
                bodyType == BodyType.JSON
            )
        }

    fun postRaw(
        url: String,
        data: String,
        headers: MutableMap<String, Any> = mutableMapOf(),
    ): Response {
        return HttpUrlUtil.postData(
            url.replacePlaceHolders(),
            data.replacePlaceHolders(),
            replacePlaceHolder(headers)
        )
    }

    fun post(
        url: String,
        params: MutableMap<String, Any> = mutableMapOf(),
        headers: MutableMap<String, Any> = mutableMapOf(),
        isJson: Boolean = false
    ): Response {
        return HttpUrlUtil.post(
            url.replacePlaceHolders(),
            replacePlaceHolder(params),
            replacePlaceHolder(headers),
            isJson
        )
    }

    fun uploadFile(
        url: String,
        files: List<File>,
        fileParamName: String,
        params: MutableMap<String, Any> = mutableMapOf(),
        headers: MutableMap<String, Any> = mutableMapOf()
    ): Response {
        return HttpUrlUtil.postFile(
            url.replacePlaceHolders(),
            files,
            fileParamName,
            replacePlaceHolder(params),
            replacePlaceHolder(headers)
        )
    }

    fun parseHeaderString(headers: String) = NetHelper.parseHeaderString(headers)
}
