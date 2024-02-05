package me.leon.toolsfx.plugin

import me.leon.ext.fromJson
import me.leon.ext.readFromNet
import me.leon.hash
import me.leon.toolsfx.KeyProvider
import me.leon.toolsfx.domain.*
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
    GEO_AMAP("geoAmap") {
        override fun process(raw: String, params: MutableMap<String, String>): String {
            val address = "address=${raw.urlEncoded}"
            val queries = "address=$raw&key=${KeyProvider.KEY_AMAP}&output=json"
            return ("https://restapi.amap.com/v3/geocode/geo?" +
                    "$address&key=${KeyProvider.KEY_AMAP}&output=json" +
                    "&sig=${(queries + "c7753ad6eef2064d6d2aa35a927be951").hash("md5")}")
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
                    "&output=json&ak=${KeyProvider.KEY_BD}")
                .readFromNet()
                .fromJson(BaiduGeo::class.java)
                .geoInfo()
        }
    },
    GEO_BD_GCJ("geoBaidu-GCJ") {
        override fun process(raw: String, params: MutableMap<String, String>): String {
            return ("http://api.map.baidu.com/geocoding/v3/?address=${raw.urlEncoded}" +
                    "&ret_coordtype=gcj02ll&output=json&ak=${KeyProvider.KEY_BD}")
                .readFromNet()
                .fromJson(BaiduGeo::class.java)
                .geoInfo()
        }
    },
    GEO_TIAN("geoTian") {
        override fun process(raw: String, params: MutableMap<String, String>): String {
            return (("https://api.tianditu.gov.cn/geocoder?" +
                        "ds={\"keyWord\":\"${raw.urlEncoded}\"}&tk=${KeyProvider.KEY_TIAN}")
                    .also { println(it) })
                .readFromNet(headers = mapOf("User-Agent" to "curl"))
                .also { println(it) }
                .fromJson(TianGeo::class.java)
                .geoInfo()
        }
    },
    GEO_REVERSE_TIAN("geoRevTian") {
        override fun process(raw: String, params: MutableMap<String, String>): String {
            val (lon, lat) = raw.split(",")
            return (("https://api.tianditu.gov.cn/geocoder" +
                        "?postStr={'lon':$lon,'lat':$lat,'ver':1}&type=geocode&tk=${KeyProvider.KEY_TIAN}")
                    .also { println(it) })
                .readFromNet(headers = mapOf("User-Agent" to ""))
                .also { println(it) }
                .fromJson(TianReverseGeo::class.java)
                .formatLocation()
        }
    },

    /** https://lbs.baidu.com/faq/api?title=webapi/guide/webservice-geocoding-abroad-base */
    GEO_REVERSE_BD("geoRevBD-WGS") {
        override fun process(raw: String, params: MutableMap<String, String>): String {
            return baiduGeoReverse(raw)
        }
    },
    GEO_REVERSE_BD_GCJ("geoRevBD-GCJ") {
        override fun process(raw: String, params: MutableMap<String, String>): String {
            return baiduGeoReverse(raw, "gcj02ll")
        }
    },
    GEO_REVERSE_BD_BD09("geoRevBD") {
        override fun process(raw: String, params: MutableMap<String, String>): String {
            return baiduGeoReverse(raw, "bd09ll")
        }
    };

    fun baiduGeoReverse(raw: String, coordType: String = "wgs84ll"): String {
        val (lon, lat) = raw.split(",")
        return (("http://api.map.baidu.com/reverse_geocoding/v3/?location=$lat,$lon" +
                    "&coordtype=$coordType&output=json&ak=${KeyProvider.KEY_BD}")
                .also { println(it) })
            .readFromNet(headers = mapOf("User-Agent" to ""))
            .also { println(it) }
            .fromJson(TianReverseGeo::class.java)
            .formatLocation()
    }
}

val locationServiceTypeMap = LocationServiceType.values().associateBy { it.type }

fun String.locationServiceType() = locationServiceTypeMap[this] ?: LocationServiceType.WGS2GCJ
