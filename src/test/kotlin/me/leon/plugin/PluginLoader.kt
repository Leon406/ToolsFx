package me.leon.plugin

import java.io.File
import java.net.URLClassLoader

fun main() {

    val pluginPath = "C:\\Users\\Leon\\Desktop\\ccc\\me.leon.plugin.TestPlugin.jar"
    val pluginClass = File(pluginPath).nameWithoutExtension
    val cl = URLClassLoader(arrayOf(File(pluginPath).toURI().toURL()))
    var fxPlugin = (cl.loadClass(pluginClass).newInstance() as IToolsFxPlugin)
    fxPlugin.info()
}
