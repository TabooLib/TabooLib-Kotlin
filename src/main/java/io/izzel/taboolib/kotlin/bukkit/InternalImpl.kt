package io.izzel.taboolib.kotlin.bukkit

import io.izzel.taboolib.Version
import io.izzel.taboolib.kotlin.Reflex.Companion.toReflex
import io.izzel.taboolib.module.packet.TPacketHandler
import net.minecraft.server.v1_16_R3.*
import org.bukkit.entity.Player

class InternalImpl : Internal() {

    override fun setupScoreboard(player: Player, remove: Boolean) {
        val packet = PacketPlayOutScoreboardObjective()
        val reflex = packet.toReflex()
        reflex.set("a", if (remove) "REMOVE" else "BCSB")
        if (Version.isAfter(Version.v1_13)) {
            reflex.set("b", ChatComponentText("ScoreBoard"))
        } else {
            reflex.set("b", "ScoreBoard")
        }
        reflex.set("c", IScoreboardCriteria.EnumScoreboardHealthDisplay.INTEGER)
        reflex.set("d", 0)
        TPacketHandler.sendPacket(player, packet)
        initTeam(player)
        initLines(player, uniqueColors.size)
    }

    override fun changeContent(player: Player, content: List<String>, lastContent: Map<Int, String>) {
        if (content.size != lastContent.size) {
            updateLines(player, content.size, lastContent.size)
        }
        content.forEachIndexed { line, ct ->
            if (ct != lastContent[line]) {
                sendTeamPrefixSuffix(player, uniqueColors[content.size - line - 1], ct)
            }
        }
    }

    override fun display(player: Player) {
        val packet = PacketPlayOutScoreboardDisplayObjective()
        val reflex = packet.toReflex()
        reflex.set("a", 1)
        reflex.set("b", "BCSB")
        TPacketHandler.sendPacket(player, packet)
    }

    override fun setDisplayName(player: Player, title: String) {
        val packet = PacketPlayOutScoreboardObjective()
        val reflex = packet.toReflex()
        reflex.set("a", "BCSB")
        if (Version.isAfter(Version.v1_13)) {
            reflex.set("b", ChatComponentText(title))
        } else {
            reflex.set("b", title)
        }
        reflex.set("c", IScoreboardCriteria.EnumScoreboardHealthDisplay.INTEGER)
        reflex.set("d", 2)
        TPacketHandler.sendPacket(player, packet)
    }

    /**
     *
     * a -> Team Name
     *
     * b -> Team Display Name
     *
     * c -> Team Prefix
     *
     * d -> Team Suffix
     *
     * e -> Name Tag Visibility
     *
     * f -> Color
     *
     * g -> Players, Player Count
     *
     * h -> Mode
     *
     *  If 0 then the team is created.
     *  If 1 then the team is removed.
     *  If 2 the team team information is updated.
     *  If 3 then new players are added to the team.
     *  If 4 then players are removed from the team.
     *
     * i -> Friendly Fire
     *
     * @see EnumChatFormat
     * @see PacketPlayOutScoreboardTeam
     */
    private fun initTeam(player: Player) {
        uniqueColors.forEachIndexed { idx, color ->
            if (Version.isAfter(Version.v1_13)) {
                val packet = PacketPlayOutScoreboardTeam()
                val reflex = packet.toReflex()
                reflex.set("a", color)
                reflex.set("b", ChatComponentText(color))
                reflex.set("e", ScoreboardTeamBase.EnumNameTagVisibility.ALWAYS.e)
                reflex.set("f", ScoreboardTeamBase.EnumTeamPush.ALWAYS.e)
                reflex.set("g", EnumChatFormat.RESET)
                reflex.set("h", listOf(color))
                reflex.set("i", 0)
                reflex.set("j", -1)
                TPacketHandler.sendPacket(player, packet)
                return@forEachIndexed
            }
            val packet = PacketPlayOutScoreboardTeam()
            val reflex = packet.toReflex()
            reflex.set("a", color)
            reflex.set("b", color)
            reflex.set("e", ScoreboardTeamBase.EnumNameTagVisibility.ALWAYS.e)
            reflex.set("g", listOf(color))
            reflex.set("h", 0)
            reflex.set("f", -1)
            TPacketHandler.sendPacket(player, packet)
        }
    }

