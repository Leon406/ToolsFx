package me.leon

import java.util.concurrent.TimeUnit
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources

class EventStreamClient {
    private val client =
        OkHttpClient.Builder()
            .writeTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build()

    var debug = true

    fun startEventStream(
        url: String,
        body: String,
        headers: Map<String, String> = emptyMap(),
        listener: EventSourceListener? = null,
    ): EventSource {
        val start = System.currentTimeMillis()
        if (debug) {
            println(">>>> $url")
        }
        val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()

        val request =
            Request.Builder()
                .url(url)
                .addHeader("Accept", "text/event-stream")
                .addHeader("Content-Type", "application/json")
                .also { headers.forEach { (key, value) -> it.addHeader(key, value) } }
                .method("POST", RequestBody.create(mediaType, body))
                .build()

        val factory = EventSources.createFactory(client)
        return factory.newEventSource(
            request,
            object : EventSourceListener() {
                override fun onOpen(eventSource: EventSource, response: Response) {
                    super.onOpen(eventSource, response)
                    if (debug) {
                        println("on open ${System.currentTimeMillis() - start} ms")
                    }
                    listener?.onOpen(eventSource, response)
                }

                override fun onEvent(
                    eventSource: EventSource,
                    id: String?,
                    type: String?,
                    data: String,
                ) {
                    super.onEvent(eventSource, id, type, data)
                    val stripeData = data.replace("<br>", System.lineSeparator()).replace("*", "")
                    if (debug) {
                        println("\t\t${type == null} data: $data")
                    }
                    listener?.onEvent(eventSource, id, type, stripeData)
                }

                override fun onClosed(eventSource: EventSource) {
                    super.onClosed(eventSource)
                    if (debug) {
                        println("<<<<on onClosed ${System.currentTimeMillis() - start} ms")
                    }
                    listener?.onClosed(eventSource)
                }

                override fun onFailure(
                    eventSource: EventSource,
                    t: Throwable?,
                    response: Response?,
                ) {
                    super.onFailure(eventSource, t, response)
                    if (debug) {
                        println("on onFailure ${t?.stackTraceToString()}")
                    }
                    // 取消流不处理
                    if (!t?.message.orEmpty().contains("CANCEL")) {
                        listener?.onFailure(eventSource, t, response)
                    }
                }
            },
        )
    }

    companion object {
        const val URL = "https://openrouter.ai/api/v1/chat/completions"
        const val KEY = "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"

        @JvmStatic
        fun main(args: Array<String>) {
            val body =
                """
                {"messages":[{"role":"user","content":"麦克斯韦方程"}],"stream":true,"temperature":0,"model":"google/gemini-flash-1.5-exp"}
            """
                    .trimIndent()
            EventStreamClient().startEventStream(URL, body, mapOf("Authorization" to "Bearer $KEY"))
        }
    }
}
