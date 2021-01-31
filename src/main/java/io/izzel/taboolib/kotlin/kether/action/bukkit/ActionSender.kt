package io.izzel.taboolib.kotlin.kether.action.bukkit

import io.izzel.kether.common.api.ParsedAction
import io.izzel.kether.common.api.QuestAction
import io.izzel.kether.common.api.QuestContext
import io.izzel.kether.common.loader.types.ArgTypes
import io.izzel.taboolib.kotlin.kether.ScriptContext
import io.izzel.taboolib.kotlin.kether.ScriptParser
import io.izzel.taboolib.module.locale.TLocale
import org.bukkit.Bukkit
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionSender : QuestAction<String>() {

    override fun process(context: QuestContext.Frame): CompletableFuture<String> {
        val sender = (context as ScriptContext).sender
        if (sender is ConsoleCommandSender) {
            return CompletableFuture.completedFuture("console")
        } else {
            return CompletableFuture.completedFuture(sender?.name ?: "null")
        }
    }

    override fun toString(): String {
        return "ActionSender()"
    }

    companion object {

        @Suppress("UnstableApiUsage")
        fun parser() = ScriptParser.parser {
            ActionSender()
        }
    }
}