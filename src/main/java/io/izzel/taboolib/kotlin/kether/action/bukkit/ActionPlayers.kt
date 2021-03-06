package io.izzel.taboolib.kotlin.kether.action.bukkit

import io.izzel.kether.common.api.QuestAction
import io.izzel.kether.common.api.QuestContext
import io.izzel.taboolib.kotlin.kether.KetherParser
import io.izzel.taboolib.kotlin.kether.ScriptParser
import org.bukkit.Bukkit
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionPlayers() : QuestAction<List<String>>() {

    override fun process(context: QuestContext.Frame): CompletableFuture<List<String>> {
        return CompletableFuture.completedFuture(Bukkit.getOnlinePlayers().map { it.name }.toList())
    }

    override fun toString(): String {
        return "ActionPlayers()"
    }

    companion object {

        @KetherParser(["players"])
        fun parser() = ScriptParser.parser {
            ActionPlayers()
        }
    }
}