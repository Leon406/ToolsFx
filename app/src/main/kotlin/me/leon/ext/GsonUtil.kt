package me.leon.ext

import com.google.gson.Gson

/**
 * <p>description： gson 工具</p> <p>author：Leon</p> <p>date：2019/3/28 0028</p>
 * <p>e-mail：deadogone@gmail.com</p>
 */
object GsonUtil {
    private val gson = Gson()

    fun toJson(s: Any): String = gson.toJson(s)

    fun <T> fromJson(json: String, clazz: Class<T>): T = gson.fromJson<T>(json, clazz)
}

// json 转换扩展
fun Any.toJson() = GsonUtil.toJson(this)

fun <T> String.fromJson(clazz: Class<T>) = GsonUtil.fromJson(this, clazz)
