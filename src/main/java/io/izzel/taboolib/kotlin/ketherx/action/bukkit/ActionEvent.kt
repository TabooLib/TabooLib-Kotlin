package io.izzel.taboolib.kotlin.ketherx.action.bukkit

import io.izzel.kether.common.api.QuestAction
import io.izzel.kether.common.api.QuestContext
import io.izzel.kether.common.loader.InferType
import io.izzel.kether.common.util.LocalizedException
import io.izzel.taboolib.kotlin.ketherx.ScriptContext
import io.izzel.taboolib.kotlin.ketherx.ScriptParser
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionEvent(val key: String, val symbol: Symbol, val value: InferType) : QuestAction<Any?>() {

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
            value.process(context).thenApply {
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
            val value = it.nextInferType()
            ActionEvent(key, symbol, value)
        }
    }
}