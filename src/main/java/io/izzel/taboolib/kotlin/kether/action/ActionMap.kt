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
class ActionMap(val key: String, val values: ParsedAction<*>, val action: ParsedAction<*>) : QuestAction<List<Any>>() {

    override fun process(context: QuestContext.Frame): CompletableFuture<List<Any>> {
        val future = CompletableFuture<List<Any>>()
        context.newFrame(values).run<Any>().thenAcceptAsync({
            when (it) {
                is Collection<*> -> {
                    process(context, future, 0, it.map { i -> i as Any }.toList())
                }
                is Array<*> -> {
                    process(context, future, 0, it.map { i -> i as Any }.toList())
                }
                else -> {
                    process(context, future, 0, listOf(it))
                }
            }
        }, context.context().executor)
        return future;
    }

    fun process(frame: QuestContext.Frame, future: CompletableFuture<List<Any>>, cur: Int, i: List<Any>, r: MutableList<Any> = ArrayList()) {
        if (cur < i.size) {
            frame.variables()[key] = i[cur]
            frame.newFrame(action).run<Any>().thenRunAsync({
                frame.variables().get<Any>(key).ifPresent {
                    r.add(it)
                }
                process(frame, future, cur + 1, i)
            }, frame.context().executor)
        } else {
            frame.variables().remove(key)
            future.complete(r)
        }
    }


    companion object {

        fun parser() = ScriptParser.parser {
            ActionMap(it.nextToken(), it.run {
                expect("in")
                next(ArgTypes.ACTION)
            }, it.run {
                expect("with")
                next(ArgTypes.ACTION)
            })
        }
    }
}