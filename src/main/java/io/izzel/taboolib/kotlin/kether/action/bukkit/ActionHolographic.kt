package io.izzel.taboolib.kotlin.kether.action.bukkit

import io.izzel.kether.common.api.ParsedAction
import io.izzel.kether.common.api.QuestAction
import io.izzel.kether.common.api.QuestContext
import io.izzel.taboolib.kotlin.asList
import io.izzel.taboolib.kotlin.kether.KetherParser
import io.izzel.taboolib.kotlin.kether.ScriptContext
import io.izzel.taboolib.kotlin.kether.ScriptParser
import io.izzel.taboolib.kotlin.sendHolographic
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionHolographic(val message: ParsedAction<*>, val location: ParsedAction<Location>) : QuestAction<Void>() {

    override fun process(context: QuestContext.Frame): CompletableFuture<Void> {
        return context.newFrame(message).run<Any>().thenAccept { text ->
            context.newFrame(location).run<Location>().thenAccept { loc ->
                val viewer = ((context.context() as ScriptContext).sender as? Player) ?: throw RuntimeException("No player selected.")
                val body = if (text is List<*>) text.asList() else text.toString().trimIndent().split("\n")
                viewer.sendHolographic(loc, *body.toTypedArray())
            }
        }
    }

    override fun toString(): String {
        return "ActionHolographic(message=$message, location=$location)"
    }

    companion object {

        /**
         * holographic *"123" at location 0 0 0
         */
        @KetherParser(["holographic"])
        fun parser() = ScriptParser.parser {
            val text = it.nextAction<Any>()
            it.expect("at")
            val loc = it.nextAction<Location>()
            ActionHolographic(text, loc)
        }
    }
}