package io.izzel.taboolib.kotlin.kether.action

import io.izzel.kether.common.api.QuestAction
import io.izzel.kether.common.api.QuestContext
import io.izzel.taboolib.kotlin.kether.KetherParser
import io.izzel.taboolib.kotlin.kether.ScriptContext
import io.izzel.taboolib.kotlin.kether.ScriptParser
import io.izzel.taboolib.kotlin.kether.ScriptService
import java.util.concurrent.CompletableFuture


/**
 * @author IzzelAliz
 */
class ActionTerminate : QuestAction<Void>() {

    override fun process(context: QuestContext.Frame): CompletableFuture<Void> {
        ScriptService.INSTANCE.terminateQuest(context.context() as ScriptContext)
        return CompletableFuture.completedFuture(null)
    }

    override fun toString(): String {
        return "ActionTerminate()"
    }

    companion object {

        @KetherParser(["exit", "stop", "terminate"])
        fun parser() = ScriptParser.parser {
            ActionTerminate()
        }
    }
}