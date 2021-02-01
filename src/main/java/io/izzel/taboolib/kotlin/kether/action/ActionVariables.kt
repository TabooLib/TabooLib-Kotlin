package io.izzel.taboolib.kotlin.kether.action

import io.izzel.kether.common.api.QuestAction
import io.izzel.kether.common.api.QuestContext
import io.izzel.taboolib.kotlin.kether.ScriptParser
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionVariables() : QuestAction<List<String>>() {

    override fun process(context: QuestContext.Frame): CompletableFuture<List<String>> {
        return CompletableFuture.completedFuture(context.variables().keys().toList())
    }

    override fun toString(): String {
        return "ActionVariables()"
    }

    companion object {

        @Suppress("UnstableApiUsage")
        fun parser() = ScriptParser.parser {
            ActionVariables()
        }
    }
}