package me.leon.plugin

import me.leon.toolsfx.plugin.prettyJson
import me.leon.toolsfx.plugin.uglyJson
import org.junit.Test

class JsonFormat {
    @Test
    fun formatTest() {

        val j = "{\"delay\":533}\n"
        println(j.prettyJson())
        val json = """{"key1":"v1","key2":"v2"}"""
        val jsonArray =
            """[{"key1":"v1","key2":"v2"},{"key1":"v1","key2":"v2"},{"key1":"v1","key2":"v2"}]"""
                .trimMargin()

        println(json.prettyJson())
        println(jsonArray.prettyJson())
        println("[\"yht\",\"xzj\",\"zwy\"]".prettyJson())
        println(
            ("[{\"name\":\"cylx\",\"trans\":[\"全乱高手\",\"臭鱼烂虾\"],\"ojb\":{\"dd\":\"22\"}}," +
                    "{\"name\":\"cylx\",\"trans\":[\"全乱高手\",\"臭鱼烂虾\"],\"ojb\":{\"dd\":\"22\"}}]")
                .prettyJson()
        )
    }

    @Test
    fun minimize() {
        var raw =
            "{\n" +
                "   \"one\" : \"AAA\",\n" +
                "   \"two\" : [ \"BBB\", \"CCC\" ],\n" +
                "   \"three\" : {\n" +
                "     \"four\" : \"DDD\",\n" +
                "     \"five\" : [ \"EEE\", \"FFF\" ]\n" +
                "   }\n" +
                " }"
        println(raw.uglyJson())
        raw = "{\"fact1\": \"Java: is dd verbose.\",\"fact2\": \"C has pointers.\"}"

        println(raw.uglyJson())

        raw =
            "{\"data\": \"{\\\"one\\\":\\\"AAA\\\",\\\"two\\\":[\\\"BBB\\\",\\\"CCC\\\"],\\\"three\\\":{\\\"four\\\":" +
                "\\\"DDD\\\",\\\"five\\\":[\\\"EEE\\\",\\\"FFF\\\"]}}\"}"
        println(raw)
        println(raw.prettyJson())
    }

    @Test
    fun ipTest() {
        val mask = "255.255.255.0".split(".").map { it.toInt() }
        val ip = "192.168.177.129".split(".").map { it.toInt() }

        ip.zip(mask) { a, b -> a and b }.forEach { println(it) }
    }
}
