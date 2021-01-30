package io.izzel.taboolib.kotlin.kether.action

import io.izzel.kether.common.api.ParsedAction
import io.izzel.kether.common.api.QuestAction
import io.izzel.kether.common.api.QuestContext
import io.izzel.kether.common.loader.types.ArgTypes
import io.izzel.taboolib.kotlin.kether.ScriptParser
import java.util.concurrent.CompletableFuture


/**
 * @author IzzelAliz
 */
class ActionForEach(val key: String, val values: ParsedAction<*>, val action: ParsedAction<*>) : QuestAction<Void>() {

    override fun process(context: QuestContext.Frame): CompletableFuture<Void> {
        val future = CompletableFuture<Void>()
        context.newFrame(values).run<Any>().thenAccept {
            when (it) {
                is Collection<*> -> {
                    process(context, future, 0, it.map { i -> i as Any }.toList())
                }
                is Map<*, *> -> {
                    process(context, future, 0, it.map { i -> i.key to i.value }.toList())
                }
                is Array<*> -> {
                    process(context, future, 0, it.map { i -> i as Any }.toList())
                }
                else -> {
                    process(context, future, 0, listOf(it))
                }
            }
        }
        return future
    }

    fun process(frame: QuestContext.Frame, future: CompletableFuture<Void>, cur: Int, i: List<Any>) {
        if (cur < i.size) {
            val e = i[cur]
            if (e is Pair<*, *>) {
                frame.variables()["$key-key"] = e.first
                frame.variables()["$key-value"] = e.second
            }
            frame.variables()[key] = e
            frame.newFrame(action).run<Any>().thenRunAsync({
                process(frame, future, cur + 1, i)
            }, frame.context().executor)
        } else {
            frame.variables().remove(key)
            frame.variables().remove("$key-key")
            frame.variables().remove("$key-value")
            future.complete(null)
        }
    }

    companion object {

        /**
         * for i in players then {  }
         * for i in range 1 to 10 then {  }
         */
        fun parser() = ScriptParser.parser {
            ActionForEach(it.nextToken(), it.run {
                expect("in")
                next(ArgTypes.ACTION)
            }, it.run {
                expect("then")
                next(ArgTypes.ACTION)
            })
        }
    }
}