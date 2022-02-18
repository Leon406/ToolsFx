package me.leon.toolsfx.domain

import com.google.gson.annotations.SerializedName

data class AmapGeo(
    val status: String?,
    val info: String?,
    val infocode: String?,
    val count: String?,
    val geocodes: List<Geocode>?
) {
    data class Geocode(
        @SerializedName("formatted_address") val formattedAddress: String?,
        val country: String?,
        val province: String?,
        val citycode: String?,
        val city: String?,
        val district: Any?,
        val township: Any?,
        val adcode: String?,
        val street: Any?,
        val number: Any?,
        val location: String?,
        val level: String?
    )

    fun geoInfo(): String {
        return "location：${geocodes?.firstOrNull()?.location} " +
            "address: ${geocodes?.firstOrNull()?.formattedAddress} " +
            "lv: ${geocodes?.firstOrNull()?.level}"
    }

    fun geos(): String {
        return "${geocodes?.firstOrNull()?.location}"
    }
}
