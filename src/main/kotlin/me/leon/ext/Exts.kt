package me.leon.ext

inline fun <reified T> Any?.safeAs(): T? = this as? T

inline fun <reified T> Any?.cast() = this as T
