package io.izzel.taboolib.kotlin.kether.action

import io.izzel.kether.common.api.ParsedAction
import io.izzel.kether.common.api.QuestAction
import io.izzel.kether.common.api.QuestContext
import io.izzel.kether.common.loader.types.ArgTypes
import io.izzel.taboolib.kotlin.kether.KetherParser
import io.izzel.taboolib.kotlin.kether.ScriptParser
import io.izzel.taboolib.util.lite.Numbers
import java.util.concurrent.CompletableFuture
import kotlin.random.Random

/**
 * TabooLibKotlin
 * io.izzel.taboolib.kotlin.kether.action.ActionRange
 *
 * @author sky
 * @since 2021/1/30 9:26 下午
 */
class ActionRandom(val from: Double, val to: Double, val action: ParsedAction<*>? = null) : QuestAction<Any?>() {

    override fun process(frame: QuestContext.Frame): CompletableFuture<Any?> {
        if (action == null) {
            return CompletableFuture.completedFuture(Numbers.getRandomDouble(from, to))
        } else {
            val future = CompletableFuture<Any?>()
            frame.newFrame(action).run<Any?>().thenAcceptAsync( {
                when (it) {
                    is Collection<*> -> {
                        random(future, it.map { i -> i as Any }.toList())
                    }
                    is Array<*> -> {
                        random(future, it.map { i -> i as Any }.toList())
                    }
                    else -> {
                        random(future, listOf(it))
                    }
                }
            }, frame.context().executor)
            return future
        }
    }

    fun random(future: CompletableFuture<Any?>, i: List<Any>) {
        future.complete(if (i.isEmpty()) null else i[Random.nextInt(i.size)])
    }

    override fun toString(): String {
        return "ActionRandom(from=$from, to=$to, action=$action)"
    }

    companion object {

        /**
         * random 1 to 10
         * random players
         * random range 1 to 10
         */
        @KetherParser(["random"])
        fun parser() = ScriptParser.parser {
            it.mark()
            try {
                var from = 0.0
                var to = it.nextDouble()
                it.mark()
                try {
                    it.expect("to")
                    it.nextDouble().run {
                        from = to
                        to = this
                    }
                } catch (ignored: Exception) {
                    it.reset()
                }
                ActionRandom(from, to)
            } catch (ignored: Exception) {
                it.reset()
                ActionRandom(0.0, 0.0, it.next(ArgTypes.ACTION))
            }
        }
    }
}