package io.izzel.taboolib.kotlin.kether.action.bukkit

import io.izzel.kether.common.api.QuestAction
import io.izzel.kether.common.api.QuestContext
import io.izzel.taboolib.internal.xseries.XSound
import io.izzel.taboolib.kotlin.kether.Kether.expects
import io.izzel.taboolib.kotlin.kether.KetherParser
import io.izzel.taboolib.kotlin.kether.ScriptContext
import io.izzel.taboolib.kotlin.kether.ScriptParser
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionSound(val sound: String, val volume: Float, val pitch: Float) : QuestAction<Void>() {

    override fun process(context: QuestContext.Frame): CompletableFuture<Void> {
        val viewer = (context.context() as ScriptContext).sender as? Player ?: throw RuntimeException("No player selected.")
        if (sound.startsWith("resource:")) {
            viewer.playSound(viewer.location, sound.substring("resource:".length), volume, pitch)
        } else {
            XSound.matchXSound(sound).ifPresent {
                it.play(viewer, volume, pitch)
            }
        }
        return CompletableFuture.completedFuture(null)
    }

    override fun toString(): String {
        return "ActionSound(sound='$sound', volume=$volume, pitch=$pitch)"
    }

    companion object {

        /**
         * sound block_stone_break by 1 1
         */
        @KetherParser(["sound"])
        fun parser() = ScriptParser.parser {
            val sound = it.nextToken()
            var volume = 1.0f
            var pitch = 1.0f
            it.mark()
            try {
                it.expects("by", "with")
                volume = it.nextDouble().toFloat()
                pitch = it.nextDouble().toFloat()
            } catch (ignored: Exception) {
                it.reset()
            }
            ActionSound(sound, volume, pitch)
        }
    }
}