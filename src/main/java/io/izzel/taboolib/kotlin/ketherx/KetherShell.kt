package io.izzel.taboolib.kotlin.ketherx

import io.izzel.kether.common.api.Quest
import java.util.concurrent.TimeUnit

object KetherShell {

    val scriptMap = HashMap<String, Quest>()

    fun eval(source: List<String>, cacheScript: Boolean = true, context: ScriptContext.() -> Unit = {}): Any? {
        return eval(source.joinToString("\n"), cacheScript, context)
    }

    fun eval(source: String, cacheScript: Boolean = true, context: ScriptContext.() -> Unit = {}): Any? {
        val script = if (cacheScript) this.scriptMap.computeIfAbsent(source) {
            ScriptLoader.load(it)
        } else {
            ScriptLoader.load(source)
        }
        return ScriptContext.create(script).also(context).runActions().get(1, TimeUnit.SECONDS)
    }
}