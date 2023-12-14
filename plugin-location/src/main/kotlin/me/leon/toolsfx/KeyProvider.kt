package me.leon.toolsfx

/**
 * @author Leon
 * @since 2023-12-14 15:03
 * @email deadogone@gmail.com
 */
object KeyProvider {
    val KEY_BD by lazy {
        System.getenv("ToolsFx-Location-KEY-BD") ?: "V0AKhZ3wN8CTU3zx8lGf4QvwyOs5rGIn"
    }
    val KEY_AMAP by lazy {
        System.getenv("ToolsFx-Location-KEY-AMAP") ?: "d235e4dfc5d2de3c24ed8c7e359c8144"
    }
    val KEY_TIAN by lazy {
        System.getenv("ToolsFx-Location-KEY-TIAN") ?: "ae3dce0fce9dc7251d20becd31bb0717"
    }
}
