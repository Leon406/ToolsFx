package me.leon

import io.github.sashirestela.openai.SimpleOpenAI
import io.github.sashirestela.openai.domain.chat.ChatMessage
import java.util.Date
import kotlin.test.Test
import me.leon.ext.toFile
import me.leon.misc.SDF_TIME

val TEST_USER_MESSAGE = ChatMessage.UserMessage.of("Hi")

class OpenAiTest {

    val server = "https://llm.indrin.cn"
    val sk = "sk-9sN1wGBOUiwot0s277F709972eAa481dB64b8cEa39B47380"

    @Test
    fun models() {
        val openAI = makeAI()
        openAI
            .supportModels()
            .groupBy { it.ownedBy }
            .forEach { (k, v) ->
                println("===$k===\n${v.joinToString(System.lineSeparator()) { it.id }}")
            }
    }

    @Test
    fun complete() {
        val openAI = makeAI()
        val model = "gpt-4o"
        val chatResponse =
            openAI.complete(
                model,
                listOf(
                    ChatMessage.SystemMessage.of("You are an expert in AI."),
                    ChatMessage.UserMessage.of(
                        "Write a technical article about ChatGPT, no more than 100 words."
                    ),
                ),
                0.0,
            )
        if (chatResponse?.choices?.isNotEmpty() == true) {
            println(chatResponse.firstContent())
        }
    }

    @Test
    fun completeStream() {
        val openAI = makeAI()
        val model = "deepseek-67b"
        println("----------------------req")
        val chatResponse =
            openAI.completeStream(
                model,
                listOf(
                    ChatMessage.SystemMessage.of("You are an expert in AI."),
                    ChatMessage.UserMessage.of(
                        "Write a technical article about ChatGPT, no more than 100 words."
                    ),
                ),
                0.0,
            )
        println("----------------------")
        chatResponse
            ?.filter { it.choices.isNotEmpty() && it.firstContent() != null }
            ?.forEach { print(it.firstContent()) }
    }

    @Test
    fun validateModels() {
        val openAI = makeAI()
        val failedModels = mutableSetOf<String>()
        val okModels = mutableSetOf<String>()
        openAI
            .supportModels()
            .map { it.id }
            .map {
                it to runCatching { openAI.complete(it, listOf(TEST_USER_MESSAGE)) }.getOrNull()
            }
            .forEach { (model, chat) ->
                if (chat?.choices?.isEmpty() == false && chat.firstContent() != null) {
                    println("$model: ${chat.firstContent()}")
                    okModels.add(model)
                } else {
                    failedModels.add(model)
                }
            }

        if (okModels.isNotEmpty()) {
            println("-------------available---------------")
            println(okModels.joinToString(System.lineSeparator()))
        }
        if (failedModels.isNotEmpty()) {
            println("-------------failed---------------")
            println(failedModels.joinToString(System.lineSeparator()))
        }
    }

    @Test
    fun quota() {
        with(quota(server, sk)) {
            val untilInfo =
                when {
                    third == 0L -> "无限"
                    System.currentTimeMillis() > third -> "已过期"
                    else -> "过期时间：${SDF_TIME.format(Date(third))}"
                }
            println("使用量：${first.format()} / ${second.format()}\n有效期：$untilInfo")
        }
    }

    @Test
    fun model() {
        models(server, sk)
            .groupBy { it.ownedBy }
            .forEach { (k, v) ->
                println("===$k===\n${v.joinToString(System.lineSeparator()) { it.id }}")
            }
    }

    @Test
    fun gp() {
        group(server, sk).forEach { println(it) }
    }

    @Test
    fun parse() {
        val USER = System.getenv("userprofile")
        "$USER/desktop/keys.txt".toFile().readLines().forEach {
            val (api, key) = it.split("\t")
            val aks = key.split(",", ";").toSet()
            aks.map { it to quota(api, it) }
                .forEach { (key, tr) ->
                    val untilInfo =
                        when {
                            tr.third == 0L -> "无限"
                            System.currentTimeMillis() > tr.third -> "已过期"
                            else -> "过期时间：${SDF_TIME.format(Date(tr.third))}"
                        }

                    println("====>$api $key")
                    println("使用量：${tr.first.format()} / ${tr.second.format()}\n有效期：$untilInfo")
                    if (untilInfo != "已过期" && tr.second != 0.0) {
                        models(api, key)
                            .groupBy { it.ownedBy }
                            .forEach { (k, v) ->
                                println(
                                    "===$k===\n${v.joinToString(System.lineSeparator()) { it.id }}"
                                )
                            }
                    }
                }
        }
    }

    private fun makeAI(): SimpleOpenAI {
        return makeAI(server, sk)
    }
}
