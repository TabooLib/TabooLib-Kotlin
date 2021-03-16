package io.izzel.taboolib.kotlin

import io.izzel.taboolib.Version
import io.izzel.taboolib.util.Ref
import org.bukkit.Bukkit
import org.bukkit.configuration.ConfigurationSection

fun MutableList<Any>.setSafely(index: Int, element: Any, def: Any = "") {
    while (size <= index) {
        add(def)
    }
    this[index] = element
}

fun MutableList<Any>.addSafely(index: Int, element: Any, def: Any = "") {
    while (size <= index) {
        add(def)
    }
    this.add(index, element)
}

fun String.toPrinted(separator: String = ""): List<String> {
    val result = ArrayList<String>()
    var i = 0
    while (i < length) {
        if (get(i) == '§') {
            i++
        } else {
            result.add("${substring(0, i + 1)}${if (i % 2 == 1) separator else ""}")
        }
        i++
    }
    if (separator.isNotEmpty() && i % 2 == 0) {
        result.add(this)
    }
    return result
}

fun Any.asMap() = when (this) {
    is Map<*, *> -> {
        this.map { (k, v) -> k.toString() to v }.toMap()
    }
    is ConfigurationSection -> {
        this.getValues(false)
    }
    else -> null
}

fun Any.asList(): List<String> {
    return if (this is List<*>) map { it.toString() } else listOf(toString())
}

fun info(any: Any?) {
    Bukkit.getLogger().info("[${Ref.getCallerPlugin().name}] $any")
}

fun warning(any: Any?) {
    Bukkit.getLogger().warning("[${Ref.getCallerPlugin().name}] $any")
}

fun nmsClass(nms: String): Class<*> {
    return Class.forName("net.minecraft.server." + Version.getBukkitVersion() + "." + nms)
}

fun obcClass(obc: String): Class<*> {
    return Class.forName("org.bukkit.craftbukkit." + Version.getBukkitVersion() + "." + obc)
}