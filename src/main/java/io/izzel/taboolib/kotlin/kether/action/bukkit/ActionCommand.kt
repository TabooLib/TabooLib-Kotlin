package io.izzel.taboolib.kotlin.kether.action.bukkit

import io.izzel.kether.common.api.ParsedAction
import io.izzel.kether.common.api.QuestAction
import io.izzel.kether.common.api.QuestContext
import io.izzel.kether.common.loader.types.ArgTypes
import io.izzel.kether.common.util.LocalizedException
import io.izzel.taboolib.kotlin.kether.ScriptContext
import io.izzel.taboolib.kotlin.kether.ScriptParser
import io.izzel.taboolib.util.Features
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionCommand(val command: ParsedAction<*>, val type: Type) : QuestAction<Void>() {

    enum class Type {

        PLAYER, PLAYER_OP, CONSOLE
    }

    override fun process(context: QuestContext.Frame): CompletableFuture<Void> {
        return context.newFrame(command).run<Any>().thenAccept {
            val command = it.toString().trimIndent()
            when (type) {
                Type.PLAYER -> {
                    val viewer = (context.context() as ScriptContext).sender ?: throw RuntimeException("No sender selected.")
                    Features.dispatchCommand(viewer, command.replace("@sender", viewer.name))
                }
                Type.PLAYER_OP -> {
                    val viewer = (context.context() as ScriptContext).sender ?: throw RuntimeException("No sender selected.")
                    Features.dispatchCommand(viewer, command.replace("@sender", viewer.name), true)
                }
                Type.CONSOLE -> {
                    val viewer = (context.context() as ScriptContext).sender?.name.toString()
                    Features.dispatchCommand(command.replace("@sender", viewer))
                }
            }
        }
    }

    override fun toString(): String {
        return "ActionCommand{" +
                "command='" + command + '\'' +
                '}'
    }

    companion object {

        @Suppress("UnstableApiUsage")
        fun parser() = ScriptParser.parser {
            val command = it.next(ArgTypes.ACTION)
            it.mark()
            val by = try {
                it.expect("by")
                when (it.nextToken()) {
                    "player" -> Type.PLAYER
                    "player_op", "op" -> Type.PLAYER_OP
                    "console", "server" -> Type.CONSOLE
                    else -> throw LocalizedException.of("load-error.not-command-type", it)
                }
            } catch (ignored: Exception) {
                it.reset()
                Type.PLAYER
            }
            ActionCommand(command, by)
        }
    }
}