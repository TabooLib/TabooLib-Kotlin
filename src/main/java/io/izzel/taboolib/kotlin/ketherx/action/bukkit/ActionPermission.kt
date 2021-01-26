package io.izzel.taboolib.kotlin.ketherx.action.bukkit

import io.izzel.kether.common.api.QuestAction
import io.izzel.kether.common.api.QuestContext
import io.izzel.kether.common.loader.InferType
import io.izzel.taboolib.kotlin.ketherx.ScriptContext
import io.izzel.taboolib.kotlin.ketherx.ScriptParser
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionPermission(val permission: InferType) : QuestAction<Boolean>() {

    override fun process(context: QuestContext.Frame): CompletableFuture<Boolean> {
        return permission.process(context).thenApply {
            (context.context() as ScriptContext).sender!!.hasPermission(it.toString())
        }
    }

    override fun toString(): String {
        return "ActionPermission(permission='$permission')"
    }

    companion object {

        @Suppress("UnstableApiUsage")
        fun parser() = ScriptParser.parser {
            ActionPermission(it.nextInferType())
        }
    }
}