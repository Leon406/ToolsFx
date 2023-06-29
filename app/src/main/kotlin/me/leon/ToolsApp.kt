package me.leon

import java.io.File
import java.util.Locale
import java.util.Properties
import javafx.scene.image.Image
import me.leon.ext.fromJson
import me.leon.ext.fx.Prefs
import me.leon.view.Home
import tornadofx.*

class ToolsApp : App(Home::class, Styles::class) {
    init {
        addStageIcon(Image(resources.stream(IMG_ICON)))
    }

    companion object {
        private val properties: Properties = Properties()
        var vocabulary: MutableMap<String, String> = mutableMapOf()
        /** online translation dict */
        lateinit var dict: DictionaryConfig.Dict

        init {
            // for text i18n
            FX.locale = if (Prefs.language == "zh") Locale.CHINESE else Locale.ENGLISH
            initConfig()
        }

        val isEnableClassical: Boolean
            get() = properties["isEnableClassical"].toString().toBoolean()

        val isEnablePBE: Boolean
            get() = properties["isEnablePBE"].toString().toBoolean()

        val isEnableInternalWebview: Boolean
            get() = properties["isEnableInternalWebview"].toString().toBoolean()

        val isEnableBigInt: Boolean
            get() = properties["isEnableBigInt"].toString().toBoolean()

        val extUrls: List<String>
            get() = properties["extUrls"].toString().split(",")

        val isEnableSignature: Boolean
            get() = properties["isEnableSignature"].toString().toBoolean()

        val isEnableMac: Boolean
            get() = properties["isEnableMac"].toString().toBoolean()

        val isEnableSymmetricStream: Boolean
            get() = properties["isEnableSymmetricStream"].toString().toBoolean()

        val isEnableQrcode: Boolean
            get() = properties["isEnableQrcode"].toString().toBoolean()

        val offlineMode: Boolean
            get() = (properties["offlineMode"] ?: "false").toString().toBoolean()

        val scale: String
            get() = (properties["uiScale"] ?: "-1").toString()

        private fun initConfig() {

            var file = File(APP_ROOT, "ToolsFx.properties")

            copyResourceFileIfNotExist("/ToolsFx.properties", file)
            properties.load(file.inputStream())

            file = File(DICT_DIR, "top1000.txt")
            copyResourceFileIfNotExist("/top1000.txt", file)

            file = File(DICT_DIR, "dictionary.conf")
            copyResourceFileIfNotExist("/dictionary.conf", file)
            file.readText().fromJson(DictionaryConfig::class.java).run {
                dict = dicts[active]
                dict.autoPronounce = autoPronounce
                File(DICT_DIR, dictFile).run {
                    println("dict $this")
                    if (exists()) {
                        readText()
                            .lines()
                            .filter { it.isNotEmpty() }
                            .forEach {
                                val (word, mean) = it.split("\t")

                                vocabulary[word] = mean
                            }
                        println("vocabulary ${vocabulary.size}")
                    }
                }
            }
            println(dict)
        }

        private fun copyResourceFileIfNotExist(fileName: String, targetFile: File) {
            if (targetFile.exists()) {
                return
            }
            targetFile.parentFile.mkdirs()
            ToolsApp::class.java.getResourceAsStream(fileName)?.use {
                it.copyTo(targetFile.outputStream())
            }
        }
    }
}
