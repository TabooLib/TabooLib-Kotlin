package io.izzel.taboolib.kotlin.kether.action.bukkit

import io.izzel.kether.common.api.ParsedAction
import io.izzel.kether.common.api.QuestAction
import io.izzel.kether.common.api.QuestContext
import io.izzel.taboolib.internal.xseries.XMaterial
import io.izzel.taboolib.kotlin.ToastFrame
import io.izzel.taboolib.kotlin.kether.Kether.expects
import io.izzel.taboolib.kotlin.kether.KetherError
import io.izzel.taboolib.kotlin.kether.KetherParser
import io.izzel.taboolib.kotlin.kether.ScriptContext
import io.izzel.taboolib.kotlin.kether.ScriptParser
import io.izzel.taboolib.kotlin.sendToast
import org.bukkit.Material
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionToast(val material: Material, val message: ParsedAction<*>, val frame: ToastFrame) : QuestAction<Void>() {

    override fun process(context: QuestContext.Frame): CompletableFuture<Void> {
        return context.newFrame(message).run<Any>().thenAcceptAsync({
            val viewer = ((context.context() as ScriptContext).sender as? Player) ?: throw RuntimeException("No player selected.")
            viewer.sendToast(material, it.toString(), frame)
        }, context.context().executor)
    }

    override fun toString(): String {
        return "ActionToast(material=$material, message=$message, frame=$frame)"
    }

    companion object {

        /**
         * toast stone *"123" as challenge
         */
        @KetherParser(["toast"])
        fun parser() = ScriptParser.parser {
            val mat = it.nextToken()
            val material = XMaterial.matchXMaterial(mat).orElse(XMaterial.BEDROCK).parseMaterial() ?: throw KetherError.NOT_MATERIAL.create(mat)
            val text = it.nextAction<Any>()
            val frame = try {
                it.mark()
                it.expects("by", "with", "as")
                ToastFrame.valueOf(it.expects("task", "goal", "challenge").toUpperCase())
            } catch (ignored: Throwable) {
                it.reset()
                ToastFrame.TASK
            }
            ActionToast(material, text, frame)
        }
    }
}