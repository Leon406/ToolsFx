package me.leon.toolsfx.plugin.net

import me.leon.ext.fromJson
import me.leon.ext.toJson

/**
 * @author Leon
 * @since 2023-06-07 11:59
 * @email deadogone@gmail.com
 */
fun String.simpleJsonPath(path: String): String {
    if (path.isEmpty()) {
        return this
    }
    val params = path.split(".")
    var d: Any = fromJson(LinkedHashMap::class.java)
    for (param in params) {
        if (!param.contains("[")) {
            if (d is Map<*, *>) {
                d = d[param]!!
            }
        } else {
            val key = param.substringBefore("[")
            val index = param.substringAfter("[").replace("]", "")
            if (d is Map<*, *>) {
                d = d[key]!!
                if (d is List<*>) {
                    d = d[index.toInt()]!!
                } else if (d is Array<*>) {
                    d = d[index.toInt()]!!
                }
            }
        }
    }
    return if (d is String) {
        d
    } else {
        d.toJson()
    }
}
