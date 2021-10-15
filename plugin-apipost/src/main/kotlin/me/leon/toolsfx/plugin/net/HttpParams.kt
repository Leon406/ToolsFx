package me.leon.toolsfx.plugin.net

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty

class HttpParams {
    val isFile: Boolean
        get() = isFileProperty.get()
    val isEnable: Boolean
        get() = enableProperty.get()

    val key: String
        get() = keyProperty.get()

    val value: String
        get() = valueProperty.get()

    val keyProperty = SimpleStringProperty()
    val valueProperty = SimpleStringProperty()
    val isFileProperty = SimpleBooleanProperty(false)
    val enableProperty = SimpleBooleanProperty(true)
}
