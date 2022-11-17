package me.leon.ctf

private val engine = BrainfuckEngine()

fun String.brainFuckEncrypt(): String = brainFuckShortEncode()

fun String.brainFuckDecrypt() = engine.interpret(this)
