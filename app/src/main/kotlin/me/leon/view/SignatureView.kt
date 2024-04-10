package me.leon.view

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.layout.Priority
import me.leon.*
import me.leon.controller.SignatureController
import me.leon.domain.SimpleMsgEvent
import me.leon.encode.base.base64
import me.leon.ext.*
import me.leon.ext.crypto.parsePublicKeyFromCerFile
import me.leon.ext.fx.*
import tornadofx.*
import tornadofx.FX.Companion.messages

class SignatureView : Fragment(messages["signVerify"]) {
    private val controller: SignatureController by inject()

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
                ),
            "JWT" to
                listOf(
                    "HS256",
                    "HS384",
                    "HS512",
                    "RS256",
                    "RS384",
                    "RS512",
                    "ES256",
                    "ES384",
                    "ES512",
                    "PS256",
                    "PS384",
                    "PS512"
                ),
        )
    private var timeConsumption = 0L
    private var startTime = 0L
    private var inputEncode = "raw"
    private var outputEncode = "base64"

    override val closeable = SimpleBooleanProperty(false)
    private val singleLine = SimpleBooleanProperty(false)
    private val showPrivateKey = SimpleBooleanProperty(true)
    private val selectedKeyPairAlg = SimpleStringProperty(keyPairAlgs.keys.first())
    private val selectedSigAlg = SimpleStringProperty(keyPairAlgs.values.first().first())

    private var taPubKey: TextArea by singleAssign()
    private var taPriKey: TextArea by singleAssign()
    private var taRaw: TextArea by singleAssign()
    private var labelInfo: Label by singleAssign()
    private var taSigned: TextArea by singleAssign()
    private var tgInput: ToggleGroup by singleAssign()
    private var tgOutput: ToggleGroup by singleAssign()
    private var cbSigs: ComboBox<String> by singleAssign()

    private val SAME_KEY_ALGS = arrayOf("HS256", "HS384", "HS512")
    private val msg: String
        get() = taRaw.text

    private val signText: String
        get() = taSigned.text

    private val eventHandler = fileDraggedHandler {
        taPubKey.text =
            with(it.first()) {
                when (extension) {
                    in listOf("pk8", "key", "der") -> {
                        val text = readText()
                        if (text.startsWith("-----BEGIN")) {
                            text
                        } else {
                            readBytes().base64()
                        }
                    }
                    in listOf("cer", "crt") -> {
                        parsePublicKeyFromCerFile()
                    }
                    else -> {
                        properText()
                    }
                }
            }
    }
    private val priKeyEventHandler = fileDraggedHandler {
        taPriKey.text =
            with(it.first()) {
                when (extension) {
                    in listOf("pk8", "key", "der") -> {
                        val text = readText()
                        if (text.startsWith("-----BEGIN")) {
                            text
                        } else {
                            readBytes().base64()
                        }
                    }
                    in listOf("cer", "crt") -> {
                        parsePublicKeyFromCerFile()
                    }
                    else -> {
                        properText()
                    }
                }
            }
    }
    private val inputEventHandler = fileDraggedHandler { taRaw.text = it.first().properText() }

    private val info
        get() =
            "Signature: ${selectedKeyPairAlg.get()} hash: ${selectedSigAlg.get()} " +
                "${messages["inputLength"]}: ${msg.length}  " +
                "${messages["outputLength"]}: ${signText.length}  " +
                "cost: $timeConsumption ms"

    private val centerNode = vbox {
        addClass(Styles.group)
        hbox {
            label(messages["plain"])
            addClass(Styles.left)
            tgInput = togglegroup {
                radiobutton("raw") { isSelected = true }
                radiobutton("base64")
                radiobutton("hex")
                selectedToggleProperty().addListener { _, _, newValue ->
                    inputEncode = newValue.cast<RadioButton>().text
                }
            }

            button(graphic = imageview(IMG_IMPORT)) {
                tooltip(messages["pasteFromClipboard"])
                action { taRaw.text = clipboardText() }
            }
        }
        taRaw = textarea {
            promptText = messages["inputHint"]
            isWrapText = true
            onDragEntered = inputEventHandler
            prefHeight = DEFAULT_SPACING_16X
        }
        hbox {
            label(messages["key"])
            button(graphic = imageview(IMG_IMPORT)) {
                tooltip(messages["pasteFromClipboard"])
                action { taPubKey.text = clipboardText() }
            }
        }

        hbox {
            spacing = DEFAULT_SPACING_3X
            taPubKey = textarea {
                promptText = messages["inputHintAsyPub"]
                isWrapText = true
                onDragEntered = eventHandler
            }
            taPriKey = textarea {
                promptText = messages["inputHintAsyPri"]
                isWrapText = true
                visibleWhen(showPrivateKey)
                onDragEntered = priKeyEventHandler
            }
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

            showPrivateKey.value = !SAME_KEY_ALGS.contains(newValue)
            timeConsumption = 0
            labelInfo.text = info
            newValue?.run { println("算法 ${selectedKeyPairAlg.get()}") }
        }
        tilepane {
            alignment = Pos.CENTER
            paddingTop = DEFAULT_SPACING
            hgap = DEFAULT_SPACING_4X
            checkbox(messages["singleLine"], singleLine)
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
            tgOutput = togglegroup {
                radiobutton("base64") { isSelected = true }
                radiobutton("hex")
                selectedToggleProperty().addListener { _, _, newValue ->
                    outputEncode = newValue.cast<RadioButton>().text
                }
            }

            button(graphic = imageview(IMG_COPY)) {
                tooltip(messages["copy"])
                action { signText.copy() }
            }
        }

        taSigned = textarea {
            vgrow = Priority.ALWAYS
            promptText = messages["outputHint"]
            isWrapText = true
        }
        subscribe<SimpleMsgEvent> {
            if (it.type == 1) {
                taPubKey.text = it.msg
            } else if (it.type == 2) {
                taPriKey.text = it.msg
            }
        }
    }
    override val root = borderpane {
        center = centerNode
        bottom = hbox { labelInfo = label(info) }
    }

    private fun sign() =
        runAsync {
            startTime = System.currentTimeMillis()
            runCatching {
                    controller.sign(
                        selectedKeyPairAlg.get(),
                        selectedSigAlg.get(),
                        if (SAME_KEY_ALGS.contains(selectedSigAlg.get())) {
                            taPubKey.text
                        } else {
                            taPriKey.text
                        },
                        msg,
                        inputEncode,
                        outputEncode,
                        singleLine.get()
                    )
                }
                .getOrElse { it.stacktrace() }
        } ui
            {
                taSigned.text = it
                if (Prefs.autoCopy) it.copy().also { primaryStage.showToast(messages["copied"]) }
                timeConsumption = System.currentTimeMillis() - startTime
                labelInfo.text = info
            }

    private fun verify() =
        runAsync {
            runCatching {
                    startTime = System.currentTimeMillis()
                    controller.verify(
                        selectedKeyPairAlg.get(),
                        selectedSigAlg.get(),
                        taPubKey.text,
                        msg,
                        inputEncode,
                        outputEncode,
                        signText,
                        singleLine.get()
                    )
                }
                .getOrElse { it.stacktrace() }
        } ui
            { state ->
                if (selectedKeyPairAlg.get() == "JWT" && signText.isNotEmpty()) {
                    val (alg, payload) = signText.jwtParse()
                    selectedSigAlg.set(alg)
                    taRaw.text = payload
                }
                primaryStage.showToast("result: \n$state")
                timeConsumption = System.currentTimeMillis() - startTime
                labelInfo.text = info
            }
}
