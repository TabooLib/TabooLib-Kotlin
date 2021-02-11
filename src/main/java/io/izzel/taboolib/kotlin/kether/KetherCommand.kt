package io.izzel.taboolib.kotlin.kether

import io.izzel.kether.common.api.DefaultRegistry
import io.izzel.kether.common.api.QuestActionParser
import io.izzel.kether.common.util.LocalizedException
import io.izzel.taboolib.kotlin.Indexed
import io.izzel.taboolib.kotlin.Reflex
import io.izzel.taboolib.module.command.base.BaseCommand
import io.izzel.taboolib.module.command.base.BaseMainCommand
import io.izzel.taboolib.module.command.base.SubCommand
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.lang.NullPointerException

/**
 * TabooLibKotlin
 * io.izzel.taboolib.kotlin.kether.KetherComand
 *
 * @author sky
 * @since 2021/2/6 4:26 下午
 */
@BaseCommand(name = "TabooLibKether", aliases = ["tKether", "tk"], permission = "*")
class KetherCommand : BaseMainCommand() {

    @SubCommand(description = "Perform actions or enter the terminal", arguments = ["action?"])
    fun shell(sender: CommandSender, args: Array<String>) {
        when {
            args.isNotEmpty() -> {
                val time = System.currentTimeMillis()
                try {
                    KetherShell.eval(Indexed.join(args, 0, " "), false) {
                        this.sender = sender
                    }.thenApply {
                        sender.sendMessage("§8[§fTabooLib§8] §7Result: §f$it §8(${System.currentTimeMillis() - time}ms)")
                    }
                } catch (e: LocalizedException) {
                    sender.sendMessage("§8[§fTabooLib§8] §7Unexpected exception while parsing kether shell:")
                    e.localizedMessage?.split("\n")?.forEach {
                        sender.sendMessage("§8[§fTabooLib§8] §8${it}")
                    }
                } catch (e: Throwable) {
                    sender.sendMessage("§8[§fTabooLib§8] §7Unexpected exception while parsing kether shell.")
                    e.printStackTrace()
                }
            }
            sender is Player -> {
                KetherTerminal.open(sender)
            }
            else -> {
                sender.sendMessage("§8[§fTabooLib§8] §7Oops!")
            }
        }
    }

    @SubCommand(description = "Format inline script", arguments = ["text"])
    fun inline(sender: CommandSender, args: Array<String>) {
        try {
            val time = System.currentTimeMillis()
            val r = KetherFunction.parse(Indexed.join(args, 0, " "), cacheFunction = false, cacheScript = false) {
                this.sender = sender
            }
            sender.sendMessage("§8[§fTabooLib§8] §7Result: §f\"${r}\" §8(${System.currentTimeMillis() - time}ms)")
        } catch (e: Exception) {
            sender.sendMessage("§8[§fTabooLib§8] §7Unexpected exception while parsing inline script:")
            e.localizedMessage?.split("\n")?.forEach {
                sender.sendMessage("§8[§fTabooLib§8] §8${it}")
            }
        } catch (e: Throwable) {
            sender.sendMessage("§8[§fTabooLib§8] §7Unexpected exception while parsing inline script.")
            e.printStackTrace()
        }
    }

    @SubCommand(description = "Browse the list of actions")
    fun actions(sender: CommandSender, args: Array<String>) {
        /**
         * Total of 15 actions
         * 32 public
         * 1 private
         * Details:
         *   Original: []
         *   TrMenu: []
         */
        val parsers = Reflex.from(DefaultRegistry::class.java, Kether.registry).read<Map<String, Map<String, QuestActionParser>>>("parsers")!!
        sender.sendMessage("§8[§fTabooLib§8] §7Total of ${parsers.values.sumBy { it.size }} actions")
        sender.sendMessage("§8[§fTabooLib§8] §8  ${parsers["kether"]?.size} public")
        sender.sendMessage("§8[§fTabooLib§8] §8  ${parsers.filter { it.key != "kether" }.values.sumBy { it.size }} private")
        sender.sendMessage("§8[§fTabooLib§8] §7Details:")
        parsers.forEach { (k, v) ->
            sender.sendMessage("§8[§fTabooLib§8] §7  ${k}: §f${v.keys}")
        }
    }
}