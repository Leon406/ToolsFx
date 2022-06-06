package me.leon.toolsfx.plugin.table

import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.control.*
import javafx.scene.input.KeyEvent
import javafx.util.Callback
import javafx.util.StringConverter
import javafx.util.converter.DefaultStringConverter
import me.leon.toolsfx.plugin.table.CellGenerator.cancelEdit
import me.leon.toolsfx.plugin.table.CellGenerator.createTextField
import me.leon.toolsfx.plugin.table.CellGenerator.startEdit
import me.leon.toolsfx.plugin.table.CellGenerator.updateItem

class EditingCell<S, T> : TableCell<S, T> {

    var textFieldCell: TextField

    val contextTableView: TableView<S>
        get() = tableView

    private val converter: ObjectProperty<StringConverter<T>?> =
        SimpleObjectProperty(this, "converter")

    constructor() : this(null) {
        textFieldCell = createTextField(this, getConverter())
    }

    constructor(converter: StringConverter<T>?) {
        styleClass.add("text-field-table-cell")
        setConverter(converter)
        textFieldCell = createTextField(this, getConverter())
    }

    constructor(converter: StringConverter<T>?, isFieldEditable: Boolean) {
        styleClass.add("text-field-table-cell")
        setConverter(converter)
        textFieldCell = createTextField(this, getConverter())
        textFieldCell.isEditable = isFieldEditable
    }

    fun converterProperty(): ObjectProperty<StringConverter<T>?> {
        return converter
    }

    fun setConverter(value: StringConverter<T>?) {
        converterProperty().set(value)
    }

    fun getConverter(): StringConverter<T>? {
        return converterProperty().get()
    }

    override fun startEdit() {
        if (!isEditable || !tableView.isEditable || !tableColumn.isEditable) {
            return
        }
        super.startEdit()
        if (isEditing) {
            startEdit(this, getConverter(), null, null, textFieldCell)
        }
    }

    override fun cancelEdit() {
        super.cancelEdit()
        cancelEdit(this, getConverter(), null)
    }

    public override fun updateItem(item: T, empty: Boolean) {
        super.updateItem(item, empty)
        updateItem(this, getConverter(), null, null, textFieldCell)
    }

    fun setNextColumn(event: KeyEvent) {
        val nextColumn: TableColumn<S, *>? = getNextColumn(!event.isShiftDown)
        if (nextColumn != null) {

            // Get Selected index to reset current editable row
            val selectedRow = tableRow.index
            // Set row that serves as a control for tapping through
            if (currentRow == -1) {
                currentRow = tableRow.index
            }
            // Reset editing upon selection change row
            if (currentRow != selectedRow) {
                currentRow = selectedRow
            }
            val colSize = tableView.columns.size
            val colindex = tableView.columns.indexOf(nextColumn)
            if (colindex == colSize - 1) {
                control++
            }
            if (control > 0 && colindex == 0) {
                currentRow++
            }
            if (tableView.items.size > currentRow) {
                tableView.edit(currentRow, nextColumn)
            } else {
                currentRow = 0
                tableView.edit(currentRow, nextColumn)
            }
        }
    }

    private fun getNextColumn(forward: Boolean): TableColumn<S, *>? {
        val columns: MutableList<TableColumn<S, *>> = ArrayList()
        for (column in tableView.columns) {
            columns.addAll(getLeaves(column))
        }

        // There is no other column that supports editing.
        if (columns.size < 2) {
            return null
        }
        val currentIndex = columns.indexOf(tableColumn)
        var nextIndex = currentIndex
        if (forward) {
            nextIndex++
            if (nextIndex > columns.size - 1) {
                nextIndex = 0
            }
        } else {
            nextIndex--
            if (nextIndex < 0) {
                nextIndex = columns.size - 1
            }
        }
        return columns[nextIndex]
    }

    private fun getLeaves(column2: TableColumn<S, *>): ObservableList<TableColumn<S, *>> {
        val columns = FXCollections.observableArrayList<TableColumn<S, *>>()
        return if (column2.columns.isEmpty()) {
            // We only want the leaves that are editable.
            if (column2.isEditable) {
                columns.addAll(column2)
            }
            columns
        } else {
            for (column in column2.columns) {
                columns.addAll(getLeaves(column))
            }
            columns
        }
    }

    companion object {
        private var currentRow = -1
        private var control = 0

        fun <S> forTableColumn(): Callback<TableColumn<S, String>, TableCell<S, String>> {
            return forTableColumn(DefaultStringConverter())
        }

        private fun <S, T> forTableColumn(
            converter: StringConverter<T>?
        ): Callback<TableColumn<S, T>, TableCell<S, T>> {
            return Callback { EditingCell(converter) }
        }

        fun <S, T> forTableColumn(
            converter: StringConverter<T>?,
            isFieldEditable: Boolean
        ): Callback<TableColumn<S, T>, TableCell<S, T>> {
            return Callback { EditingCell(converter, isFieldEditable) }
        }
    }
}
