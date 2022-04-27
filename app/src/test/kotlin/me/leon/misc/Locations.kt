package me.leon.misc

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import me.leon.ext.toFile
import org.junit.Test

class Locations {
    @Test
    fun readJson() {
        JsonReader("C:\\Users\\Leon\\Downloads\\villages.json".toFile().reader()).use {
            val gson = Gson()
            val locations =
                gson.fromJson<ArrayList<Village>>(
                    it,
                    object : TypeToken<ArrayList<Village>>() {}.type
                )
            println(locations.size)
            locations.filter { it.code.startsWith("330523") }.map { "湖州市安吉县${it.name}" }.forEach {
                println(it)
            }
        }
    }
}
