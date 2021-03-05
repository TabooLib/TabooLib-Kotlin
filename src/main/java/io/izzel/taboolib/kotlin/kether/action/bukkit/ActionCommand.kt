package io.izzel.taboolib.kotlin.kether.action.bukkit

import io.izzel.kether.common.api.ParsedAction
import io.izzel.kether.common.api.QuestAction
import io.izzel.kether.common.api.QuestContext
import io.izzel.kether.common.loader.types.ArgTypes
import io.izzel.taboolib.kotlin.kether.KetherParser
import io.izzel.taboolib.kotlin.kether.KetherError
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
        return context.newFrame(command).run<Any>().thenAcceptAsync( {
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
        }, context.context().executor)
    }

    override fun toString(): String {
        return "ActionCommand{" +
                "command='" + command + '\'' +
                '}'
    }

    companion object {

        @KetherParser(["command"])
        fun parser() = ScriptParser.parser {
            val command = it.next(ArgTypes.ACTION)
            it.mark()
            val by = try {
                it.expect("by")
                when (val type = it.nextToken()) {
                    "player" -> Type.PLAYER
                    "player_op", "op" -> Type.PLAYER_OP
                    "console", "server" -> Type.CONSOLE
                    else -> throw KetherError.NOT_COMMAND_SENDER.create(type)
                }
            } catch (ignored: Exception) {
                it.reset()
                Type.PLAYER
            }
            ActionCommand(command, by)
        }
    }
}