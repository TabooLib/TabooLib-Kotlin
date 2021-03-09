package io.izzel.taboolib.kotlin.kether.action.loop

import io.izzel.kether.common.api.QuestAction
import io.izzel.kether.common.api.QuestContext
import io.izzel.taboolib.kotlin.kether.KetherParser
import io.izzel.taboolib.kotlin.kether.ScriptParser
import io.izzel.taboolib.kotlin.kether.script
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionBreak : QuestAction<Void>() {

    override fun process(context: QuestContext.Frame): CompletableFuture<Void> {
        context.script().breakLoop = true
        return CompletableFuture.completedFuture(null)
    }

    override fun toString(): String {
        return "ActionBreak()"
    }

    companion object {

        @KetherParser(["break"])
        fun parser() = ScriptParser.parser {
            ActionBreak()
        }
    }
}