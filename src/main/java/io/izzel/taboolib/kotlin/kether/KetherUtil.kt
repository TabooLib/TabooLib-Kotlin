package io.izzel.taboolib.kotlin.kether

import io.izzel.kether.common.api.QuestContext
import io.izzel.kether.common.util.LocalizedException
import io.izzel.taboolib.kotlin.warning
import io.izzel.taboolib.util.Coerce
import java.util.*
import kotlin.collections.HashMap

fun QuestContext.Frame.script() = context() as ScriptContext

fun QuestContext.Frame.deepVars() = HashMap<String, Any?>().also { map ->
    var parent = parent()
    while (parent.isPresent) {
        map.putAll(parent.get().variables().keys().map { it to variables().get<Any>(it).orElse(null) })
        parent = parent.get().parent()
    }
    map.putAll(variables().keys().map { it to variables().get<Any>(it).orElse(null) })
}

fun Throwable.printMessage() {
    if (this is LocalizedException) {
        warning("Unexpected exception while parsing kether script:")
        localizedMessage.split("\n").forEach { warning(it) }
    } else {
        printStackTrace()
    }
}

fun Any?.inferType(): Any? {
    val asInteger = asInteger(this)
    if (asInteger.isPresent) {
        return asInteger.get()
    }
    val asLong = Coerce.asLong(this)
    if (asLong.isPresent) {
        return asLong.get()
    }
    val asDouble = Coerce.asDouble(this)
    if (asDouble.isPresent) {
        return asDouble.get()
    }
    val asBoolean = Coerce.asBoolean(this)
    if (asBoolean.isPresent) {
        return asBoolean.get()
    }
    return this
}

private fun asInteger(obj: Any?) = when (obj) {
    null -> {
        Optional.empty()
    }
    is Number -> {
        Optional.of(obj.toInt())
    }
    else -> {
        try {
            Optional.ofNullable(Integer.valueOf(obj.toString()))
        } catch (ignored: NumberFormatException) {
            Optional.empty()
        }
    }
}