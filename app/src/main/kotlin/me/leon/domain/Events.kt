package me.leon.domain

import tornadofx.*

class SimpleMsgEvent(val msg: String, val type: Int) : FXEvent()

object EmptyEvent : FXEvent()
