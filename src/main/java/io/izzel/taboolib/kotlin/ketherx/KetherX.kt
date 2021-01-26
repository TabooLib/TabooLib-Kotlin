package io.izzel.taboolib.kotlin.ketherx

import io.izzel.kether.common.actions.KetherTypes
import io.izzel.kether.common.api.QuestActionParser
import io.izzel.kether.common.api.QuestStorage
import io.izzel.kether.common.api.storage.LocalYamlStorage
import io.izzel.taboolib.TabooLib
import io.izzel.taboolib.kotlin.Indexed.join
import io.izzel.taboolib.kotlin.ketherx.action.*
import io.izzel.taboolib.kotlin.ketherx.action.bukkit.*
import io.izzel.taboolib.module.command.lite.CommandBuilder
import io.izzel.taboolib.module.inject.TFunction
import io.izzel.taboolib.module.inject.TSchedule
import io.izzel.taboolib.util.Coerce
import io.izzel.taboolib.util.Files
import org.bukkit.event.Event
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

object KetherX {

    val registry = ScriptService.registry.also {
        KetherTypes.registerInternals(it, ScriptService)
    }

    var storage: QuestStorage? = null
        private set

    val operatorMap = ConcurrentHashMap<String, EventOperator<out Event>>()

    @TFunction.Init
    fun init() {
        addAction("check", ActionCheck.parser())
        addAction("function", ActionFunction.parser())
        addAction(arrayOf("js", "javascript"), ActionJavaScript.parser())
        addAction("join", ActionJoin.parser())
        addAction(arrayOf("log", "print"), ActionLog.parser())
        addAction("pause", ActionPause.parser())
        addAction("run", ActionRun.parser())
        addAction(arrayOf("exit", "terminate"), ActionTerminate.parser())
        addAction(arrayOf("wait", "sleep"), ActionWait.parser())
        // bukkit
        addAction("color", ActionColor.parser())
        addAction("command", ActionCommand.parser())
        addAction("continue", ActionContinue.parser())
        addAction("event", ActionEvent.parser())
        addAction("listen", ActionListen.parser())
        addAction("permission", ActionPermission.parser())
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
        CommandBuilder.create("ketherx", TabooLib.getPlugin())
            .permission("*")
            .execute { sender, args ->
                if (args.isEmpty()) {
                    sender.sendMessage("§8[§fTabooLib§8] §7Usage: §8/ketherx shell [shell]")
                    sender.sendMessage("§8[§fTabooLib§8] §7Usage: §8/ketherx function [text]")
                } else if (args[0] == "shell" && args.size > 1) {
                    val time = System.currentTimeMillis()
                    ScriptContext.create(ScriptLoader.load(join(args, 1, " "))) {
                        this.sender = sender
                    }.runActions().thenAccept {
                        sender.sendMessage("§8[§fTabooLib§8] §7Execution result: §f${it} §8(${System.currentTimeMillis() - time}ms)")
                    }
                } else if (args[0] == "function") {
                    val time = System.currentTimeMillis()
                    val r = KetherFunction.parse(join(args, 1, " "), cacheFunction = false, cacheScript = false) {
                        this.sender = sender
                    }
                    sender.sendMessage("§8[§fTabooLib§8] §7Function parsed: §f\"${r}\" §8(${System.currentTimeMillis() - time}ms)")
                } else {
                    sender.sendMessage("§8[§fTabooLib§8] §7Oops!")
                }
            }.build()
    }

    @TSchedule
    fun tick() {
        try {
            storage = LocalYamlStorage(ScriptService, Files.folder(TabooLib.getPlugin().dataFolder, "temp").toPath())
            storage!!.init()
        } catch (e: Exception) {
            println("[TabooLib] Script data storage initialization failed")
            e.printStackTrace()
        }
    }

    fun addAction(name: Array<String>, parser: QuestActionParser) {
        name.forEach { registry.registerAction(it, parser) }
    }

    fun addAction(name: String, parser: QuestActionParser, namespace: String? = null) {
        registry.registerAction(namespace ?: "ketherx", name, parser)
    }

    fun <T : Event> addEventOperator(name: String, event: KClass<out T>, func: EventOperator<T>.() -> Unit = {}) {
        operatorMap[name] = EventOperator(event).also(func)
    }

    fun getEventOperator(name: String): EventOperator<out Event>? {
        return operatorMap.entries.firstOrNull { it.key.equals(name, true) }?.value
    }
}