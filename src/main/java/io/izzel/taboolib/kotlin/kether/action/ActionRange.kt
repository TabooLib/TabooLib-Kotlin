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
class ActionRange(val from: Double, val to: Double, val step: Double = 0.0) : QuestAction<List<Any>>() {

    override fun process(frame: QuestContext.Frame): CompletableFuture<List<Any>> {
        return if (step == 0.0) {
            CompletableFuture.completedFuture((from.toInt()..to.toInt()).toList())
        } else {
            val intStep = step.toInt().toDouble() == step
            val array = ArrayList<Any>()
            var i = from
            while (i <= to) {
                array.add(if (intStep) i.toInt() else i)
                i += step
            }
            CompletableFuture.completedFuture(array)
        }
    }

    override fun toString(): String {
        return "ActionRange(from=$from, to=$to)"
    }

    companion object {

        fun parser() = ScriptParser.parser {
            val from = it.nextDouble()
            it.expect("to")
            val to = it.nextDouble()
            it.mark()
            val step = try {
                it.expect("step")
                it.nextDouble()
            } catch (ignored: Exception) {
                it.reset()
                0.0
            }
            ActionRange(from, to, step)
        }
    }
}