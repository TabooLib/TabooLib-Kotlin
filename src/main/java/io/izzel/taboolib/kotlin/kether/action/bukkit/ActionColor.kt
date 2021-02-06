package io.izzel.taboolib.kotlin.kether.action.bukkit

import io.izzel.kether.common.api.ParsedAction
import io.izzel.kether.common.api.QuestAction
import io.izzel.kether.common.api.QuestContext
import io.izzel.kether.common.loader.types.ArgTypes
import io.izzel.taboolib.kotlin.kether.KetherParser
import io.izzel.taboolib.kotlin.kether.ScriptParser
import io.izzel.taboolib.module.locale.TLocale
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionColor(val source: ParsedAction<*>) : QuestAction<String>() {

    override fun process(context: QuestContext.Frame): CompletableFuture<String> {
        return context.newFrame(source).run<Any>().thenApply {
            TLocale.Translate.setColored(it.toString().trimIndent())
        }
    }

    override fun toString(): String {
        return "ActionColor(source='$source')"
    }

    companion object {

        @KetherParser(["color", "colored"])
        fun parser() = ScriptParser.parser {
            ActionColor(it.next(ArgTypes.ACTION))
        }
    }
}