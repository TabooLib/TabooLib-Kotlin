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
        reflex.set("a", if (remove) "REMOVE" else "TabooScore")
        if (Version.isAfter(Version.v1_13)) {
            reflex.set("b", ChatComponentText("ScoreBoard"))
        } else {
            reflex.set("b", "ScoreBoard")
        }
        reflex.set("c", IScoreboardCriteria.EnumScoreboardHealthDisplay.INTEGER)
        reflex.set("d", 0)
        TPacketHandler.sendPacket(player, packet)
        initTeam(player)
    }

    override fun changeContent(player: Player, content: List<String>, lastContent: Map<Int, String>) {
        if (content.size != lastContent.size) {
            updateLineCount(player, content.size, lastContent.size)
        }
        content.forEachIndexed { line, ct ->
            if (ct != lastContent[content.size - line - 1]) {
                sendTeamPrefixSuffix(player, uniqueColors[content.size - line - 1], ct)
            }
        }
    }

    override fun display(player: Player) {
        val packet = PacketPlayOutScoreboardDisplayObjective()
        val reflex = packet.toReflex()
        reflex.set("a", 1)
        reflex.set("b", "TabooScore")
        TPacketHandler.sendPacket(player, packet)
    }

    override fun setDisplayName(player: Player, title: String) {
        val packet = PacketPlayOutScoreboardObjective()
        val reflex = packet.toReflex()
        reflex.set("a", "TabooScore")
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
        uniqueColors.forEachIndexed { index, color ->
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
            // Collections$SingletonList cannot be cast to java.lang.Number
            reflex.set("g", index)
            reflex.set("h", 0)
            reflex.set("f", -1)
            TPacketHandler.sendPacket(player, packet)
        }
    }

    private fun validateLineCount(line: Int) {
        if (uniqueColors.size < line) {
            throw IllegalArgumentException("Lines size are larger than supported.")
        }
    }

    /**
     * @param team 为\[content.size - line - 1\]
     */
    private fun sendTeamPrefixSuffix(player: Player, team: String, content: String) {
        if (Version.isAfter(Version.v1_13)) {
            val packet = PacketPlayOutScoreboardTeam()
            val reflex = packet.toReflex()
            reflex.set("a", team)
            reflex.set("c", ChatComponentText(content))
            reflex.set("i", 2)
            TPacketHandler.sendPacket(player, packet)
            return
        }
        var prefix = content
        var suffix = ""

        if (content.length > 16) {
            prefix = content.substring(0 until 16)
            suffix = content.substring(16 until content.length)
        }
        val packet = PacketPlayOutScoreboardTeam()
        val reflex = packet.toReflex()
        reflex.set("a", team)
        reflex.set("h", 2)
        reflex.set("c", prefix)
        reflex.set("d", suffix)
        TPacketHandler.sendPacket(player, packet)
    }

    private fun updateLineCount(player: Player, line: Int, lastLineCount: Int) {
        validateLineCount(line)
        if (line > lastLineCount) {
            (lastLineCount until line).forEach { i ->
                if (Version.isAfter(Version.v1_13)) {
                    val packet = PacketPlayOutScoreboardScore()
                    val reflex = packet.toReflex()
                    reflex.set("a", uniqueColors[i])
                    reflex.set("b", "TabooScore")
                    reflex.set("c", i)
                    reflex.set("d", ScoreboardServer.Action.CHANGE)
                    TPacketHandler.sendPacket(player, packet)
                    return@forEach
                }
                val packet = PacketPlayOutScoreboardScore()
                val reflex = packet.toReflex()
                reflex.set("a", uniqueColors[i])
                reflex.set("b", "TabooScore")
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
                    reflex.set("b", "TabooScore")
                    reflex.set("d", ScoreboardServer.Action.REMOVE)
                    TPacketHandler.sendPacket(player, packet)
                    return@forEach
                }
                val packet = PacketPlayOutScoreboardScore()
                val reflex = packet.toReflex()
                reflex.set("a", uniqueColors[i])
                reflex.set("b", "TabooScore")
                reflex.set("d", net.minecraft.server.v1_12_R1.PacketPlayOutScoreboardScore.EnumScoreboardAction.REMOVE)
                TPacketHandler.sendPacket(player, packet)
            }
        }
    }
}