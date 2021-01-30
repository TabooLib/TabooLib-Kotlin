package io.izzel.taboolib.kotlin.kether

import io.izzel.kether.common.api.Quest
import java.util.*
import kotlin.collections.HashMap

/**
 * your health {{player health}}, your name {{player name}}
 */
object KetherFunction {

    val regex = Regex("\\{\\{(.*?)}}")

    val scriptMap = HashMap<String, Quest>()
    val functionMap = HashMap<String, Function>()

    fun parse(
        input: String,
        cacheFunction: Boolean = false,
        cacheScript: Boolean = true,
        namespace: List<String> = emptyList(),
        context: ScriptContext.() -> Unit = {}
    ): String {
        val function = if (cacheFunction) this.functionMap.computeIfAbsent(input) {
            input.toFunction()
        } else {
            input.toFunction()
        }
        val script = if (cacheScript) this.scriptMap.computeIfAbsent(function.source) {
            ScriptLoader.load(it, namespace)
        } else {
            ScriptLoader.load(function.source, namespace)
        }
        val vars = ScriptContext.create(script).also(context).run {
            runActions()
            rootFrame().variables()
        }
        return function.element.joinToString("") {
            if (it.isFunction) {
                vars.get<Any>(it.hash).orElse("{{${it.value}}}").toString()
            } else {
                it.value
            }
        }
    }

    fun String.toFunction(): Function {
        val element = ArrayList<Element>()
        var index = 0
        regex.findAll(this).forEach {
            element.add(Element(substring(index, it.range.first)))
            element.add(Element(it.groupValues[1], true))
            index = it.range.last + 1
        }
        val last = Element(substring(index, length))
        if (last.value.isNotEmpty()) {
            element.add(last)
        }
        return Function(element, element.filter { it.isFunction }.joinToString(" ") {
            "set ${it.hash} to ${it.value}"
        })
    }

    class Element(var value: String, var isFunction: Boolean = false) {

        val hash: String
            get() = value.hashCode().toString()
    }

    class Function(val element: List<Element>, val source: String)
}