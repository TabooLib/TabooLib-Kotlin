package io.izzel.taboolib.kotlin.kether.action.bukkit

import io.izzel.kether.common.actions.LiteralAction
import io.izzel.kether.common.api.ParsedAction
import io.izzel.kether.common.api.QuestAction
import io.izzel.kether.common.api.QuestContext
import io.izzel.kether.common.loader.types.ArgTypes
import io.izzel.taboolib.kotlin.kether.ScriptParser
import io.izzel.taboolib.module.locale.TLocale
import io.izzel.taboolib.util.Coerce
import org.bukkit.Bukkit
import org.bukkit.Location
import java.lang.Exception
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionLocation(
    val world: ParsedAction<*>,
    val x: ParsedAction<*>,
    val y: ParsedAction<*>,
    val z: ParsedAction<*>,
    val yaw: ParsedAction<*>,
    val pitch: ParsedAction<*>
) : QuestAction<Location>() {

    override fun process(context: QuestContext.Frame): CompletableFuture<Location> {
        val location = CompletableFuture<Location>()
        context.newFrame(world).run<Any>().thenApply { world ->
            context.newFrame(x).run<Any>().thenApply { x ->
                context.newFrame(y).run<Any>().thenApply { y ->
                    context.newFrame(z).run<Any>().thenApply { z ->
                        context.newFrame(yaw).run<Any>().thenApply { yaw ->
                            context.newFrame(pitch).run<Any>().thenApply { pitch ->
                                location.complete(
                                    Location(
                                        Bukkit.getWorld(world.toString()),
                                        Coerce.toDouble(x),
                                        Coerce.toDouble(y),
                                        Coerce.toDouble(z),
                                        Coerce.toFloat(yaw),
                                        Coerce.toFloat(pitch)
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
        return location
    }

    override fun toString(): String {
        return "ActionLocation(world=$world, x=$x, y=$y, z=$z, yaw=$yaw, pitch=$pitch)"
    }

    companion object {

        /**
         * location 10 20 10 and 0 0
         */
        @Suppress("UnstableApiUsage")
        fun parser() = ScriptParser.parser {
            val world = it.next(ArgTypes.ACTION)
            val x = it.next(ArgTypes.ACTION)
            val y = it.next(ArgTypes.ACTION)
            val z = it.next(ArgTypes.ACTION)
            var yaw: ParsedAction<*> = ParsedAction(LiteralAction<Any>(0f))
            var pitch: ParsedAction<*> = ParsedAction(LiteralAction<Any>(0f))
            it.mark()
            try {
                it.expect("and")
                yaw = it.next(ArgTypes.ACTION)
                pitch = it.next(ArgTypes.ACTION)
            } catch (ignored: Exception) {
                it.reset()
            }
            ActionLocation(world, x, y, z, yaw, pitch)
        }
    }
}