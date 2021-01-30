package io.izzel.taboolib.kotlin.kether.action

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
class ActionRange(val from: Int, val to: Int) : QuestAction<List<Int>>() {

    override fun process(frame: QuestContext.Frame): CompletableFuture<List<Int>> {
        return CompletableFuture.completedFuture((from until to).toList())
    }

    override fun toString(): String {
        return "ActionRange(from=$from, to=$to)"
    }

    companion object {

        fun parser() = ScriptParser.parser {
            ActionRange(it.nextInt(), it.run {
                expect("to")
                nextInt()
            })
        }
    }
}