package io.izzel.taboolib.kotlin.kether.action.bukkit

import org.bukkit.entity.Player

class PlayerOperator(
    val read: ((Player) -> Any?)? = { },
    val write: ((Player, Symbol, Any?) -> Unit)? = { _, _, _ -> }
)