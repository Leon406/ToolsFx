package me.leon.view

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import javafx.scene.image.Image
import javafx.scene.input.DragEvent
import me.leon.base.base64Decode
import me.leon.controller.SignatureController
import me.leon.ext.*
import tornadofx.*

class SignatureView : View("签名与验签") {
    private val controller: SignatureController by inject()
    override val closeable = SimpleBooleanProperty(false)
    private lateinit var taKey: TextArea
    private lateinit var taRaw: TextArea
    private lateinit var infoLabel: Label
    lateinit var taSigned: TextArea
    private val key: String
        get() = taKey.text
    private val msg: String
        get() = taRaw.text
    private val signText: String
        get() = taSigned.text
    var keyPairAlg = "RSA"

    private val eventHandler =fileDraggedHandler {
        taKey.text = it.first().readText()
    }

    // https://www.bouncycastle.org/specifications.html
    private val keyPairAlgs =
        linkedMapOf(
            "RSA" to
                listOf(
                    "MD2withRSA",
                    "MD5withRSA",
                    "SHA1withRSA",
                    "RIPEMD128withRSA",
                    "RIPEMD160withRSA",
                    "RIPEMD256withRSA",
                    "SHA224withRSA",
                    "SHA256withRSA",
                    "SHA384withRSA",
                    "SHA512withRSA",
                    "SHA512(224)withRSA",
                    "SHA512(256)withRSA",
                    "SHA3-224withRSA",
                    "SHA3-256withRSA",
                    "SHA3-384withRSA",
                    "SHA3-512withRSA",
                    "SHA1withRSAandMGF1",
                    "SHA256withRSAandMGF1",
                    "SHA384withRSAandMGF1",
                    "SHA512withRSAandMGF1",
                    "SHA512(224)withRSAandMGF1",
                    "SHA512(256)withRSAandMGF1",
                    "SHA1withRSA/ISO9796-2",
                    "RIPEMD160withRSA/ISO9796-2",
                    "SHA1withRSA/X9.31",
                    "SHA224withRSA/X9.31",
                    "SHA256withRSA/X9.31",
                    "SHA384withRSA/X9.31",
                    "SHA512withRSA/X9.31",
                    "SHA512(224)withRSA/X9.31",
                    "SHA512(256)withRSA/X9.31",
                    "RIPEMD128withRSA/X9.31",
                    "RIPEMD160withRSA/X9.31",
                    "WHIRLPOOLwithRSA/X9.31"
                ),
            "DSA" to
                listOf(
                    "NONEwithDSA",
                    "RIPEMD160withDSA",
                    "SHA1withDSA",
                    "SHA224withDSA",
                    "SHA256withDSA",
                    "SHA384withDSA",
                    "SHA512withDSA",
                    "SHA3-224withDSA",
                    "SHA3-256withDSA",
                    "SHA3-384withDSA",
                    "SHA3-512withDSA"
                ),
            "ECDSA" to
                listOf(
                    "RIPEMD160withECDSA",
                    "SHA1withECDSA",
                    "NONEwithECDSA",
                    "SHA224withECDSA",
                    "SHA256withECDSA",
                    "SHA384withECDSA",
                    "SHA512withECDSA",
                    "SHA3-224withECDSA",
                    "SHA3-256withECDSA",
                    "SHA3-384withECDSA",
                    "SHA3-512withECDSA",
                    "SHAKE128withECDSA",
                    "SHAKE256withECDSA"
                ),
            "SM2" to listOf("SHA256withSM2", "SM3withSM2"),
            "Ed448" to listOf("Ed448"),
            "Ed25519" to listOf("Ed25519"),
            "EC" to
                listOf(
                    "SHA1withECNR",
                    "SHA224withECNR",
                    "SHA256withECNR",
                    "SHA384withECNR",
                    "SHA512withECNR"
                )
        )

    private val selectedKeyPairAlg = SimpleStringProperty(keyPairAlgs.keys.first())
    private val selectedSigAlg = SimpleStringProperty(keyPairAlgs.values.first().first())
    private lateinit var cbSigs: ComboBox<String>
    private val info
        get() = "Signature: $keyPairAlg hash: ${selectedSigAlg.get()} "

    override val root = vbox {
        paddingAll = DEFAULT_SPACING
        spacing = DEFAULT_SPACING
        hbox {
            label("密钥:")
            button("剪贴板导入") { action { taKey.text = clipboardText() } }
        }
        taKey =
            textarea {
                promptText = "请输入密钥或者拖动文件到此区域"
                isWrapText = true
                onDragEntered = eventHandler
            }
        hbox {
            label("原始内容:")
            button("剪贴板导入") { action { taRaw.text = clipboardText() } }
        }
        taRaw =
            textarea {
                promptText = "请输入或者拖动文件到此区域"
                isWrapText = true
                onDragEntered = eventHandler
            }
        hbox {
            alignment = Pos.CENTER_LEFT
            label("公私钥算法:  ")
            combobox(selectedKeyPairAlg, keyPairAlgs.keys.toMutableList()) {
                cellFormat { text = it }
            }
            label("签名算法:  ")
            cbSigs =
                combobox(selectedSigAlg, keyPairAlgs.values.first()) { cellFormat { text = it } }
        }

        selectedKeyPairAlg.addListener { _, _, newValue ->
            newValue?.run {
                cbSigs.items = keyPairAlgs[newValue]!!.asObservable()
                selectedSigAlg.set(keyPairAlgs[newValue]!!.first())
                cbSigs.isDisable = keyPairAlgs[newValue]!!.size == 1
            }
        }

        selectedSigAlg.addListener { _, _, newValue ->
            println("selectedSigAlg __ $newValue")
            newValue?.run {
                println("算法 ${selectedKeyPairAlg.get()}")
                if (key.isNotEmpty() && msg.isNotEmpty()) {
                    sign()
                }
            }
        }
        tilepane {
            alignment = Pos.CENTER
            paddingTop = DEFAULT_SPACING
            hgap = DEFAULT_SPACING_4X
            button("私钥签名") {
                action { sign() }
                setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE)
            }
            button("公钥验签") {
                action { verify() }
                setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE)
            }
        }
        hbox {
            label("签名 (base64):")
            button(graphic = imageview(Image("/copy.png"))) { action { signText.copy() } }
        }

        taSigned =
            textarea {
                promptText = "结果"
                isWrapText = true
            }
        infoLabel = label()
    }

    private fun sign() =
        runAsync { controller.sign(selectedKeyPairAlg.get(), selectedSigAlg.get(), key, msg) } ui
            {
                taSigned.text = it
                infoLabel.text = info
            }
    private fun verify() =
        runAsync {
            controller.verify(
                selectedKeyPairAlg.get(),
                selectedSigAlg.get(),
                key,
                msg,
                signText.base64Decode()
            )
        } ui
            { state ->
                primaryStage.showToast("验签成功".takeIf { state } ?: "验签失败")
                infoLabel.text = info
            }
}
