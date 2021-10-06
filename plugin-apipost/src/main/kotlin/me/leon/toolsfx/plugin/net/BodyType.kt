package me.leon.toolsfx.plugin.net

enum class BodyType(val type: String) {
    RAW("raw text"),
    FORM_DATA("form-data"),
    JSON("json"),
}

val bodyTypeMap = BodyType.values().associateBy { it.type }
