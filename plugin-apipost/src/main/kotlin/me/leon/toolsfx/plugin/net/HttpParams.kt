package me.leon.toolsfx.plugin.net

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty

class HttpParams {

    val keyProperty = SimpleStringProperty("")
    val valueProperty = SimpleStringProperty("")
    val fileProperty = SimpleBooleanProperty(false)
    val enableProperty = SimpleBooleanProperty(true)

    val isFile: Boolean
        get() = fileProperty.get()

    val isEnable: Boolean
        get() = enableProperty.get()

    val key: String
        get() = keyProperty.get()

    val value: String
        get() = valueProperty.get()

    override fun equals(other: Any?): Boolean {
        return if (other is HttpParams) this.keyProperty.value == other.key else false
    }

    override fun hashCode(): Int {
        return key.hashCode()
    }
}
