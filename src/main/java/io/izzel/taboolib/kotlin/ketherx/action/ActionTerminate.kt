package io.izzel.taboolib.kotlin.ketherx.action

import io.izzel.kether.common.api.QuestAction
import io.izzel.kether.common.api.QuestContext
import io.izzel.taboolib.kotlin.ketherx.ScriptContext
import io.izzel.taboolib.kotlin.ketherx.ScriptParser
import io.izzel.taboolib.kotlin.ketherx.ScriptService
import java.util.concurrent.CompletableFuture


/**
 * @author IzzelAliz
 */
class ActionTerminate : QuestAction<Void>() {

    override fun process(context: QuestContext.Frame): CompletableFuture<Void> {
        ScriptService.terminateQuest(context.context() as ScriptContext)
        return CompletableFuture.completedFuture(null)
    }

    override fun toString(): String {
        return "ActionTerminate()"
    }

    companion object {

        fun parser() = ScriptParser.parser {
            ActionTerminate()
        }
    }
}