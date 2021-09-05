package me.leon.ext

import java.util.prefs.Preferences

object Prefs {
    private const val IGNORE_UPDATE = "isIgnoreUpdate"
    private val preference = Preferences.userNodeForPackage(Prefs::class.java)
    var isIgnoreUpdate
        get() = preference.getBoolean(IGNORE_UPDATE, false)
        set(value) {
            preference.putBoolean(IGNORE_UPDATE, value)
        }
}
