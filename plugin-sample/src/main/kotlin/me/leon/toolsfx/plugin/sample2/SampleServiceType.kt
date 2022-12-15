package me.leon.toolsfx.plugin.sample2

enum class SampleServiceType(val type: String) : Sample2Service {
    FUNC_1("FUNC_1") {
        override fun process(raw: String, params: MutableMap<String, String>) = "111"
    },
    FUNC_2("FUNC_2") {
        override fun process(raw: String, params: MutableMap<String, String>) = "111"
    },
    FUNC_3("FUNC_3") {
        override fun process(raw: String, params: MutableMap<String, String>) = "111"
    },
    FUNC_4("FUNC_4") {
        override fun process(raw: String, params: MutableMap<String, String>) = "111"
    },
    FUNC_5("FUNC_5") {
        override fun process(raw: String, params: MutableMap<String, String>) = "111"
    },
}

val serviceTypeMap = SampleServiceType.values().associateBy { it.type }

fun String.locationServiceType() = serviceTypeMap[this] ?: SampleServiceType.FUNC_1
