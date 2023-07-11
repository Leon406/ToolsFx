package me.leon.view

import javafx.beans.property.*
import javafx.event.EventTarget
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.input.KeyCode
import me.leon.Styles
import me.leon.config.BAIDU_OCR_APPLY_URL
import me.leon.ext.*
import me.leon.ext.fx.*
import me.leon.ext.voice.TTSVoice
import me.leon.ext.voice.Voice
import me.leon.misc.Translator
import tornadofx.*

class StringProcessConfigFragment : Fragment(FX.messages["stringProcess"]) {

    private val selectedVoice: SimpleObjectProperty<Voice> =
        SimpleObjectProperty(TTSVoice.find(Prefs.ttsVoice))
    private val selectedTargetLanguage: SimpleStringProperty =
        SimpleStringProperty(Prefs.translateTargetLan)
    private val selectedLocale: SimpleStringProperty =
        SimpleStringProperty(selectedVoice.get().Locale)
    private val cacheable: SimpleBooleanProperty = SimpleBooleanProperty(Prefs.ttsCacheable)
    private val longSentence: SimpleBooleanProperty = SimpleBooleanProperty(Prefs.ttsLongSentence)
    private var selectGender: String = selectedVoice.get().Gender

    private var cbVoice: ComboBox<Voice> by singleAssign()
    private var volumeLabel: Label by singleAssign()
    private var speedLabel: Label by singleAssign()
    private var pitchLabel: Label by singleAssign()
    private var tfOcrKey: TextField by singleAssign()
    private var tfOcrSecret: TextField by singleAssign()

    private val locales: List<String> by lazy {
        TTSVoice.provides().map { it.Locale }.distinct().sorted()
    }

    override val root = vbox {
        paddingAll = DEFAULT_SPACING
        spacing = DEFAULT_SPACING_2X
        prefWidth = DEFAULT_SPACING_50X
        alignment = Pos.CENTER

        hbox {
            addClass(Styles.left)
            label("gender: ")
            togglegroup {
                radiobutton("Female") { isSelected = selectGender == "Female" }
                radiobutton("Male") { isSelected = selectGender != "Female" }
                selectedToggleProperty().addListener { _, _, new ->
                    println(new.cast<RadioButton>().text)
                    selectGender = new.cast<RadioButton>().text
                    cbVoice.items =
                        TTSVoice.provides()
                            .filter {
                                it.Locale == selectedLocale.get() && it.Gender == selectGender
                            }
                            .toObservable()
                    selectedVoice.set(cbVoice.items.first())
                }
            }
        }

        hbox {
            addClass(Styles.left)
            label("locale: ")
            combobox(selectedLocale, locales)
            selectedLocale.addListener { _, _, newValue ->
                newValue?.run {
                    cbVoice.items =
                        TTSVoice.provides()
                            .filter { it.Locale == this && it.Gender == selectGender }
                            .toObservable()
                    selectedVoice.set(cbVoice.items.first())
                }
            }
        }
        hbox {
            addClass(Styles.left)
            label("Voice: ")
            cbVoice =
                combobox(
                    selectedVoice,
                    TTSVoice.provides()
                        .filter { it.Locale == selectedLocale.get() && it.Gender == selectGender }
                        .toObservable()
                ) {
                    tooltip(selectedVoice.get().FriendlyName)
                    cellFormat { text = it.ShortName }
                }
            selectedVoice.addListener { _, _, newValue ->
                newValue?.let { cbVoice.tooltip(it.FriendlyName) }
            }
        }
        hbox {
            label("Speed: ")
            speedLabel = label(Prefs.ttsSpeed)
        }

        addSlider(-100, 400, Prefs.ttsSpeed, speedLabel)

        hbox {
            label("Volume: ")
            volumeLabel = label(Prefs.ttsVolume)
        }

        addSlider(-100, 0, Prefs.ttsVolume, volumeLabel)

        hbox {
            label("Pitch: ")
            pitchLabel = label(Prefs.ttsPitch)
        }

        addSlider(-100, 400, Prefs.ttsPitch, pitchLabel)

        hbox {
            addClass(Styles.center)
            checkbox("cache", cacheable)
            checkbox("long sentence", longSentence)
        }

        hbox {
            addClass(Styles.left)
            label("target language: ")
            combobox(selectedTargetLanguage, Translator.SUPPORT_LANGUAGE.toList())
        }

        hbox {
            addClass(Styles.left)
            hyperlink("百度OCR ").action { BAIDU_OCR_APPLY_URL.openInBrowser() }
        }
        hbox {
            addClass(Styles.left)
            tfOcrKey = textfield(Prefs.ocrKey) { promptText = "key" }
            tfOcrSecret = textfield(Prefs.ocrSecret) { promptText = "secret" }
        }

        hbox {
            spacing = DEFAULT_SPACING_2X
            alignment = Pos.CENTER

            button(messages["save"]) {
                action {
                    Prefs.configTtsParams(
                        selectedVoice.get().ShortName,
                        speedLabel.text,
                        volumeLabel.text,
                        pitchLabel.text,
                        cacheable.get(),
                        longSentence.get()
                    )
                    Prefs.configOcr(tfOcrKey.text, tfOcrSecret.text)
                    Prefs.translateTargetLan = selectedTargetLanguage.get()
                }
            }

            button(messages["reset"]) {
                action {
                    Prefs.configTtsParams()
                    Prefs.configOcr()
                    Prefs.translateTargetLan = TRANSLATE_DEFAULT_LANGUAGE
                    close()
                }
            }
        }
    }

    private fun EventTarget.addSlider(min: Int, max: Int, default: String, label: Label) {
        hbox {
            addClass(Styles.center)
            slider(min, max, default.parseParam()) {
                prefWidth = DEFAULT_SPACING_32X
                setOnDragDetected { label.text = value.toInt().ttsParam() }
                setOnMouseDragged { label.text = value.toInt().ttsParam() }
                setOnKeyPressed {
                    if (it.code == KeyCode.LEFT) {
                        label.text = (value - 0.5).toInt().coerceAtLeast(min).ttsParam()
                    } else if (it.code == KeyCode.RIGHT) {
                        label.text = (value + 0.5).toInt().coerceAtMost(max).ttsParam()
                    }
                }
            }
        }
    }

    private fun Int.ttsParam() =
        if (this < 0) {
            String.format("%d%%", this)
        } else {
            String.format("+%d%%", this)
        }

    private fun String.parseParam() = replace("%", "").toInt()
}
