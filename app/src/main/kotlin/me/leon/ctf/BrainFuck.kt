package me.leon.ctf

private val engine = BrainfuckEngine()

fun String.brainFuckEncrypt(): String = throw NotImplementedError()

fun String.brainFuckDecrypt() = engine.interpret(this)
