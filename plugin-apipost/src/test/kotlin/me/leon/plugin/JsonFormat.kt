package me.leon.plugin

import me.leon.toolsfx.plugin.prettyJson
import org.junit.Test

class JsonFormat {
    @Test
    fun formatTest() {
        val json = """{"key1":"v1","key2":"v2"}"""
        val jsonArray =
            """[{"key1":"v1","key2":"v2"},{"key1":"v1","key2":"v2"},{"key1":"v1","key2":"v2"}]""".trimMargin()

        println(json.prettyJson())
        println(jsonArray.prettyJson())
        println("[\"yht\",\"xzj\",\"zwy\"]".prettyJson())
        println(
            ("[{\"name\":\"cylx\",\"trans\":[\"全乱高手\",\"臭鱼烂虾\"],\"ojb\":{\"dd\":\"22\"}}," +
                    "{\"name\":\"cylx\",\"trans\":[\"全乱高手\",\"臭鱼烂虾\"],\"ojb\":{\"dd\":\"22\"}}]")
                .prettyJson()
        )
    }
}
