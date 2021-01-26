package io.izzel.taboolib.kotlin.ketherx.action

import io.izzel.kether.common.api.QuestAction
import io.izzel.kether.common.api.QuestContext
import io.izzel.kether.common.loader.InferType
import io.izzel.taboolib.kotlin.ketherx.KetherFunction
import io.izzel.taboolib.kotlin.ketherx.ScriptContext
import io.izzel.taboolib.kotlin.ketherx.ScriptParser
import java.util.concurrent.CompletableFuture

/**
 * function "your name is {{player name}}"
 * @author IzzelAliz
 */
class ActionFunction(val source: InferType) : QuestAction<String>() {

    override fun process(context: QuestContext.Frame): CompletableFuture<String> {
        val s = context.context() as ScriptContext
        return source.process(context).thenApply {
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
            ActionFunction(it.nextInferType())
        }
    }
}