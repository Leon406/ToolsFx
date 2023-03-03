package me.leon.misc.net

/**
 * @author Leon
 * @since 2023-02-27 16:58
 * @email deadogone@gmail.com
 */
@Suppress("ALL")
data class IpApi(
    val status: String,
    val country: String,
    val countryCode: String,
    val region: String,
    val regionName: String,
    val city: String,
    val zip: String,
    val lat: Double,
    val lon: Double,
    val timezone: String,
    val isp: String,
    val org: String,
    val `as`: String,
    val query: String
) {
    val info
        get() = "$query\n$country $regionName $city\n$lon,$lat\n$isp,$org\n$`as`"
}
