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
class ActionNull : QuestAction<Any?>() {

    override fun process(frame: QuestContext.Frame): CompletableFuture<Any?> {
        return CompletableFuture.completedFuture(null)
    }

    override fun toString(): String {
        return "ActionNull()"
    }

    companion object {

        fun parser() = ScriptParser.parser {
            ActionNull()
        }
    }
}