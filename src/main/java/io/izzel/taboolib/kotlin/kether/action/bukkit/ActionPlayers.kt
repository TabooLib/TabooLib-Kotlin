package io.izzel.taboolib.kotlin.kether.action.bukkit

import io.izzel.kether.common.api.ParsedAction
import io.izzel.kether.common.api.QuestAction
import io.izzel.kether.common.api.QuestContext
import io.izzel.kether.common.loader.types.ArgTypes
import io.izzel.taboolib.kotlin.kether.ScriptParser
import io.izzel.taboolib.module.locale.TLocale
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionPlayers() : QuestAction<List<Player>>() {

    override fun process(context: QuestContext.Frame): CompletableFuture<List<Player>> {
        return CompletableFuture.completedFuture(Bukkit.getOnlinePlayers().toList())
    }

    override fun toString(): String {
        return "ActionPlayers()"
    }

    companion object {

        @Suppress("UnstableApiUsage")
        fun parser() = ScriptParser.parser {
            ActionPlayers()
        }
    }
}