package io.izzel.taboolib.kotlin.kether.action

import io.izzel.kether.common.api.QuestAction
import io.izzel.kether.common.api.QuestContext
import io.izzel.taboolib.kotlin.Reflex.Companion.reflex
import io.izzel.taboolib.kotlin.kether.KetherParser
import io.izzel.taboolib.kotlin.kether.ScriptParser
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionImport : QuestAction<Void>() {

    override fun process(context: QuestContext.Frame): CompletableFuture<Void> {
        return CompletableFuture.completedFuture(null)
    }

    override fun toString(): String {
        return "ActionImport()"
    }

    companion object {

        @KetherParser(["import"])
        fun parser0() = ScriptParser.parser {
            it.reflex<MutableList<String>>("namespace")!!.add(it.nextToken())
            ActionImport()
        }

        @KetherParser(["release"])
        fun parser1() = ScriptParser.parser {
            it.reflex<MutableList<String>>("namespace")!!.remove(it.nextToken())
            ActionImport()
        }
    }
}