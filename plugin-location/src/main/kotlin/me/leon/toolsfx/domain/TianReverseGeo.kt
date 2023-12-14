package me.leon.toolsfx.domain

@Suppress("All")
data class TianReverseGeo(val result: Result, val msg: String, val status: String) {
    data class Result(
        val formatted_address: String,
        val location: TianGeo.Location,
        val addressComponent: AddressComponent
    ) {
        data class AddressComponent(
            val address: String,
            val city: String,
            val county_code: String,
            val nation: String,
            val poi_position: String,
            val county: String,
            val city_code: String,
            val address_position: String,
            val poi: String,
            val province_code: String,
            val province: String,
            val road: String,
            val road_distance: Int,
            val poi_distance: Int,
            val address_distance: Int
        )
    }

    fun formatLocation() = result.formatted_address
}
