package me.leon.ctf

@Suppress("ALL")
interface BrainFuckToken {
    val start: String
    val next: String
    val pre: String
    val plus: String
    val minus: String
    val output: String
    val input: String
    val bracketLeft: String
    val bracketRight: String
    val end: String
}
