package me.leon.misc.net

import me.leon.ext.stacktrace

/**
 * @author Leon
 * @since 2023-02-23 10:10
 * @email deadogone@gmail.com
 */
data class MiitDetail(val code: Int, val msg: String, val params: Params, val success: Boolean) {
    data class Params(
        val endRow: Int,
        val firstPage: Int,
        val hasNextPage: Boolean,
        val hasPreviousPage: Boolean,
        val isFirstPage: Boolean,
        val isLastPage: Boolean,
        val lastPage: Int,
        val list: List<Info>,
        val navigatePages: Int,
        val navigatepageNums: List<Int>,
        val nextPage: Int,
        val orderBy: String,
        val pageNum: Int,
        val pageSize: Int,
        val pages: Int,
        val prePage: Int,
        val size: Int,
        val startRow: Int,
        val total: Int
    )

    data class Info(
        val contentTypeName: String,
        val domain: String,
        val domainId: Long,
        val leaderName: String,
        val limitAccess: String,
        val mainId: Long,
        val mainLicence: String,
        val natureName: String,
        val serviceId: Long,
        val serviceLicence: String,
        val unitName: String,
        val updateRecordTime: String
    )

    val showInfo: String
        get() =
            runCatching {
                    if (params.list.isEmpty()) {
                        "未查到备案信息"
                    } else {
                        with(params.list.first()) {
                            "主办单位:\t$unitName ($natureName)" +
                                "\n备案号:\t$serviceLicence" +
                                "\n审核日期:\t$updateRecordTime" +
                                "\n是否限制接入:\t$limitAccess"
                        }
                    }
                }
                .getOrElse { it.stacktrace() }
}
