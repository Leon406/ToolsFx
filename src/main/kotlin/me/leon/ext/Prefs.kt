package me.leon.ext

import java.util.prefs.Preferences

object Prefs {
    private const val IGNORE_UPDATE = "isIgnoreUpdate"
    private const val ALWAYS_ON_TOP = "alwaysOnTop"
    private val preference = Preferences.userNodeForPackage(Prefs::class.java)
    var isIgnoreUpdate
        get() = preference.getBoolean(IGNORE_UPDATE, false)
        set(value) {
            preference.putBoolean(IGNORE_UPDATE, value)
        }
    var alwaysOnTop
        get() = preference.getBoolean(ALWAYS_ON_TOP, true)
        set(value) {
            preference.putBoolean(ALWAYS_ON_TOP, value)
        }
}
