package io.izzel.taboolib.kotlin.kether.action.transform

import io.izzel.kether.common.api.ParsedAction
import io.izzel.kether.common.api.QuestAction
import io.izzel.kether.common.api.QuestContext
import io.izzel.kether.common.loader.types.ArgTypes
import io.izzel.taboolib.cronus.CronusUtils
import io.izzel.taboolib.kotlin.*
import io.izzel.taboolib.kotlin.kether.Kether.expects
import io.izzel.taboolib.kotlin.kether.KetherParser
import io.izzel.taboolib.kotlin.kether.ScriptParser
import io.izzel.taboolib.kotlin.kether.script
import io.izzel.taboolib.util.Coerce
import java.util.concurrent.CompletableFuture

/**
 * ink.ptms.zaphkiel.module.kether.ActionMath
 *
 * @author sky
 * @since 2021/3/16 2:56 下午
 */
class ActionMath(val type: Type, val array: List<ParsedAction<*>>) : QuestAction<Number>() {

    enum class Type(val exec: List<Any>.() -> Number) {

        ADD({
            if (intAll()) {
                sumBy { Coerce.toInteger(it) }
            } else {
                sumByDouble { Coerce.toDouble(it) }
            }
        }),

        SUB({
            if (intAll()) {
                subBy { Coerce.toInteger(it) }
            } else {
                subByDouble { Coerce.toDouble(it) }
            }
        }),

        MUL({
            if (intAll()) {
                mulBy { Coerce.toInteger(it) }
            } else {
                mulByDouble { Coerce.toDouble(it) }
            }
        }),

        DIV({
            if (intAll()) {
                divBy { Coerce.toInteger(it) }
            } else {
                divByDouble { Coerce.toDouble(it) }
            }
        });

        companion object {

            fun List<Any>.intAll() = all { it is Int || CronusUtils.isInt(it.toString()) }
        }
    }

    override fun process(frame: QuestContext.Frame): CompletableFuture<Number> {
        val future = CompletableFuture<Number>()
        val number = ArrayList<Any>()
        fun process(cur: Int) {
            if (cur < array.size) {
                frame.newFrame(array[cur]).run<Any>().thenApply {
                    number.add(it)
                    if (frame.script().breakLoop) {
                        frame.script().breakLoop = false
                        future.complete(type.exec(number))
                    } else {
                        process(cur + 1)
                    }
                }
            } else {
                future.complete(type.exec(number))
            }
        }
        return future
    }

    companion object {

        /**
         * math + [ 1 2 3 ]
         */
        @KetherParser(["math"])
        fun parser0() = ScriptParser.parser {
            it.next(ArgTypes.listOf(ArgTypes.ACTION))
            ActionMath(when (it.expects(
                "add", "plus", "+",
                "sub", "minus", "-",
                "mul", "times", "*",
                "div", "divided", "/"
            )) {
                "add", "plus", "+" -> Type.ADD
                "sub", "minus", "-" -> Type.SUB
                "mul", "times", "*" -> Type.MUL
                "div", "divided", "/" -> Type.DIV
                else -> error("out of case")
            }, it.next(ArgTypes.listOf(ArgTypes.ACTION)))
        }
    }
}