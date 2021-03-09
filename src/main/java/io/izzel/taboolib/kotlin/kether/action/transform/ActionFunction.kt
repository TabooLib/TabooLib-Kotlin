package io.izzel.taboolib.kotlin.kether.action.transform

import io.izzel.kether.common.api.ParsedAction
import io.izzel.kether.common.api.QuestAction
import io.izzel.kether.common.api.QuestContext
import io.izzel.kether.common.loader.types.ArgTypes
import io.izzel.taboolib.kotlin.kether.KetherFunction
import io.izzel.taboolib.kotlin.kether.KetherParser
import io.izzel.taboolib.kotlin.kether.ScriptParser
import io.izzel.taboolib.kotlin.kether.deepVars
import java.util.concurrent.CompletableFuture

/**
 * function "your name is {{player name}}"
 * @author IzzelAliz
 */
class ActionFunction(val source: ParsedAction<*>) : QuestAction<String>() {

    override fun process(context: QuestContext.Frame): CompletableFuture<String> {
        val vars = context.deepVars()
        return context.newFrame(source).run<Any>().thenApply {
            KetherFunction.parse(it.toString().trimIndent()) {
                vars.forEach { (k, v) -> rootFrame().variables().set(k, v) }
            }
        }
    }

    override fun toString(): String {
        return "ActionFunction(source='$source')"
    }

    companion object {

        @KetherParser(["inline", "function"])
        fun parser() = ScriptParser.parser {
            ActionFunction(it.next(ArgTypes.ACTION))
        }
    }
}