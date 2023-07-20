package me.leon.misc.zhconvert

/**
 * @author Leon
 * @since 2023-07-18 11:23
 * @email deadogone@gmail.com
 */

/** online https://opencc.byvoid.com/#main-tabs-conversion */
data class ZhConvertConfig(val name: String, val desc: String, val dicts: List<List<String>>)

val CONFIG =
    arrayOf(
        // 繁体转换 (4个)
        ZhConvertConfig(
            "t2s",
            "Traditional Chinese to Simplified Chinese",
            listOf(listOf("TSPhrases", "TSCharacters"))
        ),
        ZhConvertConfig(
            "t2tw",
            "Traditional Chinese to Traditional Chinese (Taiwan standard)",
            listOf(listOf("TWVariants"))
        ),
        ZhConvertConfig(
            "t2hk",
            "Traditional Chinese to Traditional Chinese (Hong Kong variant)",
            listOf(listOf("HKVariants"))
        ),
        ZhConvertConfig(
            "t2jp",
            "Traditional Chinese Characters (Kyūjitai) to New Japanese Kanji (Shinjitai)",
            listOf(listOf("JPVariants"))
        ),
        // 简体转换 (4个)
        ZhConvertConfig(
            "s2t",
            "Traditional Chinese to Simplified Chinese",
            listOf(listOf("STPhrases", "STCharacters"))
        ),
        ZhConvertConfig(
            "s2tw",
            "Traditional Chinese to Traditional Chinese (Taiwan standard)",
            listOf(listOf("STPhrases", "STCharacters"), listOf("TWVariants"))
        ),
        ZhConvertConfig(
            "s2hk",
            "Traditional Chinese to Traditional Chinese (Hong Kong variant)",
            listOf(listOf("STPhrases", "STCharacters"), listOf("HKVariants"))
        ),
        ZhConvertConfig(
            "s2twp",
            "Simplified Chinese to Traditional Chinese (Taiwan standard, with phrases)",
            listOf(listOf("STPhrases", "STCharacters"), listOf("TWPhrases", "TWVariants"))
        ),

        //  繁体(港)
        ZhConvertConfig(
            "hk2s",
            "Traditional Chinese (Hong Kong variant) to Simplified Chinese",
            listOf(
                listOf("HKVariantsRevPhrases", "HKVariantsRev"),
                listOf("TSPhrases", "TSCharacters")
            )
        ),
        ZhConvertConfig(
            "hk2t",
            "Traditional Chinese (Hong Kong variant) to Traditional Chinese",
            listOf(listOf("HKVariantsRevPhrases", "HKVariantsRev"))
        ),
        //  繁体(日)
        ZhConvertConfig(
            "jp2t",
            "New Japanese Kanji (Shinjitai) to Traditional Chinese Characters (Kyūjitai)",
            listOf(listOf("JPShinjitaiPhrases", "JPShinjitaiCharacters", "JPVariantsRev"))
        ),
        //  繁体(台)
        ZhConvertConfig(
            "tw2s",
            "Traditional Chinese (Taiwan standard) to Simplified Chinese",
            listOf(
                listOf("TWVariantsRevPhrases", "TWVariantsRev"),
                listOf("TSPhrases", "TSCharacters")
            )
        ),
        ZhConvertConfig(
            "tw2sp",
            "Traditional Chinese (Taiwan standard) to Simplified Chinese (with phrases)",
            listOf(
                listOf("TWPhrasesRev", "TWVariantsRevPhrases", "TWVariantsRev"),
                listOf("TSPhrases", "TSCharacters")
            )
        ),
        ZhConvertConfig(
            "tw2t",
            "Traditional Chinese (Taiwan standard) to Traditional Chinese",
            listOf(listOf("TWVariantsRevPhrases", "TWVariantsRev"))
        ),
    )

fun ZhConvertConfig.convert(text: String): String {
    val translators =
        dicts.map { dicts ->
            val dict = dicts.first()
            val reverse = dict.endsWith("Rev")
            CACHE_TRANSLATOR[dicts.toString()]
                ?: ZhTranslator("/zhconvert/${dict.replace("Rev$".toRegex(), "")}.txt", reverse)
                    .also { translator ->
                        dicts.drop(1).forEach {
                            translator.loadDict(
                                "/zhconvert/${it.replace("Rev$".toRegex(), "")}.txt",
                                it.endsWith("Rev")
                            )
                        }
                        CACHE_TRANSLATOR[dicts.toString()] = translator
                    }
        }
    var converted = text
    for (translator in translators) {
        converted = translator.convert(converted)
    }
    return converted
}
