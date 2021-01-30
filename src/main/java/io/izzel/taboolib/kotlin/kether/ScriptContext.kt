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
        set(value) {
            rootFrame.variables().set("@Event", value)
        }
        get() {
            return rootFrame.variables().get<Event?>("@Event").orElse(null)
        }

    var eventOperator: EventOperator<*>?
        set(value) {
            rootFrame.variables().set("@EventOperator", value)
        }
        get() {
            return rootFrame.variables().get<EventOperator<*>?>("@EventOperator").orElse(null)
        }

    var listener: CompletableFuture<Void>?
        set(value) {
            rootFrame.variables().set("@Listener", value)
        }
        get() {
            return rootFrame.variables().get<CompletableFuture<Void>?>("@Listener").orElse(null)
        }

    var sender: CommandSender?
        set(value) {
            rootFrame.variables().set("@Sender", value)
        }
        get() {
            return rootFrame.variables().get<CommandSender?>("@Sender").orElse(null)
        }

    override fun createExecutor() = ScriptSchedulerExecutor

    companion object {

        fun create(script: Quest, context: ScriptContext.() -> Unit = {}): ScriptContext {
            return ScriptContext(ScriptService, script).also(context)
        }
    }
}