package me.leon.toolsfx.plugin

import java.util.regex.Pattern
import kotlin.math.*

/**
 * 坐标转换程序
 *
 * WGS84坐标系：即地球坐标系，国际上通用的坐标系。Earth
 *
 * GCJ02坐标系：即火星坐标系，WGS84坐标系经加密后的坐标系。Mars
 *
 * BD09坐标系：即百度坐标系，GCJ02坐标系经加密后的坐标系。 Bd09
 *
 * @link http://lbsyun.baidu.com/index.php?title=webapi/guide/changeposition
 *
 * 搜狗坐标系、图吧坐标系等，估计也是在GCJ02基础上加密而成的。
 *
 * 百度地图API 百度坐标 腾讯搜搜地图API 火星坐标 搜狐搜狗地图API 搜狗坐标* 阿里云地图API 火星坐标 图吧MapBar地图API 图吧坐标 高德MapABC地图API 火星坐标
 * 灵图51ditu地图API 火星坐标
 *
 * 经度 lnggtitude lng 助记: 长的是经度 纬度 latitude lat
 */
object CoordinatorTransform {
    private const val PI = Math.PI
    private const val AXIS = 6_378_245.0 //
    private const val OFFSET = 0.00669342162296594323 // (a^2 - b^2) / a^2
    private const val X_PI = PI * 3000.0 / 180.0
    private val PATTERN_DEGREE_LOCATION =
        Pattern.compile("(\\d{1,3})° *(\\d{1,2})′ *(\\d{1,2}\\.\\d+)″")

    private val map: Map<String, Map<String, Double>> =
        mapOf(
            "wgs" to
                mapOf(
                    "a" to 6_378_137.0,
                    "b" to 6_356_752.3142,
                    "e1Square" to 0.00669437999013,
                    "e2Square" to 0.006739496742227,
                    "ratio" to 1.0 / 298.257223563,
                ),
            "cgcs2000" to
                mapOf(
                    "a" to 6_378_137.0,
                    "b" to 66_356_752.314,
                    "e1Square" to 0.00669438002290,
                    "ratio" to 1.0 / 298.257222101,
                ),
        )

    // GCJ-02=>BD09 火星坐标系=>百度坐标系  10位小数 跟百度api一样
    fun gcj2BD09(glat: Double, glng: Double): DoubleArray {
        val latlng = DoubleArray(2)
        val z = sqrt(glng * glng + glat * glat) + 0.00002 * sin(glat * X_PI)
        val theta = atan2(glat, glng) + 0.000003 * cos(glng * X_PI)
        latlng[0] = z * sin(theta) + 0.006
        latlng[1] = z * cos(theta) + 0.0065
        return latlng
    }

    // BD09=>GCJ-02 百度坐标系=>火星坐标系
    fun bd092GCJ(glat: Double, glng: Double): DoubleArray {
        val x = glng - 0.0065
        val y = glat - 0.006
        val latlng = DoubleArray(2)
        val z = sqrt(x * x + y * y) - 0.00002 * sin(y * X_PI)
        val theta = atan2(y, x) - 0.000003 * cos(x * X_PI)
        latlng[0] = z * sin(theta)
        latlng[1] = z * cos(theta)
        return latlng
    }

    // BD09=>WGS84 百度坐标系=>地球坐标系
    fun bd092WGS(glat: Double, glng: Double): DoubleArray {
        val latlng = bd092GCJ(glat, glng)
        return gcj2WGS(latlng[0], latlng[1])
    }

    // BD09=>WGS84 百度坐标系=>地球坐标系
    fun bd092WGSExactly(glat: Double, glng: Double): DoubleArray {
        val latlng = bd092GCJ(glat, glng)
        return gcj2WGSExactly(latlng[0], latlng[1])
    }

    // WGS84=》BD09   地球坐标系=>百度坐标系
    fun wgs2BD09(wgLat: Double, wglng: Double): DoubleArray {
        val latlng = wgs2GCJ(wgLat, wglng)
        return gcj2BD09(latlng[0], latlng[1])
    }

    // WGS84=》GCJ02   地球坐标系=>火星坐标系
    fun wgs2GCJ(wgLat: Double, wglng: Double): DoubleArray {
        val latlng = DoubleArray(2)
        if (outOfChina(wgLat, wglng)) {
            latlng[0] = wgLat
            latlng[1] = wglng
            return latlng
        }
        val deltaD = delta(wgLat, wglng)
        latlng[0] = wgLat + deltaD[0]
        latlng[1] = wglng + deltaD[1]
        return latlng
    }

    // GCJ02=>WGS84   火星坐标系=>地球坐标系(粗略)
    fun gcj2WGS(glat: Double, glng: Double): DoubleArray {
        val latlng = DoubleArray(2)
        if (outOfChina(glat, glng)) {
            latlng[0] = glat
            latlng[1] = glng
            return latlng
        }
        val deltaD = delta(glat, glng)
        latlng[0] = glat - deltaD[0]
        latlng[1] = glng - deltaD[1]
        return latlng
    }

    // GCJ02=>WGS84   火星坐标系=>地球坐标系（精确）
    fun gcj2WGSExactly(gcjLat: Double, gcjlng: Double): DoubleArray {
        val initDelta = 0.01
        val threshold = 0.000000001
        var dLat = initDelta
        var dlng = initDelta
        var mLat = gcjLat - dLat
        var mlng = gcjlng - dlng
        var pLat = gcjLat + dLat
        var plng = gcjlng + dlng
        var wgsLat: Double
        var wgslng: Double
        var i = 0.0
        while (true) {
            wgsLat = (mLat + pLat) / 2
            wgslng = (mlng + plng) / 2
            val tmp = wgs2GCJ(wgsLat, wgslng)
            dLat = tmp[0] - gcjLat
            dlng = tmp[1] - gcjlng
            if (abs(dLat) < threshold && abs(dlng) < threshold) break
            if (dLat > 0) pLat = wgsLat else mLat = wgsLat
            if (dlng > 0) plng = wgslng else mlng = wgslng
            if (++i > 10_000) break
        }
        val latlng = DoubleArray(2)
        latlng[0] = wgsLat
        latlng[1] = wgslng
        return latlng
    }

