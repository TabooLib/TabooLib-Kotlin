package io.izzel.taboolib.kotlin.kether.action

import io.izzel.kether.common.api.ParsedAction
import io.izzel.kether.common.api.QuestAction
import io.izzel.kether.common.api.QuestContext
import io.izzel.kether.common.loader.types.ArgTypes
import io.izzel.taboolib.kotlin.kether.KetherParser
import io.izzel.taboolib.kotlin.kether.ScriptParser
import io.izzel.taboolib.util.Coerce
import java.util.concurrent.CompletableFuture
import kotlin.math.roundToInt


/**
 * @author IzzelAliz
 */
class ActionRound(val number: ParsedAction<*>) : QuestAction<Int>() {

    override fun process(frame: QuestContext.Frame): CompletableFuture<Int> {
        return frame.newFrame(number).run<Any>().thenApply {
            Coerce.toDouble(it).roundToInt()
        }
    }

    override fun toString(): String {
        return "ActionRound(number=$number)"
    }

    companion object {

        /**
         * round *1.0
         */
        @KetherParser(["round"])
        fun parser() = ScriptParser.parser {
            ActionRound(it.next(ArgTypes.ACTION))
        }
    }
}