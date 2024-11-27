package me.leon

import com.google.gson.annotations.SerializedName
import io.github.sashirestela.openai.SimpleOpenAI
import io.github.sashirestela.openai.domain.chat.Chat
import io.github.sashirestela.openai.domain.chat.ChatMessage
import io.github.sashirestela.openai.domain.chat.ChatRequest
import java.text.NumberFormat
import java.util.stream.Stream
import me.leon.ext.fromJson
import me.leon.ext.stacktrace
import me.leon.toolsfx.plugin.net.HttpUrlUtil

fun makeAI(baseUrl: String, sk: String): SimpleOpenAI {
    val openAI = SimpleOpenAI.builder().baseUrl(baseUrl).apiKey(sk).build()
    return openAI
}

fun SimpleOpenAI.complete(
    model: String,
    messages: List<ChatMessage>,
    temperature: Double = 0.7,
): Chat? {
    val chatRequest =
        ChatRequest.builder().model(model).messages(messages).temperature(temperature).build()
    val futureChat = chatCompletions().create(chatRequest)
    return futureChat.join()
}

fun SimpleOpenAI.completeStream(
    model: String,
    messages: List<ChatMessage>,
    temperature: Double = 0.7,
): Stream<Chat>? {
    val chatRequest =
        ChatRequest.builder().model(model).messages(messages).temperature(temperature).build()
    val futureChat = chatCompletions().createStream(chatRequest)
    return futureChat.join()
}

fun SimpleOpenAI.supportModels() = models().list.get()

const val PATH_SUBSCRIPTION = "/dashboard/billing/subscription"
const val PATH_USAGE = "/dashboard/billing/usage"
const val PATH_MODELS = "/v1/models"
const val PATH_GROUP = "/api/user/group"
const val PATH_MODEL_PRICE = "/api/pricing?group=default"

fun quota(baseUrl: String, sk: String): Triple<Double, Double, Long> {
    var total = 0.0
    var used = 0.0
    var time = 0L
    runCatching {
        HttpUrlUtil.get(
                baseUrl + PATH_SUBSCRIPTION,
                headers = mutableMapOf("Authorization" to "Bearer $sk"),
            )
            .data
            .also {
                val sub = it.fromJson(Subscription::class.java)
                total = sub.hardLimitUsd
                time = sub.accessUntil
                HttpUrlUtil.get(
                        baseUrl + PATH_USAGE,
                        headers = mutableMapOf("Authorization" to "Bearer $sk"),
                    )
                    .data
                    .also { used = it.fromJson(Usage::class.java).totalUsageInUsd }
            }
    }
    return Triple(used, total, time)
}

fun models(baseUrl: String, sk: String): List<ModelInfo.Model> {
    runCatching {
            return HttpUrlUtil.get(
                    baseUrl + PATH_MODELS,
                    headers = mutableMapOf("Authorization" to "Bearer $sk"),
                )
                .data
                .fromJson(ModelInfo::class.java)
                .data
        }
        .onFailure { println(it.stacktrace()) }
    return emptyList()
}

fun group(baseUrl: String, sk: String): List<Group.GP> {
    runCatching {
            return HttpUrlUtil.get(
                    baseUrl + PATH_GROUP,
                    headers = mutableMapOf("Authorization" to "Bearer $sk"),
                )
                .data
                .also { println(it) }
                .fromJson(Group::class.java)
                .data
        }
        .onFailure { println(it.stacktrace()) }
    return emptyList()
}

data class Subscription(
    @SerializedName("access_until") val accessUntil: Long,
    @SerializedName("hard_limit_usd") val hardLimitUsd: Double,
    @SerializedName("has_payment_method") val hasPaymentMethod: Boolean,
    @SerializedName("object") val obj: String,
    @SerializedName("soft_limit_usd") val softLimitUsd: Double,
    @SerializedName("system_hard_limit_usd") val systemHardLimitUsd: Double,
)

data class Usage(
    @SerializedName("object") val obj: String,
    @SerializedName("total_usage") val totalUsage: Double,
) {
    val totalUsageInUsd
        get() = totalUsage / 100
}

fun Double.format(maximum: Int = 4) =
    NumberFormat.getInstance()
        .apply {
            isGroupingUsed = false
            maximumFractionDigits = maximum
        }
        .format(this)

data class ModelInfo(val data: List<Model>, val success: Boolean) {
    data class Model(
        val created: Int,
        val id: String,
        val `object`: String,
        @SerializedName("owned_by") val ownedBy: String,
        val parent: Any,
        val root: String,
    )
}

data class Group(val message: String, val code: Int, val data: List<GP>) {
    data class GP(@SerializedName("group_name") val groupName: String, val ratio: Double)
}
