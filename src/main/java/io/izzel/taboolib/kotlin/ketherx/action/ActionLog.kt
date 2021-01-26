package io.izzel.taboolib.kotlin.ketherx.action

import io.izzel.kether.common.api.QuestAction
import io.izzel.kether.common.api.QuestContext
import io.izzel.kether.common.loader.InferType
import io.izzel.taboolib.kotlin.ketherx.ScriptContext
import io.izzel.taboolib.kotlin.ketherx.ScriptParser
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionLog(val message: InferType) : QuestAction<Void>() {

    override fun process(context: QuestContext.Frame): CompletableFuture<Void> {
        return message.process(context).thenAccept {
            if (context.context() is ScriptContext) {
                val player = (context.context() as ScriptContext).sender?.name.toString()
                println("[Adyeshach] ${it.toString().trimIndent().replace("@sender", player)}")
            } else {
                println("[Adyeshach] ${it.toString().trimIndent()}")
            }
        }
    }

    override fun toString(): String {
        return "ActionLog{" +
                "message='" + message + '\'' +
                '}'
    }

    companion object {

        @Suppress("UnstableApiUsage")
        fun parser() = ScriptParser.parser {
            ActionLog(it.nextInferType())
        }
    }
}