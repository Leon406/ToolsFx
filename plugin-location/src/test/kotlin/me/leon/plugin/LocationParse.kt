package me.leon.plugin

import javax.json.Json.createReader
import kotlin.test.Test
import me.leon.ext.*
import me.leon.toolsfx.plugin.CoordinatorTransform
import me.leon.toolsfx.plugin.LocationServiceType

class LocationParse {

    @Test
    fun geo() {

        //        LocationServiceType.GEO_TIAN.process("北京市延庆区延庆镇莲花池村前街50夕阳红养老院", mutableMapOf())
        //            .also {
        //                println(it)
        //            }
        //        LocationServiceType.GEO_REVERSE_TIAN.process("116.001608,40.453170",
        // mutableMapOf())
        //            .also {
        //                println(it)
        //            }
        LocationServiceType.GEO_REVERSE_BD.process("116.001608,40.453170", mutableMapOf()).also {
            println(it)
        }
        LocationServiceType.GEO_REVERSE_BD_GCJ.process("116.001608,40.453170", mutableMapOf())
            .also { println(it) }
        LocationServiceType.GEO_REVERSE_BD_BD09.process("116.001608,40.453170", mutableMapOf())
            .also { println(it) }
        //        LocationServiceType.GEO_BD.process("北京市延庆区延庆镇莲花池村前街50夕阳红养老院", mutableMapOf())
        //            .also {
        //                println(it)
        //            }
        //        LocationServiceType.GEO_AMAP.process("北京市延庆区延庆镇莲花池村前街50夕阳红养老院", mutableMapOf())
        //            .also {
        //                println(it)
        //            }
    }

    @Test
    fun location() {
        "C:\\Users\\Leon\\Desktop\\loc.txt"
            .toFile()
            .readLines()
            .map { it.split(" +".toRegex()).filter { it.isNotEmpty() } }
            .sortedBy { it[0].toInt() }
            .forEach { amapPoi(it) }
    }

    fun amapLocation(
        addr: List<String>,
        key: String = "282f521c5c372f233da702769e43bfba",
        city: String = "杭州"
    ) {
        "https://restapi.amap.com/v3/geocode/geo?address=${addr[1] + addr.last()}&output=json&key=$key&city=$city"
            .readStreamFromNet()
            .also {
                createReader(it).readObject().also {
                    it.getJsonArray("geocodes")
                        .map { it.asJsonObject() }
                        .forEach { jo ->
                            jo.getString("location").split(",").run {
                                CoordinatorTransform.distance(
                                        addr[4].toDouble(),
                                        addr[3].toDouble(),
                                        this[1].toDouble(),
                                        this[0].toDouble()
                                    )
                                    .also {
                                        if (it > 200) {
                                            println(
                                                "${jo.getString("level")} 误差: $it db $addr amap ${this@run}"
                                            )
                                        }
                                    }
                            }
                        }
                }
            }
    }

    fun amapPoi(
        addr: List<String>,
        key: String = "282f521c5c372f233da702769e43bfba",
        city: String = "杭州"
    ) {
        val location =
            (addr[1] + addr[2] + addr.last().replace("上城区".toRegex(), ""))
                .replace("流动人口服务室|便民服务中心|公共法律服务(?:站|中心)".toRegex(), "")
                .replaceAfterLast("农贸市场", "")
                .preHandle()

        ("http://restapi.amap.com/v3/place/text?key=$key&keywords=$location" +
                "&types=政府机构及社会团体;政府机关;政府机关相关&city=$city")
            .readFromNet()
            .also {
                it.fromJson(AmapSearchBean::class.java).also { bean ->
                    val location2 =
                        location
                            .replace(addr[1], "")
                            .replace(addr[2], "")
                            .replace("（.*）|村委会|\\(.*\\)".toRegex(), "")

                    val poi =
                        bean.pois?.firstOrNull { jo ->
                            val preHandle =
                                runCatching { jo.address.toString() }
                                    .getOrElse { jo.name.toString() }
                                    .preHandle(addr[1], addr[2])
                            val preHandle2 =
                                runCatching { jo.name.toString() }
                                    .getOrElse { jo.address.toString() }
                                    .preHandle(addr[1], addr[2])

                            location2.contains(preHandle).also {
                                if (it) println("location1 $addr full matched $preHandle")
                            } ||
                                location2.contains(preHandle2).also {
                                    if (it) println("location2 $addr full matched $preHandle")
                                }
                        }
                            ?: bean.pois?.firstOrNull {
                                it.name?.run {
                                    contains(addr[2]) && contains("居委会|村委会|综合服务中心|党群服务中心".toRegex())
                                }
                                    ?: false
                            }
                                ?: bean.pois?.first().also {
                                println("$location 可能不准确  \n\t\t${bean.pois}")
                            }
                    calculateDistance(poi, addr, location)
                }
            }
    }

    private fun calculateDistance(poi: AmapSearchBean.Poi?, addr: List<String>, location: String) {
        poi?.location?.split(",")?.run {
            CoordinatorTransform.distance(
                    addr[4].toDouble(),
                    addr[3].toDouble(),
                    this[1].toDouble(),
                    this[0].toDouble()
                )
                .also {
                    if (it > 200) {
                        println(
                            "\t\t$location  ${poi.address} ${poi.adname}${poi.name}" +
                                " \t\t误差: $it db $addr amap ${this@run}  "
                        )
                    }
                }
        }
    }

    private fun String.preHandle(pre: String = "", pre2: String = "") =
        replace("中间|老年活动室|对面|商铺|附近|\\d+层|$pre|$pre2".toRegex(), "")
            .replace("至", "-")
            .replace("（.*）|\\(.*\\)".toRegex(), "")
            .replaceAfter("号", "")
            .replaceAfterLast("楼", "")
}
