package me.leon.ctf

private val engine = TrollScriptEngine()

fun String.trollScriptEncrypt(): String = throw NotImplementedError()

fun String.trollScriptDecrypt() = engine.interpret(this)
