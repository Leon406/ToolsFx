package me.leon.toolsfx.domain

data class TianGeo(
    val location: Location,
    val status: String,
    val msg: String,
    val searchVersion: String
) {
    data class Location(val lon: String, val level: String, val lat: String)

    fun geoInfo(): String {
        return "location：${location.lon},${location.lat} " + "level:${location.level}"
    }
}
