package io.izzel.taboolib.kotlin.kether

import io.izzel.kether.common.api.Quest
import java.util.concurrent.TimeUnit

object KetherShell {

    val scriptMap = HashMap<String, Quest>()

    fun eval(
        source: List<String>,
        cacheScript: Boolean = true,
        namespace: List<String> = emptyList(),
        context: ScriptContext.() -> Unit = {}
    ): Any? {
        return eval(source.joinToString("\n"), cacheScript, namespace, context)
    }

    fun eval(
        source: String,
        cacheScript: Boolean = true,
        namespace: List<String> = emptyList(),
        context: ScriptContext.() -> Unit = {}
    ): Any? {
        val s = "def main = { $source }"
        val script = if (cacheScript) this.scriptMap.computeIfAbsent(s) {
            ScriptLoader.load(it, namespace)
        } else {
            ScriptLoader.load(s, namespace)
        }
        return ScriptContext.create(script).also(context).runActions().get(1, TimeUnit.SECONDS)
    }
}