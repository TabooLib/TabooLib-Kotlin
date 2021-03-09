package io.izzel.taboolib.kotlin.kether

import io.izzel.kether.common.api.AbstractQuestContext
import io.izzel.kether.common.api.Quest
import org.bukkit.command.CommandSender
import org.bukkit.event.Event
import java.util.concurrent.CompletableFuture

/**
 * Adyeshach
 * io.izzel.taboolib.kotlin.ketherx.ScriptContext
 *
 * @author sky
 * @since 2021/1/20 10:39 上午
 */
open class ScriptContext(service: ScriptService, script: Quest) :
    AbstractQuestContext<ScriptContext>(service, script, null) {

    lateinit var id: String

    var event: Event?
        get() = this["@Event"]
        set(value) {
            this["@Event"] = value
        }

    var eventOperator: EventOperator<*>?
        get() = this["@EventOperator"]
        set(value) {
            this["@EventOperator"] = value
        }

    var listener: CompletableFuture<Void>?
        get() = this["@Listener"]
        set(value) {
            this["@Listener"] = value
        }

    var sender: CommandSender?
        get() = this["@Sender"]
        set(value) {
            this["@Sender"] = value
        }

    var breakLoop: Boolean
        get() = get<Boolean>("@BreakLoop") == true
        set(value) {
            this["@BreakLoop"] = value
        }

    operator fun set(key: String, value: Any?) {
        rootFrame.variables().set(key, value)
    }

    operator fun <T> get(key: String, def: T? = null): T? {
        return rootFrame.variables().get<T>(key).orElse(def)
    }

    override fun createExecutor() = ScriptSchedulerExecutor

    companion object {

        fun create(script: Quest, context: ScriptContext.() -> Unit = {}): ScriptContext {
            return ScriptContext(ScriptService.INSTANCE, script).also(context)
        }
    }
}