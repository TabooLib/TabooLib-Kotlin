package io.izzel.taboolib.kotlin.ketherx.action.bukkit

import io.izzel.kether.common.api.QuestAction
import io.izzel.kether.common.api.QuestContext
import io.izzel.kether.common.loader.InferType
import io.izzel.taboolib.kotlin.ketherx.ScriptContext
import io.izzel.taboolib.kotlin.ketherx.ScriptParser
import io.izzel.taboolib.module.locale.TLocale
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionPlaceholder(val source: InferType) : QuestAction<String>() {

    override fun process(context: QuestContext.Frame): CompletableFuture<String> {
        return source.process(context).thenApply {
            TLocale.Translate.setPlaceholders((context.context() as ScriptContext).sender!!, it.toString().trimIndent())
        }
    }

    override fun toString(): String {
        return "ActionPlaceholder(source='$source')"
    }

    companion object {

        @Suppress("UnstableApiUsage")
        fun parser() = ScriptParser.parser {
            ActionPlaceholder(it.nextInferType())
        }
    }
}