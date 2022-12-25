package me.leon

import java.io.File
import java.util.Locale
import java.util.Properties
import javafx.scene.image.Image
import me.leon.ext.fx.Prefs
import me.leon.view.Home
import tornadofx.*

class ToolsApp : App(Home::class, Styles::class) {
    init {
        addStageIcon(Image(resources.stream(IMG_ICON)))
    }

    companion object {
        private val properties: Properties = Properties()
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
            if (!file.exists()) {
                javaClass.getResourceAsStream("/ToolsFx.properties")?.use {
                    it.copyTo(file.outputStream())
                }
            }
            properties.load(file.inputStream())
            file = File(DICT_DIR, "top1000.txt")

            file.parentFile.mkdirs()

            javaClass.getResourceAsStream("/top1000.txt")?.use { it.copyTo(file.outputStream()) }
        }
    }
}
