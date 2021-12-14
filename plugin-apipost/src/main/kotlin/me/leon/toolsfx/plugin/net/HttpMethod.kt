package me.leon.toolsfx.plugin.net

import java.io.File

object HttpMethod {

    fun get(
        url: String,
        params: MutableMap<String, Any> = mutableMapOf(),
        headers: MutableMap<String, Any> = mutableMapOf(),
        isDownload: Boolean = false
    ) = HttpUrlUtil.get(url, params, headers, isDownload)

    fun head(
        url: String,
        params: MutableMap<String, Any> = mutableMapOf(),
        headers: MutableMap<String, Any> = mutableMapOf()
    ) = HttpUrlUtil.request(url, "HEAD", params, headers)

    fun put(
        url: String,
        params: MutableMap<String, Any> = mutableMapOf(),
        headers: MutableMap<String, Any> = mutableMapOf()
    ) = HttpUrlUtil.request(url, "PUT", params, headers)

    fun trace(
        url: String,
        params: MutableMap<String, Any> = mutableMapOf(),
        headers: MutableMap<String, Any> = mutableMapOf()
    ) = HttpUrlUtil.request(url, "TRACE", params, headers)

    fun patch(
        url: String,
        params: MutableMap<String, Any> = mutableMapOf(),
        headers: MutableMap<String, Any> = mutableMapOf()
    ): Response {
        return HttpUrlUtil.request(url, "PATCH", params, headers)
    }

    fun delete(
        url: String,
        params: MutableMap<String, Any> = mutableMapOf(),
        headers: MutableMap<String, Any> = mutableMapOf()
    ) = HttpUrlUtil.request(url, "DELETE", params, headers)

    fun options(
        url: String,
        params: MutableMap<String, Any> = mutableMapOf(),
        headers: MutableMap<String, Any> = mutableMapOf()
    ) = HttpUrlUtil.request(url, "OPTIONS", params, headers)

    fun connect(
        url: String,
        params: MutableMap<String, Any> = mutableMapOf(),
        headers: MutableMap<String, Any> = mutableMapOf()
    ) = HttpUrlUtil.request(url, "CONNECT", params, headers)

    fun post(
        url: String,
        params: MutableMap<String, Any> = mutableMapOf(),
        headers: MutableMap<String, Any> = mutableMapOf(),
        isJson: Boolean = false,
    ) = HttpUrlUtil.post(url, params, headers, isJson)

    fun postFile(
        url: String,
        files: List<File>,
        name: String = "file",
        params: MutableMap<String, Any> = mutableMapOf(),
        headers: MutableMap<String, Any> = mutableMapOf()
    ) = HttpUrlUtil.postFile(url, files, name, params, headers)

    fun postData(url: String, data: String, headers: MutableMap<String, Any> = mutableMapOf()) =
        HttpUrlUtil.postData(url, data, headers)
}
