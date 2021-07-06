package me.leon.ext

import javafx.scene.input.Clipboard
import javafx.scene.input.ClipboardContent
import java.awt.Desktop
import java.net.URL

fun String.copy() =
    Clipboard.getSystemClipboard().setContent(ClipboardContent().apply { putString(this@copy) })

fun String.openInBrowser() =
    Desktop.getDesktop().browse(URL(this).toURI());