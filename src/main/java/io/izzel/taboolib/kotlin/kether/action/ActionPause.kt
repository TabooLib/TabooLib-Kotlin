package io.izzel.taboolib.kotlin.kether.action

import io.izzel.kether.common.api.QuestAction
import io.izzel.kether.common.api.QuestContext
import io.izzel.taboolib.kotlin.kether.KetherParser
import io.izzel.taboolib.kotlin.kether.ScriptParser
import java.util.concurrent.CompletableFuture


/**
 * @author IzzelAliz
 */
class ActionPause : QuestAction<Void>() {

    override fun process(context: QuestContext.Frame): CompletableFuture<Void> {
        return CompletableFuture<Void>()
    }

    override fun toString(): String {
        return "ActionPause()"
    }

    companion object {

        @KetherParser(["pause"])
        fun parser() = ScriptParser.parser {
            ActionPause()
        }
    }
}