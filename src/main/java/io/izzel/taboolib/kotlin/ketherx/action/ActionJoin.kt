package io.izzel.taboolib.kotlin.ketherx.action

import io.izzel.kether.common.api.QuestAction
import io.izzel.kether.common.api.QuestContext
import io.izzel.kether.common.loader.InferType
import io.izzel.taboolib.kotlin.ketherx.ScriptParser
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionJoin(val source: List<InferType>, val separator: String) : QuestAction<String>() {

    override fun process(context: QuestContext.Frame): CompletableFuture<String> {
        return CompletableFuture.completedFuture(source.joinToString(separator) {
            it.process(context).get().toString()
        })
    }

    override fun toString(): String {
        return "ActionJoin(source='$source')"
    }

    companion object {

        @Suppress("UnstableApiUsage")
        fun parser() = ScriptParser.parser {
            val source = it.nextInferList()
            it.mark()
            ActionJoin(
                source, try {
                    it.expect("by")
                    it.nextToken()
                } catch (ignored: Exception) {
                    it.reset()
                    " "
                }
            )
        }
    }
}