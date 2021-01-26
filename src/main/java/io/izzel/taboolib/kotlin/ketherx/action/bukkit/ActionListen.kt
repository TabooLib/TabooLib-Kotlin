package io.izzel.taboolib.kotlin.ketherx.action.bukkit

import io.izzel.kether.common.api.QuestAction
import io.izzel.kether.common.api.QuestContext
import io.izzel.kether.common.loader.InferType
import io.izzel.kether.common.util.LocalizedException
import io.izzel.taboolib.kotlin.ketherx.EventOperator
import io.izzel.taboolib.kotlin.ketherx.KetherX
import io.izzel.taboolib.kotlin.ketherx.ScriptContext
import io.izzel.taboolib.kotlin.ketherx.ScriptParser
import io.izzel.taboolib.kotlin.ketherx.util.Closables
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionListen(val operator: EventOperator<*>, val value: InferType) : QuestAction<Void>() {

    @Suppress("UNCHECKED_CAST")
    override fun process(context: QuestContext.Frame): CompletableFuture<Void> {
        return CompletableFuture<Void>().also { future ->
            val s = (context.context() as ScriptContext)
            s.listener = future
            context.addClosable(Closables.listening(operator.event.java) {
                s.event = it
                s.eventOperator = operator
                value.process(context)
            })
        }
    }

    override fun toString(): String {
        return "ActionListen(operator=$operator, value=$value)"
    }

    companion object {

        @Suppress("UnstableApiUsage")
        fun parser() = ScriptParser.parser {
            val name = it.nextToken()
            val event = KetherX.getEventOperator(name) ?: throw LocalizedException.of("unknown-event", name)
            it.expect("then")
            ActionListen(event, it.nextInferType())
        }
    }
}