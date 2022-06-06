package me.leon.ctf

private val engine = TrollScriptEngine()

fun String.trollScriptEncrypt(): String = TODO()

fun String.trollScriptDecrypt() = engine.interpret(this)
