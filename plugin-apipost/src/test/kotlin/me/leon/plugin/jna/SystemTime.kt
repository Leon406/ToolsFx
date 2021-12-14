package me.leon.plugin.jna

import com.sun.jna.Structure

@Structure.FieldOrder(
    "wYear",
    "wMonth",
    "wDayOfWeek",
    "wDay",
    "wHour",
    "wMinute",
    "wSecond",
    "wMilliseconds"
)
class SystemTime : Structure() {
    @JvmField var wYear: Short = 0
    @JvmField var wMonth: Short = 0
    @JvmField var wDayOfWeek: Short = 0
    @JvmField var wDay: Short = 0
    @JvmField var wHour: Short = 0
    @JvmField var wMinute: Short = 0
    @JvmField var wSecond: Short = 0
    @JvmField var wMilliseconds: Short = 0
}
