package io.izzel.taboolib.kotlin.kether

import io.izzel.kether.common.actions.KetherTypes
import io.izzel.kether.common.api.QuestActionParser
import io.izzel.taboolib.TabooLib
import io.izzel.taboolib.kotlin.Indexed.join
import io.izzel.taboolib.kotlin.kether.action.*
import io.izzel.taboolib.kotlin.kether.action.bukkit.*
import io.izzel.taboolib.module.command.lite.CommandBuilder
import io.izzel.taboolib.module.inject.TFunction
import io.izzel.taboolib.util.Coerce
import org.bukkit.event.Event
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

object Kether {

    val registry = ScriptService.INSTANCE.registry.also {
        KetherTypes.registerInternals(it, ScriptService.INSTANCE)
    }

    val operatorMap = ConcurrentHashMap<String, EventOperator<out Event>>()

    @TFunction.Init
    @JvmStatic
    fun init() {
        addAction("run", ActionRun.parser())
        addAction("for", ActionForEach.parser())
        addAction("map", ActionMap.parser())
        addAction("pass", ActionPass.parser())
        addAction("join", ActionJoin.parser())
        addAction("check", ActionCheck.parser())
        addAction("pause", ActionPause.parser())
        addAction("array", ActionArray.parser())
        addAction("range", ActionRange.parser())
        addAction("random", ActionRandom.parser())
        addAction("variables", ActionVariables.parser())
        addAction(arrayOf("log", "print"), ActionLog.parser())
        addAction(arrayOf("wait", "sleep"), ActionWait.parser())
        addAction(arrayOf("exit", "terminate"), ActionTerminate.parser())
        addAction(arrayOf("inline", "function"), ActionFunction.parser())
        addAction(arrayOf("$", "js", "javascript"), ActionJavaScript.parser())
        // bukkit
        addAction("tell", ActionTell.parser())
        addAction("color", ActionColor.parser())
        addAction("title", ActionTitle.parser())
        addAction("sound", ActionSound.parser())
        addAction("event", ActionEvent.parser())
        addAction("sender", ActionSender.parser())
        addAction("switch", ActionSwitch.parser())
        addAction("listen", ActionListen.parser())
        addAction("players", ActionPlayers.parser())
        addAction("command", ActionCommand.parser())
        addAction("continue", ActionContinue.parser())
        addAction("subtitle", ActionSubtitle.parser())
        addAction("actionbar", ActionActionBar.parser())
        addAction(arrayOf("perm", "permission"), ActionPermission.parser())
        addAction(arrayOf("papi", "placeholder"), ActionPlaceholder.parser())
        // events
        addEventOperator("join", PlayerJoinEvent::class) {
            unit("player") {
                reader = { it.player.name }
            }
            unit("message") {
                reader = { it.joinMessage }
                writer = { k, v -> k.joinMessage = v.toString() }
            }
        }
        addEventOperator("quit", PlayerQuitEvent::class) {
            unit("player") {
                reader = { it.player.name }
            }
            unit("message") {
                reader = { it.quitMessage }
                writer = { k, v -> k.quitMessage = v.toString() }
            }
        }
        addEventOperator("chat", AsyncPlayerChatEvent::class) {
            unit("player") {
                reader = { it.player.name }
            }
            unit("message") {
                reader = { it.message }
                writer = { k, v -> k.message = v.toString() }
            }
            unit("cancelled") {
                reader = { it.isCancelled }
                writer = { k, v -> k.isCancelled = Coerce.toBoolean(v) }
            }
        }
        addEventOperator("command", PlayerCommandPreprocessEvent::class) {
            unit("player") {
                reader = { it.player.name }
            }
            unit("command") {
                reader = { it.message }
                writer = { k, v -> k.message = v.toString() }
            }
            unit("cancelled") {
                reader = { it.isCancelled }
                writer = { k, v -> k.isCancelled = Coerce.toBoolean(v) }
            }
        }
        CommandBuilder.create("tkether", TabooLib.getPlugin())
            .permission("*")
            .aliases("tk")
            .execute { sender, args ->
                if (args.isEmpty()) {
                    sender.sendMessage("§8[§fTabooLib§8] §7Usage: §8/tkether shell [shell]")
                    sender.sendMessage("§8[§fTabooLib§8] §7Usage: §8/tkether inline [text]")
                } else if (args[0] == "shell" && args.size > 1) {
                    val time = System.currentTimeMillis()
                    try {
                        ScriptContext.create(ScriptLoader.load("def main = { ${join(args, 1, " ")} }")) {
                            this.sender = sender
                        }.runActions().thenAccept {
                            sender.sendMessage("§8[§fTabooLib§8] §7Result: §f${it} §8(${System.currentTimeMillis() - time}ms)")
                        }
                    } catch (e: Exception) {
                        sender.sendMessage("§8[§fTabooLib§8] §7Unexpected exception while parsing kether shell:")
                        e.localizedMessage?.split("\n")?.forEach {
                            sender.sendMessage("§8[§fTabooLib§8] §8${it}")
                        }
                    }
                } else if (args[0] == "inline") {
                    try {
                        val time = System.currentTimeMillis()
                        val r = KetherFunction.parse(join(args, 1, " "), cacheFunction = false, cacheScript = false) {
                            this.sender = sender
                        }
                        sender.sendMessage("§8[§fTabooLib§8] §7Result: §f\"${r}\" §8(${System.currentTimeMillis() - time}ms)")
                    } catch (e: Exception) {
                        sender.sendMessage("§8[§fTabooLib§8] §7Unexpected exception while parsing inline script:")
                        e.localizedMessage?.split("\n")?.forEach {
                            sender.sendMessage("§8[§fTabooLib§8] §8${it}")
                        }
                    }
                } else {
                    sender.sendMessage("§8[§fTabooLib§8] §7Oops!")
                }
            }.build()
    }

    fun addAction(name: Array<String>, parser: QuestActionParser) {
        name.forEach { registry.registerAction(it, parser) }
    }

    fun addAction(name: String, parser: QuestActionParser, namespace: String? = null) {
        registry.registerAction(namespace ?: "kether", name, parser)
    }

    fun <T : Event> addEventOperator(name: String, event: KClass<out T>, func: EventOperator<T>.() -> Unit = {}) {
        operatorMap[name] = EventOperator(event).also(func)
    }

    fun getEventOperator(name: String): EventOperator<out Event>? {
        return operatorMap.entries.firstOrNull { it.key.equals(name, true) }?.value
    }
}