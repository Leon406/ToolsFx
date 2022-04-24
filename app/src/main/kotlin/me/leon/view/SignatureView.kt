package me.leon.view

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.control.*
import me.leon.Styles
import me.leon.controller.SignatureController
import me.leon.encode.base.base64
import me.leon.ext.*
import me.leon.ext.crypto.parsePublicKeyFromCerFile
import me.leon.ext.fx.*
import tornadofx.*
import tornadofx.FX.Companion.messages

class SignatureView : Fragment(messages["signVerify"]) {
    private val controller: SignatureController by inject()
    override val closeable = SimpleBooleanProperty(false)
    private val isSingleLine = SimpleBooleanProperty(false)
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

    private val eventHandler = fileDraggedHandler {
        taKey.text =
            with(it.first()) {
                if (extension in listOf("pk8", "key", "der")) readBytes().base64()
                else if (extension in listOf("cer", "crt")) parsePublicKeyFromCerFile()
                else if (length() <= 10 * 1024 * 1024)
                    if (realExtension() in unsupportedExts) "unsupported file extension"
                    else readText()
                else "not support file larger than 10M"
            }
    }
    private val inputEventHandler = fileDraggedHandler {
        taRaw.text =
            with(it.first()) {
                if (length() <= 128 * 1024)
                    if (realExtension() in unsupportedExts) "unsupported file extension"
                    else readText()
                else "not support file larger than 128KB"
            }
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
            "RSASSA-PSS" to
                listOf(
                    "RAWRSASSA-PSS",
                    "SHA256withRSA/PSS",
                    "SHAKE128WITHRSAPSS",
                    "SHAKE256WITHRSAPSS"
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
    private var timeConsumption = 0L
    private var startTime = 0L
    private val info
        get() =
            "Signature: ${selectedKeyPairAlg.get()} hash: ${selectedSigAlg.get()} " +
                "${messages["inputLength"]}: ${msg.length}  " +
                "${messages["outputLength"]}: ${signText.length}  " +
                "cost: $timeConsumption ms"
    private var inputEncode = "raw"
    private var outputEncode = "base64"
    private lateinit var tgInput: ToggleGroup
    private lateinit var tgOutput: ToggleGroup
    private val centerNode = vbox {
        addClass(Styles.group)
        hbox {
            label(messages["plain"])
            addClass(Styles.left)
            tgInput =
                togglegroup {
                    radiobutton("raw") { isSelected = true }
                    radiobutton("base64")
                    radiobutton("hex")
                    selectedToggleProperty().addListener { _, _, newValue ->
                        inputEncode = newValue.cast<RadioButton>().text
                    }
                }

            button(graphic = imageview("/img/import.png")) {
                action { taRaw.text = clipboardText() }
            }
        }
        taRaw =
            textarea {
                promptText = messages["inputHint"]
                isWrapText = true
                onDragEntered = inputEventHandler
                prefHeight = DEFAULT_SPACING_16X
            }
        hbox {
            label(messages["key"])
            button(graphic = imageview("/img/import.png")) {
                action { taKey.text = clipboardText() }
            }
        }
        taKey =
            textarea {
                promptText = messages["inputHintAsy"]
                isWrapText = true
                onDragEntered = eventHandler
            }

        hbox {
            addClass(Styles.left)
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
                timeConsumption = 0
                labelInfo.text = info
            }
        }

        selectedSigAlg.addListener { _, _, newValue ->
            println("selectedSigAlg __ $newValue")
            timeConsumption = 0
            labelInfo.text = info
            newValue?.run { println("算法 ${selectedKeyPairAlg.get()}") }
        }
        tilepane {
            alignment = Pos.CENTER
            paddingTop = DEFAULT_SPACING
            hgap = DEFAULT_SPACING_4X
            checkbox(messages["singleLine"], isSingleLine)
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
            addClass(Styles.left)
            label(messages["sig"])
            tgOutput =
                togglegroup {
                    radiobutton("base64") { isSelected = true }
                    radiobutton("hex")
                    selectedToggleProperty().addListener { _, _, newValue ->
                        outputEncode = newValue.cast<RadioButton>().text
                    }
                }

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
        runAsync {
            startTime = System.currentTimeMillis()
            controller.sign(
                selectedKeyPairAlg.get(),
                selectedSigAlg.get(),
                key,
                msg,
                inputEncode,
                outputEncode,
                isSingleLine.get()
            )
        } ui
            {
                taSigned.text = it
                if (Prefs.autoCopy) it.copy().also { primaryStage.showToast(messages["copied"]) }
                timeConsumption = System.currentTimeMillis() - startTime
                labelInfo.text = info
            }

    private fun verify() =
        runAsync {
            startTime = System.currentTimeMillis()
            controller.verify(
                selectedKeyPairAlg.get(),
                selectedSigAlg.get(),
                key,
                msg,
                inputEncode,
                outputEncode,
                signText,
                isSingleLine.get()
            )
        } ui
            { state ->
                primaryStage.showToast("result: \n$state")
                timeConsumption = System.currentTimeMillis() - startTime
                labelInfo.text = info
            }
}
