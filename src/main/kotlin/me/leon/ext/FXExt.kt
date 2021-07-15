package me.leon.ext

import java.awt.Desktop
import java.net.URL
import javafx.scene.input.Clipboard
import javafx.scene.input.ClipboardContent

fun String.copy() =
    Clipboard.getSystemClipboard().setContent(ClipboardContent().apply { putString(this@copy) })

fun String.openInBrowser() = Desktop.getDesktop().browse(URL(this).toURI())