    // 两点距离
    fun distance(latA: Double, logA: Double, latB: Double, logB: Double): Double {
        val earthR = 6_371_000
        val x =
            (cos(Math.toRadians(latA)) *
                cos(Math.toRadians(latB)) *
                cos(Math.toRadians(logA - logB)))
        val y = sin(Math.toRadians(latA)) * sin(Math.toRadians(latB))
        var s = x + y
        if (s > 1) s = 1.0
        if (s < -1) s = -1.0
        val alpha = acos(s)
        return alpha * earthR
    }

    fun delta(wgLat: Double, wglng: Double): DoubleArray {
        val latlng = DoubleArray(2)
        var dLat = transformLat(wglng - 105.0, wgLat - 35.0)
        var dlng = transformlng(wglng - 105.0, wgLat - 35.0)
        val radLat = Math.toRadians(wgLat)
        var magic = sin(radLat)
        magic = 1 - OFFSET * magic * magic
        val sqrtMagic = sqrt(magic)
        dLat = dLat * 180.0 / (AXIS * (1 - OFFSET) / (magic * sqrtMagic) * PI)
        dlng = dlng * 180.0 / (AXIS / sqrtMagic * cos(radLat) * PI)
        latlng[0] = dLat
        latlng[1] = dlng
        return latlng
    }

    fun outOfChina(lat: Double, lng: Double): Boolean {
        if (lng < 72.004 || lng > 137.8347) return true
        return lat < 0.8293 || lat > 55.8271
    }

    fun transformLat(x: Double, y: Double): Double {
        var ret =
            (-100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * Math.sqrt(Math.abs(x)))
        ret += (20.0 * sin(6.0 * x * PI) + 20.0 * sin(2.0 * x * PI)) * 2.0 / 3.0
        ret += (20.0 * sin(y * PI) + 40.0 * sin(y / 3.0 * PI)) * 2.0 / 3.0
        ret += (160.0 * sin(y / 12.0 * PI) + 320 * sin(y * PI / 30.0)) * 2.0 / 3.0
        return ret
    }

    fun transformlng(x: Double, y: Double): Double {
        var ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * Math.sqrt(Math.abs(x))
        ret += (20.0 * sin(6.0 * x * PI) + 20.0 * sin(2.0 * x * PI)) * 2.0 / 3.0
        ret += (20.0 * sin(x * PI) + 40.0 * sin(x / 3.0 * PI)) * 2.0 / 3.0
        ret += (150.0 * sin(x / 12.0 * PI) + 300.0 * sin(x / 30.0 * PI)) * 2.0 / 3.0
        return ret
    }

    /**
     * 小数转坐标
     *
     * @param location
     * @return
     */
    fun toDegree(location: Double): String {
        val degree = location.toInt()
        val decimal = location - degree
        val min = (decimal * 60).toInt()
        val sec = (decimal * 60 - min) * 60
        return String.format("%s°%d′%.2f″", degree, min, sec)
    }

    /**
     * 角度转小数
     *
     * @param location
     * @return
     */
    fun toDecimal(location: String?): Double {
        val matcher = PATTERN_DEGREE_LOCATION.matcher(location)
        var degree = 0.0
        if (matcher.matches()) {
            for (i in 0 until matcher.groupCount()) {
                degree += matcher.group(i + 1).toDouble() * 60.0.pow(-i.toDouble())
            }
        }
        return degree
    }

    /** 经纬度转地心 */
    fun lbh2xyz(l: Double, b: Double, h: Double, type: String = "wgs"): DoubleArray {
        val lRadian = Math.toRadians(l)
        val bRadian = Math.toRadians(b)

        val r = requireNotNull(map[type]!!["a"])
        val e2 = requireNotNull(map[type]!!["e1Square"])
        val tmp = 1.0 - e2

        val n = r / sqrt(1 - tmp * sin(bRadian).pow(2))
        val x = (n + h) * cos(bRadian) * cos(lRadian)
        val y = (n + h) * cos(bRadian) * sin(lRadian)
        val z = (n * tmp + h) * sin(bRadian)
        return doubleArrayOf(x, y, z)
    }

    fun xyz2lbh(x: Double, y: Double, z: Double, type: String = "wgs"): DoubleArray {
        val l = Math.toDegrees(atan(y / x))
        requireNotNull(map[type])
        val r = requireNotNull(map[type]!!["a"])
        val e2 = requireNotNull(map[type]!!["e1Square"])
        val tmp = 1.0 - e2

        var tB = 0.0
        var n = r / sqrt(1 - tmp * sin(tB).pow(2))
        var b = atan((z + n * e2 * sin(tB)) / sqrt(x.pow(2) + y.pow(2)))
        while (b - tB > 0.00000001) {
            tB = b
            n = r / sqrt(1 - tmp * sin(tB).pow(2))
            b = atan((z + n * e2 * sin(tB)) / sqrt(x.pow(2) + y.pow(2)))
        }

        val h = z / sin(b) - n * tmp
        b = Math.toDegrees(b)

        return doubleArrayOf(l, b, h)
    }
}
