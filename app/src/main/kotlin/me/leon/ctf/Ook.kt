package me.leon.ctf

private val engine = OokEngine()

fun String.ookEncrypt(): String = brainFuckShortEncode(OokEngine.Token)

fun String.ookDecrypt() = engine.interpret(this.replace("\r\n|\n".toRegex(), " "))
