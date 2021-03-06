package io.izzel.taboolib.kotlin.kether

import io.izzel.kether.common.actions.KetherTypes
import io.izzel.kether.common.api.QuestActionParser
import io.izzel.kether.common.loader.LoadError
import io.izzel.kether.common.loader.QuestReader
import io.izzel.taboolib.kotlin.kether.action.bukkit.PlayerOperator
import io.izzel.taboolib.util.Coerce
import org.bukkit.event.Event
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import kotlin.reflect.KClass

object Kether {

    val registry = ScriptService.INSTANCE.registry.also {
        KetherTypes.registerInternals(it, ScriptService.INSTANCE)
    }

    val operatorsEvent = LinkedHashMap<String, EventOperator<out Event>>()
    val operatorsPlayer = LinkedHashMap<String, PlayerOperator>()

    init {
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
    }

    fun addAction(name: Array<String>, parser: QuestActionParser) {
        name.forEach { registry.registerAction(it, parser) }
    }

    fun addAction(name: String, parser: QuestActionParser, namespace: String? = null) {
        registry.registerAction(namespace ?: "kether", name, parser)
    }

    fun removeAction(name: String, namespace: String? = null) {
        registry.unregisterAction(namespace ?: "kether", name)
    }

    fun addPlayerOperator(name: String, operator: PlayerOperator) {
        operatorsPlayer[name] = operator
    }

    fun <T : Event> addEventOperator(name: String, event: KClass<out T>, func: EventOperator<T>.() -> Unit = {}) {
        operatorsEvent[name] = EventOperator(event).also(func)
    }

    fun getEventOperator(name: String): EventOperator<out Event>? {
        return operatorsEvent.entries.firstOrNull { it.key.equals(name, true) }?.value
    }

    fun QuestReader.expects(vararg args: String): String {
        val element = nextToken()
        if (element !in args) {
            throw LoadError.NOT_MATCH.create("[${args.joinToString(", ")}]", element)
        }
        return element
    }
}