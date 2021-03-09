package io.izzel.taboolib.kotlin.kether.action

import io.izzel.kether.common.api.ParsedAction
import io.izzel.kether.common.api.QuestAction
import io.izzel.kether.common.api.QuestContext
import io.izzel.kether.common.loader.types.ArgTypes
import io.izzel.taboolib.kotlin.kether.KetherParser
import io.izzel.taboolib.kotlin.kether.ScriptParser
import org.bukkit.Bukkit
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionPrint(val message: ParsedAction<*>) : QuestAction<Void>() {

    override fun process(context: QuestContext.Frame): CompletableFuture<Void> {
        return context.newFrame(message).run<Any>().thenAccept {
            Bukkit.getLogger().info(it.toString().trimIndent())
        }
    }

    override fun toString(): String {
        return "ActionPrint(message=$message)"
    }

    companion object {

        @KetherParser(["log", "print"])
        fun parser() = ScriptParser.parser {
            ActionPrint(it.next(ArgTypes.ACTION))
        }
    }
}