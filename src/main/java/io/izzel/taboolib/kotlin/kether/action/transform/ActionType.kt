package io.izzel.taboolib.kotlin.kether.action.transform

import io.izzel.kether.common.api.ParsedAction
import io.izzel.kether.common.api.QuestAction
import io.izzel.kether.common.api.QuestContext
import io.izzel.kether.common.loader.types.ArgTypes
import io.izzel.taboolib.kotlin.kether.Kether.expects
import io.izzel.taboolib.kotlin.kether.KetherParser
import io.izzel.taboolib.kotlin.kether.ScriptParser
import io.izzel.taboolib.kotlin.kether.inferType
import java.util.concurrent.CompletableFuture


/**
 * @author IzzelAliz
 */
class ActionType {

    class ActionType(val any: String) : QuestAction<Any>() {

        override fun process(frame: QuestContext.Frame): CompletableFuture<Any> {
            return CompletableFuture.completedFuture(any.inferType())
        }

        override fun toString(): String {
            return "ActionType(any=$any)"
        }
    }

    class ActionTypeTo(val to: TypeTo, val action: ParsedAction<*>) : QuestAction<Any>() {

        override fun process(frame: QuestContext.Frame): CompletableFuture<Any> {
            return frame.newFrame(action).run<Any>().thenApply { to.transfer(it) }
        }

        override fun toString(): String {
            return "ActionTypeTo(action=$action, to=$to)"
        }
    }

    enum class TypeTo(val transfer: (Any) -> Any) {

        INT({
            io.izzel.taboolib.util.Coerce.toInteger(it)
        }),

        LONG({
            io.izzel.taboolib.util.Coerce.toLong(it)
        }),

        FLOAT({
            io.izzel.taboolib.util.Coerce.toFloat(it)
        }),

        DOUBLE({
            io.izzel.taboolib.util.Coerce.toDouble(it)
        }),

        BOOLEAN({
            io.izzel.taboolib.util.Coerce.toInteger(it)
        })
    }

    companion object {

        val types = TypeTo.values().map { it.name.toLowerCase() }.toTypedArray()

        @KetherParser(["type"])
        fun parser() = ScriptParser.parser {
            try {
                it.mark()
                ActionTypeTo(TypeTo.valueOf(it.expects(*types).toUpperCase()), it.next(ArgTypes.ACTION))
            } catch (ex: Throwable) {
                it.reset()
                ActionType(it.nextToken())
            }
        }
    }
}