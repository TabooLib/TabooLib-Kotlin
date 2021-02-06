package io.izzel.taboolib.kotlin.kether.action

import io.izzel.kether.common.api.ParsedAction
import io.izzel.kether.common.api.QuestAction
import io.izzel.kether.common.api.QuestContext
import io.izzel.kether.common.loader.types.ArgTypes
import io.izzel.taboolib.kotlin.kether.KetherParser
import io.izzel.taboolib.kotlin.kether.ScriptParser
import java.util.concurrent.CompletableFuture


/**
 * @author IzzelAliz
 */
class ActionRun(val run: ParsedAction<*>) : QuestAction<Any>() {

    override fun process(frame: QuestContext.Frame): CompletableFuture<Any> {
        return frame.newFrame(run).run()
    }

    override fun toString(): String {
        return "ActionRun(run=$run)"
    }

    companion object {

        @KetherParser(["run"])
        fun parser() = ScriptParser.parser {
            ActionRun(it.next(ArgTypes.ACTION))
        }
    }
}