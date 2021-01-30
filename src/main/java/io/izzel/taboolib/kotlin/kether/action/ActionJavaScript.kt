package io.izzel.taboolib.kotlin.kether.action

import io.izzel.kether.common.api.QuestAction
import io.izzel.kether.common.api.QuestContext
import io.izzel.taboolib.kotlin.kether.ScriptContext
import io.izzel.taboolib.kotlin.kether.ScriptParser
import io.izzel.taboolib.module.event.EventNormal
import io.izzel.taboolib.util.Features
import java.util.concurrent.CompletableFuture
import javax.script.CompiledScript
import javax.script.SimpleBindings

/**
 * @author IzzelAliz
 */
class ActionJavaScript(val script: CompiledScript) : QuestAction<Any>() {

    override fun process(context: QuestContext.Frame): CompletableFuture<Any> {
        val s = (context.context() as ScriptContext)
        return CompletableFuture.completedFuture(
            script.eval(
                SimpleBindings(
                    Event(
                        hashMapOf(
                            "event" to s.event,
                            "sender" to s.sender,
                            "variables" to context.variables().values().map { it.key to it.value }.toMap(),
                        ), s
                    ).bindings
                )
            )
        )
    }

    override fun toString(): String {
        return "ActionJs(script=$script)"
    }

    class Event(val bindings: MutableMap<String, Any?>, val context: ScriptContext) : EventNormal<Event>()

    companion object {

        fun parser() = ScriptParser.parser {
            ActionJavaScript(Features.compileScript(it.nextToken().trimIndent())!!)
        }
    }
}