    private fun initLines(player: Player, line: Int) {
        validateLines(line)
        uniqueColors.forEachIndexed { num, str ->
            if (num > line) {
                return
            }
            if (Version.isAfter(Version.v1_13)) {
                val packet = PacketPlayOutScoreboardScore()
                val reflex = packet.toReflex()
                reflex.set("a", str)
                reflex.set("b", "BCSB")
                reflex.set("c", num)
                reflex.set("d", ScoreboardServer.Action.CHANGE)
                TPacketHandler.sendPacket(player, packet)
                return
            }
            val packet = PacketPlayOutScoreboardScore()
            val reflex = packet.toReflex()
            reflex.set("a", str)
            reflex.set("b", "BCSB")
            reflex.set("c", num)
            reflex.set("d", net.minecraft.server.v1_12_R1.PacketPlayOutScoreboardScore.EnumScoreboardAction.CHANGE)
            TPacketHandler.sendPacket(player, packet)
        }
    }

    private fun validateLines(line: Int) {
        if (uniqueColors.size < line) {
            throw IllegalArgumentException("Lines size are larger than supported.")
        }
    }

    private fun sendTeamPrefixSuffix(player: Player, team: String, prefix: String) {
        if (Version.isAfter(Version.v1_13)) {
            val packet = PacketPlayOutScoreboardTeam()
            val reflex = packet.toReflex()
            reflex.set("a", team)
            reflex.set("c", ChatComponentText(prefix))
            reflex.set("i", 2)
            TPacketHandler.sendPacket(player, packet)
            return
        }
        var p = prefix
        var s = ""

        if (prefix.length > 16) {
            p = prefix.substring(0 until 16)
            s = prefix.substring(16 until prefix.length)
        }
        val packet = PacketPlayOutScoreboardTeam()
        val reflex = packet.toReflex()
        reflex.set("a", team)
        reflex.set("h", 2)
        reflex.set("c", p)
        TPacketHandler.sendPacket(player, packet)

        val packetSuffix = PacketPlayOutScoreboardTeam()
        val reflexSuffix = packetSuffix.toReflex()
        reflexSuffix.set("a", team)
        reflexSuffix.set("h", 2)
        reflexSuffix.set("d", s)
        TPacketHandler.sendPacket(player, packetSuffix)
    }

    private fun updateLines(player: Player, line: Int, lastLineCount: Int) {
        validateLines(line)
        if (line > lastLineCount) {
            (lastLineCount until line).forEach { i ->
                if (Version.isAfter(Version.v1_13)) {
                    val packet = PacketPlayOutScoreboardScore()
                    val reflex = packet.toReflex()
                    reflex.set("a", uniqueColors[i])
                    reflex.set("b", "BCSB")
                    reflex.set("c", i)
                    reflex.set("d", ScoreboardServer.Action.CHANGE)
                    TPacketHandler.sendPacket(player, packet)
                    return@forEach
                }
                val packet = PacketPlayOutScoreboardScore()
                val reflex = packet.toReflex()
                reflex.set("a", uniqueColors[i])
                reflex.set("b", "BCSB")
                reflex.set("c", i)
                reflex.set("d", net.minecraft.server.v1_12_R1.PacketPlayOutScoreboardScore.EnumScoreboardAction.CHANGE)
                TPacketHandler.sendPacket(player, packet)
            }
        } else {
            (line until lastLineCount).forEach { i ->
                if (Version.isAfter(Version.v1_13)) {
                    val packet = PacketPlayOutScoreboardScore()
                    val reflex = packet.toReflex()
                    reflex.set("a", uniqueColors[i])
                    reflex.set("b", "BCSB")
                    reflex.set("d", ScoreboardServer.Action.REMOVE)
                    TPacketHandler.sendPacket(player, packet)
                    return@forEach
                }
                val packet = PacketPlayOutScoreboardScore()
                val reflex = packet.toReflex()
                reflex.set("a", uniqueColors[i])
                reflex.set("b", "BCSB")
                reflex.set("d", net.minecraft.server.v1_12_R1.PacketPlayOutScoreboardScore.EnumScoreboardAction.REMOVE)
                TPacketHandler.sendPacket(player, packet)
            }
        }
    }
}