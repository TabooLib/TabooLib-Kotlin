package io.izzel.taboolib.kotlin.kether.action

import io.izzel.kether.common.api.ParsedAction
import io.izzel.kether.common.api.QuestAction
import io.izzel.kether.common.api.QuestContext
import io.izzel.kether.common.loader.types.ArgTypes
import io.izzel.taboolib.kotlin.kether.KetherFunction
import io.izzel.taboolib.kotlin.kether.ScriptContext
import io.izzel.taboolib.kotlin.kether.ScriptParser
import java.util.concurrent.CompletableFuture

/**
 * function "your name is {{player name}}"
 * @author IzzelAliz
 */
class ActionFunction(val source: ParsedAction<*>) : QuestAction<String>() {

    override fun process(context: QuestContext.Frame): CompletableFuture<String> {
        val s = context.context() as ScriptContext
        return context.newFrame(source).run<Any>().thenApply {
            KetherFunction.parse(it.toString().trimIndent()) {
                s.rootFrame().variables().values().forEach { v ->
                    rootFrame().variables().set(v.key, v.value)
                }
            }
        }
    }

    override fun toString(): String {
        return "ActionFunction(source='$source')"
    }

    companion object {

        @Suppress("UnstableApiUsage")
        fun parser() = ScriptParser.parser {
            ActionFunction(it.next(ArgTypes.ACTION))
        }
    }
}