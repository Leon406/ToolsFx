package me.leon.ext.math

/** support shift */
fun Int.circleIndex(len: Int = 26, shift: Int = 0) =
    with(this + shift) { if (this < 0) (this % len + len) else this } % len
