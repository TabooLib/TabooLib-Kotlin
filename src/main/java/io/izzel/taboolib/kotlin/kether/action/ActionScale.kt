package io.izzel.taboolib.kotlin.kether.action

import io.izzel.kether.common.api.ParsedAction
import io.izzel.kether.common.api.QuestAction
import io.izzel.kether.common.api.QuestContext
import io.izzel.kether.common.loader.types.ArgTypes
import io.izzel.taboolib.kotlin.kether.KetherParser
import io.izzel.taboolib.kotlin.kether.ScriptParser
import io.izzel.taboolib.util.Coerce
import java.util.concurrent.CompletableFuture


/**
 * @author IzzelAliz
 */
class ActionScale(val number: ParsedAction<*>) : QuestAction<Double>() {

    override fun process(frame: QuestContext.Frame): CompletableFuture<Double> {
        return frame.newFrame(number).run<Any>().thenApply {
            Coerce.format(Coerce.toDouble(it))
        }
    }

    override fun toString(): String {
        return "ActionScale(number=$number)"
    }

    companion object {

        @KetherParser(["scale", "scaled"])
        fun parser() = ScriptParser.parser {
            ActionScale(it.next(ArgTypes.ACTION))
        }
    }
}