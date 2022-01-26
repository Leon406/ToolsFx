package me.leon.plugin

data class AmapSearchBean(
    val suggestion: Suggestion?,
    val count: String?,
    val infocode: String?,
    val pois: List<Poi>?,
    val status: String?,
    val info: String?
) {
    data class Suggestion(val keywords: List<Any>?, val cities: List<Any>?)

    data class Poi(
        val distance: Any?,
        val type: Any?,
        val typecode: Any?,
        val citycode: String?,
        val adname: Any?,
        val alias: Any?,
        val address: Any?,
        val pname: Any?,
        val cityname: Any?,
        val name: String?,
        val location: String?,
    ) {
        val properLocation
            get() = address ?: alias ?: name

        override fun toString(): String {
            return "Poi: name=$name address=$address location=$location"
        }
    }
}
