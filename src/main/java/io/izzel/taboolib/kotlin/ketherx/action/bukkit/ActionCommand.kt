package io.izzel.taboolib.kotlin.ketherx.action.bukkit

import io.izzel.kether.common.api.QuestAction
import io.izzel.kether.common.api.QuestContext
import io.izzel.kether.common.loader.InferType
import io.izzel.kether.common.util.LocalizedException
import io.izzel.taboolib.kotlin.ketherx.ScriptContext
import io.izzel.taboolib.kotlin.ketherx.ScriptParser
import io.izzel.taboolib.util.Features
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionCommand(val command: InferType, val type: Type) : QuestAction<Void>() {

    enum class Type {

        PLAYER, PLAYER_OP, CONSOLE
    }

    override fun process(context: QuestContext.Frame): CompletableFuture<Void> {
        return command.process(context).thenAccept {
            val command = it.toString().trimIndent()
            when (type) {
                Type.PLAYER -> {
                    val viewer = (context.context() as ScriptContext).sender ?: throw RuntimeException("No viewer selected.")
                    Features.dispatchCommand(viewer, command.replace("@sender", viewer.name))
                }
                Type.PLAYER_OP -> {
                    val viewer = (context.context() as ScriptContext).sender ?: throw RuntimeException("No viewer selected.")
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
            val command = it.nextInferType()
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