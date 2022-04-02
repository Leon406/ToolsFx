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
        // for text i18n
        FX.locale = if (Prefs.language == "zh") Locale.CHINESE else Locale.ENGLISH
        addStageIcon(Image(resources.stream("/img/tb.png")))
        initConfig()
    }

    private fun initConfig() {

        val file = File(APP_ROOT, "ToolsFx.properties")
        if (!file.exists())
            javaClass.getResourceAsStream("/ToolsFx.properties").use {
                it.copyTo(file.outputStream())
            }
        properties.load(file.inputStream())
    }

    companion object {
        var releaseInfo: ReleaseInfo? = null
        val properties: Properties = Properties()
        val isEnableClassical: Boolean
            get() = properties["isEnableClassical"].toString().toBoolean()
        val isEnablePBE: Boolean
            get() = properties["isEnablePBE"].toString().toBoolean()
        val isEnableInternalWebview: Boolean
            get() = properties["isEnableInternalWebview"].toString().toBoolean()
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
    }
}
