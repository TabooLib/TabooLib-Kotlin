package io.izzel.taboolib.kotlin.kether.action.transform

import io.izzel.kether.common.api.ParsedAction
import io.izzel.kether.common.api.QuestAction
import io.izzel.kether.common.api.QuestContext
import io.izzel.kether.common.loader.types.ArgTypes
import io.izzel.taboolib.kotlin.kether.KetherParser
import io.izzel.taboolib.kotlin.kether.ScriptParser
import io.izzel.taboolib.kotlin.kether.inferType
import io.izzel.taboolib.util.Coerce
import java.util.concurrent.CompletableFuture


/**
 * @author IzzelAliz
 */
class ActionType(val any: String) : QuestAction<Any>() {

    override fun process(frame: QuestContext.Frame): CompletableFuture<Any> {
        return CompletableFuture.completedFuture(any.inferType())
    }

    override fun toString(): String {
        return "ActionType(any=$any)"
    }

    companion object {

        @KetherParser(["type"])
        fun parser() = ScriptParser.parser {
            ActionType(it.nextToken())
        }
    }
}