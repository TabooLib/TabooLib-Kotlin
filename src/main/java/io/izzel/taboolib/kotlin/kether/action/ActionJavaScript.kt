package io.izzel.taboolib.kotlin.kether.action

import io.izzel.kether.common.api.QuestAction
import io.izzel.kether.common.api.QuestContext
import io.izzel.taboolib.kotlin.kether.KetherParser
import io.izzel.taboolib.kotlin.kether.ScriptContext
import io.izzel.taboolib.kotlin.kether.ScriptParser
import io.izzel.taboolib.module.event.EventNormal
import io.izzel.taboolib.util.Features
import org.bukkit.Bukkit
import java.util.concurrent.CompletableFuture
import javax.script.CompiledScript
import javax.script.SimpleBindings

/**
 * @author IzzelAliz
 */
class ActionJavaScript(val script: CompiledScript) : QuestAction<Any>() {

    override fun process(context: QuestContext.Frame): CompletableFuture<Any> {
        val s = (context.context() as ScriptContext)
        val r = try {
            script.eval(
                SimpleBindings(
                    Event(
                        hashMapOf(
                            "event" to s.event,
                            "sender" to s.sender,
                            "server" to Bukkit.getServer(),
                        ).also {
                            val vars = ArrayList<String>()
                            var p = context.parent()
                            while (p.isPresent) {
                                vars.addAll(p.get().variables().keys())
                                p = p.get().parent()
                            }
                            it.putAll(vars.map { i -> i to context.variables().get<Any>(i).orElse(null) })
                        }, s
                    ).bindings
                )
            )
        } catch (e: Exception) {
            println(e.localizedMessage)
        }
        return CompletableFuture.completedFuture(r)
    }

    override fun toString(): String {
        return "ActionJavaScript(script=$script)"
    }

    class Event(val bindings: MutableMap<String, Any?>, val context: ScriptContext) : EventNormal<Event>()

    companion object {

        @KetherParser(["$", "js", "javascript"])
        fun parser() = ScriptParser.parser {
            ActionJavaScript(Features.compileScript(it.nextToken().trimIndent())!!)
        }
    }
}