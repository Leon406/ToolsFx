package me.leon.toolsfx.plugin

import me.leon.ext.fromJson
import me.leon.ext.readFromNet
import me.leon.hash
import me.leon.toolsfx.domain.AmapGeo
import me.leon.toolsfx.domain.BaiduGeo
import tornadofx.*

enum class LocationServiceType(val type: String) : ILocationService {
    WGS2GCJ("wgs2gcj") {
        override fun process(raw: String, params: MutableMap<String, String>) =
            CoordinatorTransform.wgs2GCJ(
                    raw.substringAfter(",").toDouble(),
                    raw.substringBefore(",").toDouble()
                )
                .reversed()
                .joinToString(",") { String.format("%.6f", it) }
    },
    WGS2BD09("wgs2bd09") {
        override fun process(raw: String, params: MutableMap<String, String>) =
            CoordinatorTransform.wgs2BD09(
                    raw.substringAfter(",").toDouble(),
                    raw.substringBefore(",").toDouble()
                )
                .reversed()
                .joinToString(",") { String.format("%.6f", it) }
    },
    GCJ2BD09("gcj2bd09") {
        override fun process(raw: String, params: MutableMap<String, String>) =
            CoordinatorTransform.gcj2BD09(
                    raw.substringAfter(",").toDouble(),
                    raw.substringBefore(",").toDouble()
                )
                .reversed()
                .joinToString(",") { String.format("%.6f", it) }
    },
    GCJ2WGS("gcj2wgs") {
        override fun process(raw: String, params: MutableMap<String, String>) =
            CoordinatorTransform.gcj2WGSExactly(
                    raw.substringAfter(",").toDouble(),
                    raw.substringBefore(",").toDouble()
                )
                .reversed()
                .joinToString(",") { String.format("%.6f", it) }
    },
    BD092WGS("bd092wgs") {
        override fun process(raw: String, params: MutableMap<String, String>) =
            CoordinatorTransform.bd092WGSExactly(
                    raw.substringAfter(",").toDouble(),
                    raw.substringBefore(",").toDouble()
                )
                .reversed()
                .joinToString(",") { String.format("%.6f", it) }
    },
    BD092GCJ("bd092gcj") {
        override fun process(raw: String, params: MutableMap<String, String>) =
            CoordinatorTransform.bd092GCJ(
                    raw.substringAfter(",").toDouble(),
                    raw.substringBefore(",").toDouble()
                )
                .reversed()
                .joinToString(",") { String.format("%.6f", it) }
    },
    DISTANCE("distance") {
        override fun process(raw: String, params: MutableMap<String, String>): String {
            val (p1, p2) = raw.split("-")
            val (p1Lng, p1Lat) = p1.split(",").map { it.toDouble() }
            val (p2Lng, p2Lat) = p2.split(",").map { it.toDouble() }
            return String.format(
                "%.2f m",
                CoordinatorTransform.distance(p1Lat, p1Lng, p2Lat, p2Lng)
            )
        }
    },
    GEO_AMPA("geoAmap") {
        override fun process(raw: String, params: MutableMap<String, String>): String {
            val address = "address=${raw.urlEncoded}"
            val queries = "address=$raw&key=282f521c5c372f233da702769e43bfba&output=json"
            return ("https://restapi.amap.com/v3/geocode/geo?" +
                    "$address&key=282f521c5c372f233da702769e43bfba&output=json" +
                    "&sig=${(queries + "57b0452167c85d33217472e4e53028ec").hash("md5")}")
                .also { println(it) }
                .readFromNet()
                .also { println(it) }
                .fromJson(AmapGeo::class.java)
                .geoInfo()
        }
    },
    GEO_BD("geoBaidu") {
        override fun process(raw: String, params: MutableMap<String, String>): String {
            return ("http://api.map.baidu.com/geocoding/v3/?address=${raw.urlEncoded}" +
                    "&output=json&ak=V0AKhZ3wN8CTU3zx8lGf4QvwyOs5rGIn")
                .readFromNet()
                .fromJson(BaiduGeo::class.java)
                .geoInfo()
        }
    },
}

val locationServiceTypeMap = LocationServiceType.values().associateBy { it.type }

fun String.locationServiceType() = locationServiceTypeMap[this] ?: LocationServiceType.WGS2GCJ
