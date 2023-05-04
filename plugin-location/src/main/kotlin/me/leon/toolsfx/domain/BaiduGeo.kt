package me.leon.toolsfx.domain

data class BaiduGeo(val status: Int?, val result: Result?) {
    data class Result(
        val location: Location?,
        val precise: Int?,
        val confidence: Int?,
        val comprehension: Int?,
        val level: String?
    )

    data class Location(val lng: Double?, val lat: Double?)

    fun geoInfo(): String {
        return "location：${result?.location?.lng},${result?.location?.lat} precise:${result?.precise} " +
            "confidence:${result?.precise} comprehension:${result?.comprehension} level:${result?.level}"
                .also { println(this) }
    }

    fun geos(): String {
        return "${result?.location?.lng},${result?.location?.lat}"
    }
}
