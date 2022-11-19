package me.leon.ctf

private val engine = TrollScriptEngine()

fun String.trollScriptEncrypt(): String = brainFuckShortEncode(TrollScriptEngine.Token)

fun String.trollScriptDecrypt() = engine.interpret(this)
