package me.leon.view

import javafx.beans.property.*
import javafx.geometry.Pos
import javafx.scene.control.*
import me.leon.Styles
import me.leon.ext.*
import me.leon.ext.fx.*
import me.leon.ext.voice.TTSVoice
import me.leon.ext.voice.Voice
import tornadofx.*

class TtsFragment : Fragment("TTS") {

    private val selectedVoice: SimpleObjectProperty<Voice> =
        SimpleObjectProperty(TTSVoice.find(Prefs.ttsVoice))
    private val selectedLocale: SimpleStringProperty =
        SimpleStringProperty(selectedVoice.get().Locale)
    private val cacheable: SimpleBooleanProperty = SimpleBooleanProperty(Prefs.ttsCacheable)
    private val longSentence: SimpleBooleanProperty = SimpleBooleanProperty(Prefs.ttsLongSentence)
    private var selectGender: String = selectedVoice.get().Gender

    private var cbVoice: ComboBox<Voice> by singleAssign()
    private var volumeLabel: Label by singleAssign()
    private var speedLabel: Label by singleAssign()
    private var pitchLabel: Label by singleAssign()

    private val locales: List<String> by lazy {
        TTSVoice.provides().map { it.Locale }.distinct().sorted()
    }

    override val root = vbox {
        paddingAll = DEFAULT_SPACING
        spacing = DEFAULT_SPACING_2X
        prefWidth = DEFAULT_SPACING_40X
        alignment = Pos.CENTER
        hbox {
            addClass(Styles.left)
            label("gender: ")
            togglegroup {
                radiobutton("Female") { isSelected = selectGender == "Female" }
                radiobutton("Male") { isSelected = selectGender != "Female" }
                selectedToggleProperty().addListener { _, old, new ->
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
        hbox {
            addClass(Styles.center)
            slider(-100, 400, Prefs.ttsSpeed.parseParam()) {
                prefWidth = DEFAULT_SPACING_32X
                setOnDragDetected { speedLabel.text = value.ttsParam() }
                setOnMouseDragged { speedLabel.text = value.ttsParam() }
            }
        }
        hbox {
            label("Volume: ")
            volumeLabel = label(Prefs.ttsVolume)
        }

        hbox {
            addClass(Styles.center)
            slider(-100, 0, Prefs.ttsVolume.parseParam()) {
                prefWidth = DEFAULT_SPACING_32X
                setOnDragDetected { volumeLabel.text = value.ttsParam() }
                setOnMouseDragged { volumeLabel.text = value.ttsParam() }
            }
        }

        hbox {
            label("Pitch: ")
            pitchLabel = label(Prefs.ttsPitch)
        }

        hbox {
            addClass(Styles.center)
            slider(-100, 400, Prefs.ttsPitch.parseParam()) {
                prefWidth = DEFAULT_SPACING_32X
                setOnDragDetected { pitchLabel.text = value.ttsParam() }
                setOnMouseDragged { pitchLabel.text = value.ttsParam() }
            }
        }

        hbox {
            addClass(Styles.center)
            checkbox("cache", cacheable)
            checkbox("long sentence", longSentence)
        }

        hbox {
            spacing = DEFAULT_SPACING_2X
            alignment = Pos.CENTER

            button(messages["save"]) {
                action {
                    Prefs.ttsVoice = selectedVoice.get().ShortName
                    Prefs.ttsSpeed = speedLabel.text
                    Prefs.ttsVolume = volumeLabel.text
                    Prefs.ttsPitch = pitchLabel.text
                    Prefs.ttsCacheable = cacheable.get()
                    Prefs.ttsLongSentence = longSentence.get()
                    close()
                }
            }

            button(messages["reset"]) {
                action {
                    Prefs.ttsVoice = TTS_DEFAULT_MODEL
                    Prefs.ttsSpeed = TTS_DEFAULT_RATE
                    Prefs.ttsVolume = TTS_DEFAULT_RATE
                    Prefs.ttsPitch = TTS_DEFAULT_RATE
                    Prefs.ttsCacheable = false
                    Prefs.ttsLongSentence = false
                    close()
                }
            }
        }
    }

    private fun Double.ttsParam() =
        if (this < 0) {
            String.format("%.0f%%", this)
        } else {
            String.format("+%.0f%%", this)
        }

    private fun String.parseParam() = replace("%", "").toInt()
}
