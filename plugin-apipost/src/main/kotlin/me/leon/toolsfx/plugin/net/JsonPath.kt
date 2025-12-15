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
    for ((i, param) in params.withIndex()) {
        if (!param.contains("[")) {
            if (d is Map<*, *>) {
                d = d[param]!!
            } else if (d is List<*>) {
                d = d.map { (it as Map<*, *>)[param]!! }
            } else if (d is Array<*>) {
                d = d.map { (it as Map<*, *>)[param]!! }
            }
        } else {
            val key = param.substringBefore("[")
            val index = param.substringAfter("[").replace("]", "")
            val isAll = index == "*"
            if (d is Map<*, *>) {
                d = d[key]!!
                if (d is List<*>) {
                    d = if (isAll) d else d[index.toInt()]!!
                } else if (d is Array<*>) {
                    d = if (isAll) d else d[index.toInt()]!!
                }
            }
        }
    }
    return d as? String ?: d.toJson()
}
