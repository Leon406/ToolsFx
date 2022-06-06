package me.leon.ctf

private val engine = OokEngine()

fun String.ookEncrypt(): String = TODO()

fun String.ookDecrypt() = engine.interpret(this.replace("\r\n|\n".toRegex(), " "))
