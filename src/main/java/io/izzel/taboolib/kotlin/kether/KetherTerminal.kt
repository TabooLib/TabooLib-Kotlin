package io.izzel.taboolib.kotlin.kether

import io.izzel.kether.common.util.LocalizedException
import io.izzel.taboolib.TabooLib
import io.izzel.taboolib.kotlin.Tasks
import io.izzel.taboolib.module.inject.PlayerContainer
import io.izzel.taboolib.module.inject.TListener
import io.izzel.taboolib.module.packet.Packet
import io.izzel.taboolib.module.packet.TPacket
import net.md_5.bungee.api.chat.BaseComponent
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.metadata.FixedMetadataValue
import java.lang.Exception
import java.lang.NullPointerException
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.ArrayList

@TListener
object KetherTerminal : Listener {

    @PlayerContainer
    val shell = ConcurrentHashMap<String, MutableList<String>>()

    @PlayerContainer
    val namespace = ConcurrentHashMap<String, MutableList<String>>()

    fun open(player: Player) {
        repeat(100) {
            player.sendMessage("§c")
        }
        player.sendMessage("§c[Terminal] §7Last login: ${Date()} on ${player.name}.")
        player.sendMessage("§c[Terminal] §7Use \"quit\" to exit the terminal.")
        player.setMetadata("kether:terminal", FixedMetadataValue(TabooLib.getPlugin(), true))
    }

    fun close(player: Player) {
        player.removeMetadata("kether:terminal", TabooLib.getPlugin())
        repeat(100) {
            player.sendMessage("§c")
        }
        player.sendMessage("§8[§fTabooLib§8] §7Terminal closed.")
    }

    @EventHandler
    fun e(e: PlayerJoinEvent) {
        e.player.removeMetadata("kether:terminal", TabooLib.getPlugin())
    }

    @Suppress("UNCHECKED_CAST")
    @TPacket(type = TPacket.Type.SEND)
    fun send(player: Player, packet: Packet): Boolean {
        if (packet.equals("PacketPlayOutChat") && player.hasMetadata("kether:terminal")) {
            val message = packet.reflex("a").read<String>("e")!!
            if (message.startsWith("[Terminal]") || message.isEmpty()) {
                return true
            }
            return false
        }
        return true
    }

    @TPacket(type = TPacket.Type.RECEIVE)
    fun receive(player: Player, packet: Packet): Boolean {
        if (packet.equals("PacketPlayInChat") && player.hasMetadata("kether:terminal")) {
            val message = packet.read("a", "")
            Tasks.task {
                player.playSound(player.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 2f)
                when (message) {
                    "include", "namespace" -> {
                        player.sendMessage("§c[Terminal] §7$ include $message")
                        namespace.computeIfAbsent(player.name) { ArrayList() }.add(message)
                    }
                    "clear", "reset" -> {
                        namespace.remove(player.name)
                        shell.remove(player.name)
                        repeat(100) {
                            player.sendMessage("§c")
                        }
                    }
                    "quit", "exit" -> {
                        close(player)
                    }
                    else -> {
                        val shell = shell.computeIfAbsent(player.name) { ArrayList() }
                        if (message != "rerun") {
                            shell.add(message)
                        }
                        player.sendMessage("§c[Terminal] §7(${shell.size}) $ $message")
                        try {
                            KetherShell.eval(shell, cacheScript = false, namespace = namespace[player.name] ?: emptyList()) {
                                sender = player
                            }.thenApply {
                                if (it != null) {
                                    player.sendMessage("§c[Terminal] §7(${shell.size}) > §f$it")
                                }
                            }
                        } catch (e: LocalizedException) {
                            player.sendMessage("§c[Terminal] §7Use \"clear\" to reset the terminal.")
                            player.sendMessage("§c[Terminal] §7Unexpected exception while parsing kether shell:")
                            e.localizedMessage?.split("\n")?.forEach {
                                player.sendMessage("§c[Terminal] §8${it}")
                            }
                            shell.removeLastOrNull()
                        } catch (e: Throwable) {
                            player.sendMessage("§c[Terminal] §7Use \"clear\" to reset the terminal.")
                            player.sendMessage("§c[Terminal] §7Unexpected exception while parsing kether shell.")
                            e.printStackTrace()
                            shell.removeLastOrNull()
                        }
                    }
                }
            }
            return false
        }
        return true
    }
}