package io.izzel.taboolib.kotlin.kether.action.bukkit

import io.izzel.kether.common.api.ParsedAction
import io.izzel.kether.common.api.QuestAction
import io.izzel.kether.common.api.QuestContext
import io.izzel.taboolib.kotlin.asList
import io.izzel.taboolib.kotlin.kether.KetherParser
import io.izzel.taboolib.kotlin.kether.ScriptContext
import io.izzel.taboolib.kotlin.kether.ScriptParser
import io.izzel.taboolib.kotlin.sendScoreboard
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionScoreboard(val content: ParsedAction<*>) : QuestAction<Void>() {

    override fun process(context: QuestContext.Frame): CompletableFuture<Void> {
        return context.newFrame(content).run<Any>().thenAccept { content ->
            val viewer = ((context.context() as ScriptContext).sender as? Player) ?: throw RuntimeException("No player selected.")
            if (content == null) {
                viewer.sendScoreboard(null)
            } else {
                val body = if (content is List<*>) content.asList() else content.toString().trimIndent().split("\n")
                viewer.sendScoreboard(body[0], *body.filterIndexed { index, _ -> index > 0 }.toTypedArray())
            }
        }
    }

    override fun toString(): String {
        return "ActionScoreboard(content=$content)"
    }

    companion object {

        /**
         * scoreboard *"123"
         */
        @KetherParser(["scoreboard"])
        fun parser() = ScriptParser.parser {
            ActionScoreboard(it.nextAction<Any>())
        }
    }
}