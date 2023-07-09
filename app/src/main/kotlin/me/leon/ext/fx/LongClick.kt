package me.leon.ext.fx

import javafx.scene.control.Control
import kotlin.concurrent.thread

/**
 * @author Leon
 * @since 2023-07-06 11:13
 * @email deadogone@gmail.com
 */
fun Control.longClick(threshold: Long = 300L, action: () -> Unit) {
    var pressedState: Boolean
    var thread: Thread? = null

    setOnMousePressed {
        pressedState = true
        thread?.interrupt()
        thread = thread {
            Thread.sleep(threshold)
            if (pressedState) {
                runOnUi(action)
            }
        }
    }
    setOnMouseReleased { pressedState = false }
}
