package io.izzel.taboolib.kotlin.kether.action.bukkit

import io.izzel.kether.common.api.ParsedAction
import io.izzel.kether.common.api.QuestAction
import io.izzel.kether.common.api.QuestContext
import io.izzel.kether.common.loader.types.ArgTypes
import io.izzel.taboolib.kotlin.kether.ScriptContext
import io.izzel.taboolib.kotlin.kether.ScriptParser
import io.izzel.taboolib.module.locale.TLocale
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionActionBar(val message: ParsedAction<*>) : QuestAction<Void>() {

    override fun process(context: QuestContext.Frame): CompletableFuture<Void> {
        return context.newFrame(message).run<Any>().thenAccept {
            val viewer = (context.context() as ScriptContext).sender as? Player ?: throw RuntimeException("No player selected.")
            TLocale.Display.sendActionBar(viewer, it.toString().trimIndent().replace("@sender", viewer.name))
        }
    }

    override fun toString(): String {
        return "ActionActionBar(message=$message)"
    }

    companion object {

        @Suppress("UnstableApiUsage")
        fun parser() = ScriptParser.parser {
            ActionActionBar(it.next(ArgTypes.ACTION))
        }
    }
}