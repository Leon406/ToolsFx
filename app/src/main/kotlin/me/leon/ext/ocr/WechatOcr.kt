package me.leon.ext.ocr

import com.sun.jna.*
import java.io.File
import java.util.Collections.singletonMap
import java.util.concurrent.atomic.AtomicReference
import me.leon.config.WECHAT_OCR_DIR

object WechatOcr {
    private var pattern: java.util.regex.Pattern =
        java.util.regex.Pattern.compile("\"text\":\"(.*?)\"}")
    private var exe: String? = null
    private val wechatDir = AtomicReference<String?>()
    private val tmpDir = System.getenv("TEMP")
    private val tmpOcrFile = File(tmpDir, "toolsfxOcrTmp.jpg")

    init {
        var wechatRoot = System.getenv("PROGRAMFILES(X86)") + "\\Tencent\\WeChat"
        val e1 = File(wechatRoot).exists()
        if (!e1) {
            wechatRoot = wechatRoot.replace("C:", "D:")
        }
        val wechat = File(wechatRoot)
        if (wechat.exists()) {
            println("==> found WeChat")
            java.util.Arrays.stream(wechat.listFiles())
                .filter { file: File -> file.isDirectory && file.name.endsWith("]") }
                .findFirst()
                .ifPresent { file: File ->
                    wechatDir.set(file.absolutePath)
                    println("==> found dll " + file.absolutePath)
                }
            val localWechatOcr =
                System.getenv("APPDATA") +
                    "\\Tencent\\WeChat\\XPlugin\\Plugins\\WeChatOCR\\7079\\extracted\\WeChatOCR.exe"
            val exists = File(localWechatOcr).exists()
            if (exists) {
                exe = localWechatOcr
                println("==> found WeChatOCR.exe")
            }
        }
        if (exe == null) {
            exe = File(WECHAT_OCR_DIR, "WeChatOCR.exe").absolutePath
        }

        if (wechatDir.get() == null) {
            val x = File(WECHAT_OCR_DIR, "dll").absolutePath
            wechatDir.set(x)
        }
    }

    fun ocr(bytes: ByteArray): String {
        tmpOcrFile.writeBytes(bytes)
        return ocr(tmpOcrFile.absolutePath)
    }

    fun ocr(imageFile: String): String {
        var result = ""
        val sb = StringBuilder()
        WechatOCR.dll.wechat_ocr(WString(exe), WString(wechatDir.get()), imageFile) {
            newValue: String? ->
            result = newValue.orEmpty()
        }
        println("================= Result =================")
        if (result.isNotEmpty()) {
            try {
                val matcher = pattern.matcher(result)
                while (matcher.find()) {
                    sb.append(matcher.group(1)).appendLine()
                    println(matcher.group(1))
                }
            } catch (ignored: Exception) {
                println("================= Error =================")
                println(result)
                error(ignored.stackTraceToString())
            }
        } else {
            println("================= Error ===============")
        }
        return sb.toString()
    }

    interface WechatOCR : Library {
        fun interface SetResCallback : Callback {
            fun callback(arg: String)
        }

        @Suppress("All")
        fun wechat_ocr(
            ocr_exe: WString,
            wechat_dir: WString,
            imgfn: String,
            res: SetResCallback
        ): Boolean

        companion object {
            val dll: WechatOCR =
                Native.load(
                    "wcocr",
                    WechatOCR::class.java,
                    singletonMap(Library.OPTION_STRING_ENCODING, "UTF-8")
                )
        }
    }
}
