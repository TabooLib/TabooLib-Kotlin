package io.izzel.taboolib.kotlin.kether.action.bukkit

import io.izzel.kether.common.api.ParsedAction
import io.izzel.kether.common.api.QuestAction
import io.izzel.kether.common.api.QuestContext
import io.izzel.kether.common.loader.types.ArgTypes
import io.izzel.kether.common.util.LocalizedException
import io.izzel.taboolib.kotlin.kether.ScriptContext
import io.izzel.taboolib.kotlin.kether.ScriptParser
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionEvent(val key: String, val symbol: Symbol, val value: ParsedAction<*>) : QuestAction<Any?>() {

    enum class Symbol {

        GET, SET
    }

    @Suppress("UNCHECKED_CAST")
    override fun process(context: QuestContext.Frame): CompletableFuture<Any?> {
        val s = (context.context() as ScriptContext)
        val event = s.event
        val eventOperator = s.eventOperator
        if (event == null || eventOperator == null) {
            throw RuntimeException("No event selected.")
        }
        return if (symbol == Symbol.SET) {
            context.newFrame(value).run<Any>().thenApply {
                eventOperator.write(key, event, it)
            }
        } else {
            CompletableFuture.completedFuture(eventOperator.read(key, event))
        }
    }

    override fun toString(): String {
        return "ActionEvent(key='$key', symbol=$symbol, value=$value)"
    }

    companion object {

        @Suppress("UnstableApiUsage")
        fun parser() = ScriptParser.parser {
            val symbol = when (val type = it.nextToken()) {
                "set" -> Symbol.SET
                "get" -> Symbol.GET
                else -> throw LocalizedException.of("not-event-method", type)
            }
            val key = it.nextToken()
            val value = it.next(ArgTypes.ACTION)
            ActionEvent(key, symbol, value)
        }
    }
}