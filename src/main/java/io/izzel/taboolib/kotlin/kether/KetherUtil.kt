package io.izzel.taboolib.kotlin.kether

import io.izzel.kether.common.api.QuestContext
import io.izzel.kether.common.util.LocalizedException
import io.izzel.taboolib.kotlin.warning

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