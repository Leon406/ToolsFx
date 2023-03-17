package me.leon.ext

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken

object GsonUtil {
    private val gson = Gson()

    fun toJson(s: Any): String = gson.toJson(s)

    fun <T> fromJson(json: String, clazz: Class<T>): T = gson.fromJson(json, clazz)

    fun <D> jsonToArrayList(json: String?, clazz: Class<D>?): ArrayList<D> {
        val type = object : TypeToken<ArrayList<JsonObject?>?>() {}.type
        val jsonObjects: ArrayList<JsonObject> = gson.fromJson(json, type)
        val arrayList = ArrayList<D>()
        for (jsonObject in jsonObjects) {
            arrayList.add(gson.fromJson(jsonObject, clazz))
        }
        return arrayList
    }
}

// json 转换扩展
fun Any.toJson() = GsonUtil.toJson(this)

fun <T> String.fromJson(clazz: Class<T>) = GsonUtil.fromJson(this, clazz)

inline fun <reified T> String.fromJson() = GsonUtil.fromJson(this, T::class.java)

fun <T> String.fromJsonArray(clazz: Class<T>) = GsonUtil.jsonToArrayList(this, clazz)
