package io.izzel.taboolib.kotlin.kether.action.supplier

import io.izzel.kether.common.api.QuestAction
import io.izzel.kether.common.api.QuestContext
import io.izzel.taboolib.kotlin.kether.KetherParser
import io.izzel.taboolib.kotlin.kether.ScriptParser
import java.util.concurrent.CompletableFuture


/**
 * @author IzzelAliz
 */
class ActionNull : QuestAction<Any?>() {

    override fun process(frame: QuestContext.Frame): CompletableFuture<Any?> {
        return CompletableFuture.completedFuture(null)
    }

    override fun toString(): String {
        return "ActionNull()"
    }

    companion object {

        @KetherParser(["null"])
        fun parser() = ScriptParser.parser {
            ActionNull()
        }
    }
}