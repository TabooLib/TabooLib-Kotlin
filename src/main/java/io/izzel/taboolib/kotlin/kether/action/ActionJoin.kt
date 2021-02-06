package io.izzel.taboolib.kotlin.kether.action

import io.izzel.kether.common.api.ParsedAction
import io.izzel.kether.common.api.QuestAction
import io.izzel.kether.common.api.QuestContext
import io.izzel.kether.common.loader.types.ArgTypes
import io.izzel.taboolib.kotlin.kether.Kether.expects
import io.izzel.taboolib.kotlin.kether.KetherParser
import io.izzel.taboolib.kotlin.kether.ScriptParser
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionJoin(val source: List<ParsedAction<*>>, val separator: String) : QuestAction<String>() {

    override fun process(context: QuestContext.Frame): CompletableFuture<String> {
        val future = CompletableFuture<String>()
        process(context, future, 0, source, ArrayList())
        return future;
    }

    fun process(frame: QuestContext.Frame, future: CompletableFuture<String>, cur: Int, i: List<ParsedAction<*>>, array: ArrayList<Any>) {
        if (cur < i.size) {
            frame.newFrame(i[cur]).run<Any>().thenAcceptAsync({
                array.add(it)
                process(frame, future, cur + 1, i, array)
            }, frame.context().executor)
        } else {
            future.complete(array.joinToString(separator))
        }
    }

    override fun toString(): String {
        return "ActionJoin(source='$source')"
    }

    companion object {

        /**
         * join [ *1 *2 *3 ] by -
         * join [ *a *b *c ] with -
         */
        @KetherParser(["join"])
        fun parser() = ScriptParser.parser {
            val source = it.next(ArgTypes.listOf(ArgTypes.ACTION))
            it.mark()
            ActionJoin(
                source, try {
                    it.expects("by", "with")
                    it.nextToken()
                } catch (ignored: Exception) {
                    it.reset()
                    " "
                }
            )
        }
    }
}