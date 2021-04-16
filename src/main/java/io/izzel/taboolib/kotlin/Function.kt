package io.izzel.taboolib.kotlin

import io.izzel.taboolib.Version
import io.izzel.taboolib.module.locale.TLocale
import io.izzel.taboolib.module.nms.NMS
import io.izzel.taboolib.util.Ref
import io.izzel.taboolib.util.Strings
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.inventory.ItemStack

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

fun ItemStack.getCompound() = NMS.handle().loadNBT(this)

fun String.replaceWithOrder(vararg args: Any) = Strings.replaceWithOrder(this, *args)!!

fun String.colored() = TLocale.Translate.setColored(this)

fun List<String>.colored() = TLocale.Translate.setColored(this).toList()

fun CommandSender.sendLocale(node: String, vararg args: Any) {
    TLocale.sendTo(this, node, *args)
}

inline fun <T> Iterable<T>.subBy(selector: (T) -> Int): Int {
    var sum = 0
    for (element in this) {
        sum -= selector(element)
    }
    return sum
}

inline fun <T> Iterable<T>.subByDouble(selector: (T) -> Double): Double {
    var sum = 0.0
    for (element in this) {
        sum -= selector(element)
    }
    return sum
}

inline fun <T> Iterable<T>.mulBy(selector: (T) -> Int): Int {
    var sum = 1
    for (element in this) {
        sum *= selector(element)
    }
    return sum
}

inline fun <T> Iterable<T>.mulByDouble(selector: (T) -> Double): Double {
    var sum = 1.0
    for (element in this) {
        sum *= selector(element)
    }
    return sum
}

inline fun <T> Iterable<T>.divBy(selector: (T) -> Int): Int {
    var sum = selector(firstOrNull() ?: return 0)
    forEachIndexed { index, element ->
        if (index > 0) {
            sum /= selector(element)
        }
    }
    return sum
}

inline fun <T> Iterable<T>.divByDouble(selector: (T) -> Double): Double {
    var sum = selector(firstOrNull() ?: return 0.0)
    forEachIndexed { index, element ->
        if (index > 0) {
            sum /= selector(element)
        }
    }
    return sum
}