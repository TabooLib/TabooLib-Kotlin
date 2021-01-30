package io.izzel.taboolib.kotlin.kether.action

import io.izzel.kether.common.api.ParsedAction
import io.izzel.kether.common.api.QuestAction
import io.izzel.kether.common.api.QuestContext
import io.izzel.kether.common.loader.types.ArgTypes
import io.izzel.taboolib.kotlin.kether.ScriptParser
import java.util.concurrent.CompletableFuture

/**
 * TabooLibKotlin
 * io.izzel.taboolib.kotlin.kether.action.ActionRange
 *
 * @author sky
 * @since 2021/1/30 9:26 下午
 */
class ActionArray(val list: List<ParsedAction<*>>) : QuestAction<List<Any>>() {

    override fun process(frame: QuestContext.Frame): CompletableFuture<List<Any>> {
        return CompletableFuture.completedFuture(list.map { frame.newFrame(it).run<Any>() }.toList())
    }

    override fun toString(): String {
        return "ActionArray(list=$list)"
    }

    companion object {

        /**
         * set a to array [  ]
         */
        fun parser() = ScriptParser.parser {
            ActionArray(it.next(ArgTypes.listOf(ArgTypes.ACTION)))
        }
    }
}