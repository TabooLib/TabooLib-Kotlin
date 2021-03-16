package io.izzel.taboolib.kotlin

import io.izzel.taboolib.module.locale.TLocale
import org.bukkit.entity.Player

object ScoreBoardHelper {
    private val scoreboardMap = HashMap<Player,PlayerScoreboardObj> ()

    fun send(player: Player, content: String) {
        val obj = scoreboardMap[player] ?: PlayerScoreboardObj(player).also { scoreboardMap[player] = it }
        obj.sendContent(content)
    }

    private data class PlayerScoreboardObj(val player: Player) {
        private var currentLine = NMSImpl().colors.size

        init {
            AbsNMS.instance.setupScoreboard(player)
            AbsNMS.instance.setDisplayName(player, TLocale.asString("game.scoreboard_title"))
            display()
        }

        fun display() {
            AbsNMS.instance.display(player)
        }

        fun sendContent(lines: String) {
            AbsNMS.instance.changeContent(player, lines, currentLine)
            currentLine = lines.lines().size
        }
    }
}