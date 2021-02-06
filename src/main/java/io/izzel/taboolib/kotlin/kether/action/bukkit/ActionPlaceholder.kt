package io.izzel.taboolib.kotlin.kether.action.bukkit

import io.izzel.kether.common.api.ParsedAction
import io.izzel.kether.common.api.QuestAction
import io.izzel.kether.common.api.QuestContext
import io.izzel.kether.common.loader.types.ArgTypes
import io.izzel.taboolib.kotlin.kether.KetherParser
import io.izzel.taboolib.kotlin.kether.ScriptContext
import io.izzel.taboolib.kotlin.kether.ScriptParser
import io.izzel.taboolib.module.locale.TLocale
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionPlaceholder(val source: ParsedAction<*>) : QuestAction<String>() {

    override fun process(context: QuestContext.Frame): CompletableFuture<String> {
        return context.newFrame(source).run<Any>().thenApply {
            TLocale.Translate.setPlaceholders((context.context() as ScriptContext).sender!!, it.toString().trimIndent())
        }
    }

    override fun toString(): String {
        return "ActionPlaceholder(source='$source')"
    }

    companion object {

        @KetherParser(["papi", "placeholder"])
        fun parser() = ScriptParser.parser {
            ActionPlaceholder(it.next(ArgTypes.ACTION))
        }
    }
}