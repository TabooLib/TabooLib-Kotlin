package io.izzel.taboolib.kotlin.kether

import io.izzel.kether.common.api.Quest
import io.izzel.kether.common.util.LocalizedException
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import kotlin.jvm.Throws

object KetherShell {

    val scriptMap = HashMap<String, Quest>()

    @Throws(LocalizedException::class)
    fun eval(
        source: List<String>,
        cacheScript: Boolean = true,
        namespace: List<String> = emptyList(),
        context: ScriptContext.() -> Unit = {}
    ): CompletableFuture<Any?> {
        return eval(source.joinToString("\n"), cacheScript, namespace, context)
    }

    @Throws(LocalizedException::class)
    fun eval(
        source: String,
        cacheScript: Boolean = true,
        namespace: List<String> = emptyList(),
        context: ScriptContext.() -> Unit = {}
    ): CompletableFuture<Any?> {
        val s = "def main = { $source }"
        val script = if (cacheScript) this.scriptMap.computeIfAbsent(s) {
            ScriptLoader.load(it, namespace)
        } else {
            ScriptLoader.load(s, namespace)
        }
        return ScriptContext.create(script).also(context).runActions()
    }
}