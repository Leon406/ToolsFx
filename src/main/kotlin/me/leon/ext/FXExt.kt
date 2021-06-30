package me.leon.ext

import javafx.scene.input.Clipboard
import javafx.scene.input.ClipboardContent

fun String.copy() =
    Clipboard.getSystemClipboard().setContent(ClipboardContent().apply { putString(this@copy) })