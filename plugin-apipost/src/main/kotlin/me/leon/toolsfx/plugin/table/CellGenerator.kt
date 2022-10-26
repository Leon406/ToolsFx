@file:Suppress("UNCHECKED_CAST")

package me.leon.toolsfx.plugin.table

import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.control.Cell
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.HBox
import javafx.util.StringConverter

internal object CellGenerator {

    private val defaultStringConverter: StringConverter<*> =
        object : StringConverter<Any?>() {
            override fun toString(t: Any?): String {
                return t?.toString().orEmpty()
            }

            override fun fromString(string: String): Any? {
                return string
            }
        }

    fun <T> defaultStringConverter(): StringConverter<T> {
        return defaultStringConverter as StringConverter<T>
    }

    private fun <T> getItemText(cell: Cell<T>, converter: StringConverter<T>?): String {
        return if (converter == null) {
            if (cell.item == null) {
                ""
            } else {
                cell.item.toString()
            }
        } else {
            converter.toString(cell.item)
        }
    }

    fun <T> updateItem(cell: Cell<T>, converter: StringConverter<T>?, textField: TextField?) {
        updateItem(cell, converter, null, null, textField)
    }

    @JvmStatic
    fun <T> updateItem(
        cell: Cell<T>,
        converter: StringConverter<T>?,
        hbox: HBox?,
        graphic: Node?,
        textField: TextField?
    ) {
        if (cell.isEmpty) {
            cell.text = null
            cell.setGraphic(null)
        } else {
            if (cell.isEditing) {
                if (textField != null) {
                    textField.text = getItemText(cell, converter)
                }
                cell.text = null
                if (graphic != null) {
                    hbox?.children?.setAll(graphic, textField)
                    cell.setGraphic(hbox)
                } else {
                    cell.setGraphic(textField)
                }
            } else {
                cell.text = getItemText(cell, converter)
                cell.setGraphic(graphic)
            }
        }
    }

    @JvmStatic
    fun <T> startEdit(
        cell: Cell<T>,
        converter: StringConverter<T>?,
        hbox: HBox?,
        graphic: Node?,
        textField: TextField?
    ) {
        if (textField != null) {
            textField.text = getItemText(cell, converter)
        }
        cell.text = null
        if (graphic != null) {
            hbox?.children?.setAll(graphic, textField)
            cell.setGraphic(hbox)
        } else {
            cell.setGraphic(textField)
        }
        textField!!.selectAll()
        textField.requestFocus()
    }

    @JvmStatic
    fun <T> cancelEdit(cell: Cell<T>, converter: StringConverter<T>?, graphic: Node?) {
        cell.text = getItemText(cell, converter)
        cell.graphic = graphic
    }

    @JvmStatic
    fun <T> createTextField(cell: Cell<T>, converter: StringConverter<T>?): TextField {
        val textField = TextField(getItemText(cell, converter))
        val cellEdit = cell as EditingCell<*, *>
        textField.onMouseExited = EventHandler {
            checkNotNull(converter) {
                ("Attempting to convert text input into Object, but provided " +
                    "StringConverter is null. Be sure to set a StringConverter " +
                    "in your cell factory.")
            }
            cell.commitEdit(converter.fromString(textField.text))
        }
        textField.addEventFilter(KeyEvent.KEY_PRESSED) { event: KeyEvent ->
            @Suppress("ElseCaseInsteadOfExhaustiveWhen")
            when (event.code) {
                KeyCode.ESCAPE -> {
                    cell.cancelEdit()
                    event.consume()
                }
                KeyCode.RIGHT -> {
                    cellEdit.tableView.selectionModel.selectRightCell()
                    event.consume()
                }
                KeyCode.LEFT -> {
                    cellEdit.tableView.selectionModel.selectLeftCell()
                    event.consume()
                }
                KeyCode.UP -> {
                    cellEdit.tableView.selectionModel.selectAboveCell()
                    event.consume()
                }
                KeyCode.DOWN -> {
                    cellEdit.tableView.selectionModel.selectBelowCell()
                    event.consume()
                }
                KeyCode.ENTER -> {
                    checkNotNull(converter) {
                        ("Attempting to convert text input into Object, but provided " +
                            "StringConverter is null. Be sure to set a StringConverter " +
                            "in your cell factory.")
                    }
                    cell.commitEdit(converter.fromString(textField.text))
                    event.consume()
                }
                KeyCode.TAB -> {
                    cell.commitEdit(converter!!.fromString(textField.text))
                    cellEdit.setNextColumn(event)
                    event.consume()
                }
                else -> {
                    // nop
                }
            }
        }
        return textField
    }
}
