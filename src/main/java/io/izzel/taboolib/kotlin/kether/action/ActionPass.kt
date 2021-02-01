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
class ActionPass : QuestAction<Void>() {

    override fun process(frame: QuestContext.Frame): CompletableFuture<Void> {
        return CompletableFuture.completedFuture(null)
    }

    override fun toString(): String {
        return "ActionPass()"
    }

    companion object {

        fun parser() = ScriptParser.parser {
            ActionPass()
        }
    }
}