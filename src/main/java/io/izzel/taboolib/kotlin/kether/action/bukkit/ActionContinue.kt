package io.izzel.taboolib.kotlin.kether.action.bukkit

import io.izzel.kether.common.api.QuestAction
import io.izzel.kether.common.api.QuestContext
import io.izzel.taboolib.kotlin.kether.KetherParser
import io.izzel.taboolib.kotlin.kether.ScriptContext
import io.izzel.taboolib.kotlin.kether.ScriptParser
import java.util.concurrent.CompletableFuture


/**
 * @author IzzelAliz
 */
class ActionContinue : QuestAction<Void>() {

    override fun process(context: QuestContext.Frame): CompletableFuture<Void> {
        val s = context.context() as ScriptContext
        s.listener?.complete(null)
        s.listener = null
        return CompletableFuture.completedFuture(null)
    }

    override fun toString(): String {
        return "ActionContinue()"
    }

    companion object {

        @KetherParser(["continue"])
        fun parser() = ScriptParser.parser {
            ActionContinue()
        }
    }
}