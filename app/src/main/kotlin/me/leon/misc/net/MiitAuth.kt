package me.leon.misc.net

/**
 * @author Leon
 * @since 2023-02-23 9:46
 * @email deadogone@gmail.com
 */
data class MiitAuth(val code: Int, val msg: String, val params: Params, val success: Boolean) {
    data class Params(val bussiness: String, val expire: Int, val refresh: String)
}
