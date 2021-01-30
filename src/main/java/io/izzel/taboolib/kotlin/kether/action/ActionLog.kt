package io.izzel.taboolib.kotlin.kether.action

import io.izzel.kether.common.api.ParsedAction
import io.izzel.kether.common.api.QuestAction
import io.izzel.kether.common.api.QuestContext
import io.izzel.kether.common.loader.types.ArgTypes
import io.izzel.taboolib.kotlin.kether.ScriptContext
import io.izzel.taboolib.kotlin.kether.ScriptParser
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionLog(val message: ParsedAction<*>) : QuestAction<Void>() {

    override fun process(context: QuestContext.Frame): CompletableFuture<Void> {
        return context.newFrame(message).run<Any>().thenAccept {
            if (context.context() is ScriptContext) {
                val player = (context.context() as ScriptContext).sender?.name.toString()
                println(it.toString().trimIndent().replace("@sender", player))
            } else {
                println(it.toString().trimIndent())
            }
        }
    }

    override fun toString(): String {
        return "ActionLog{" +
                "message='" + message + '\'' +
                '}'
    }

    companion object {

        @Suppress("UnstableApiUsage")
        fun parser() = ScriptParser.parser {
            ActionLog(it.next(ArgTypes.ACTION))
        }
    }
}