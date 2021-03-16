package io.izzel.taboolib.kotlin

import io.izzel.taboolib.module.inject.TInject
import org.bukkit.entity.Player

abstract class AbsNMS {
    abstract fun setupScoreboard(player: Player)

    abstract fun setDisplayName(player: Player, title: String)

    abstract fun changeContent(player: Player, content: String, lastLineCount: Int)

    abstract fun display(player: Player)

    companion object {
        @TInject(asm = "io.izzel.taboolib.kotlin.NMSImpl")
        lateinit var instance: AbsNMS
    }
}