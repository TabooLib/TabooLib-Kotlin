package io.izzel.taboolib.kotlin.ketherx.action

import io.izzel.kether.common.api.QuestAction
import io.izzel.kether.common.api.QuestContext
import io.izzel.kether.common.loader.InferType
import io.izzel.taboolib.kotlin.ketherx.ScriptParser
import java.util.concurrent.CompletableFuture


/**
 * @author IzzelAliz
 */
class ActionRun(val run: InferType) : QuestAction<Any>() {

    override fun process(frame: QuestContext.Frame): CompletableFuture<Any> {
        return run.process(frame)
    }

    override fun toString(): String {
        return "ActionRun(run=$run)"
    }

    companion object {

        @Suppress("UnstableApiUsage")
        fun parser() = ScriptParser.parser {
            ActionRun(it.nextInferType())
        }
    }
}