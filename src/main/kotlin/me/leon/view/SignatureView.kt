package me.leon.view

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.control.*
import me.leon.controller.SignatureController
import me.leon.encode.base.base64Decode
import me.leon.ext.*
import tornadofx.*
import tornadofx.FX.Companion.messages

class SignatureView : View(messages["signVerify"]) {
    private val controller: SignatureController by inject()
    override val closeable = SimpleBooleanProperty(false)
    private lateinit var taKey: TextArea
    private lateinit var taRaw: TextArea
    private lateinit var labelInfo: Label
    private lateinit var taSigned: TextArea
    private val key: String
        get() = taKey.text
    private val msg: String
        get() = taRaw.text
    private val signText: String
        get() = taSigned.text
    private var keyPairAlg = "RSA"

    private val eventHandler = fileDraggedHandler { taKey.text = it.first().readText() }

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

    private val centerNode = vbox {
        paddingAll = DEFAULT_SPACING
        spacing = DEFAULT_SPACING
        hbox {
            label(messages["key"])
            button(graphic = imageview("/img/import.png")) {
                action { taKey.text = clipboardText() }
            }
        }
        taKey =
            textarea {
                promptText = messages["inputHint"]
                isWrapText = true
                onDragEntered = eventHandler
            }
        hbox {
            label(messages["plain"])
            button(graphic = imageview("/img/import.png")) {
                action { taRaw.text = clipboardText() }
            }
        }
        taRaw =
            textarea {
                promptText = messages["inputHint"]
                isWrapText = true
                onDragEntered = eventHandler
                prefHeight = DEFAULT_SPACING_16X
            }
        hbox {
            alignment = Pos.CENTER_LEFT
            label(messages["publicAlg"])
            combobox(selectedKeyPairAlg, keyPairAlgs.keys.toMutableList()) {
                cellFormat { text = it }
            }
            label(messages["sigAlg"])
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
            button(messages["priSig"]) {
                action { sign() }
                setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE)
            }
            button(messages["pubVerify"]) {
                action { verify() }
                setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE)
            }
        }
        hbox {
            label(messages["sig"])
            button(graphic = imageview("/img/copy.png")) { action { signText.copy() } }
        }

        taSigned =
            textarea {
                promptText = messages["outputHint"]
                isWrapText = true
                prefHeight = DEFAULT_SPACING_10X
            }
    }
    override val root = borderpane {
        center = centerNode
        bottom = hbox { labelInfo = label(info) }
    }

    private fun sign() =
        runAsync { controller.sign(selectedKeyPairAlg.get(), selectedSigAlg.get(), key, msg) } ui
            {
                taSigned.text = it
                labelInfo.text = info
                if (Prefs.autoCopy) it.copy().also { primaryStage.showToast(messages["copied"]) }
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
                labelInfo.text = info
            }
}
