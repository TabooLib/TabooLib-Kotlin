package io.izzel.taboolib.kotlin

import com.google.common.collect.Maps
import io.izzel.taboolib.util.Ref
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap

/**
 * @author sky
 * @since 2020-10-02 01:40
 */
@Suppress("UNCHECKED_CAST")
class Reflex(val from: Class<*>) {

    var instance: Any? = null

    fun instance(instance: Any?): Reflex {
        this.instance = instance
        return this
    }

    fun <T> read(name: String): T? {
        val deep = name.indexOf("/")
        if (deep == -1) {
            return get(name)
        }
        var find: T? = null
        var ref = of(get(name.substring(0, deep))!!)
        name.substring(deep).split("/").filter { it.isNotEmpty() }.forEach { point ->
            find = ref.get(point)
            if (find != null) {
                ref = of(find!!)
            }
        }
        return find
    }

    fun write(name: String, value: Any?) {
        val deep = name.indexOf("/")
        if (deep == -1) {
            return set(name, value)
        }
        val node0 = name.substring(0, deep)
        val node1 = name.substring(name.lastIndexOf("/") + 1, name.length)
        val space = name.substring(deep).split("/").filter { it.isNotEmpty() }
        var ref = of(get(node0)!!)
        space.forEachIndexed { index, point ->
            if (index + 1 < space.size) {
                ref = of(ref.get(point)!!)
            }
        }
        ref.set(node1, value)
    }

    fun <T> get(type: Class<T>, index: Int = 0): T? {
        val field = cachedField.computeIfAbsent(from.name) {
            Ref.getDeclaredFields(from).map {
                it.isAccessible = true
                it.name to it
            }.toMap(ConcurrentHashMap())
        }.values.filter { it.type == type }.getOrNull(index - 1) ?: throw NoSuchFieldException("$type($index) ($from)")
        val obj = Ref.getField(instance, field)
        return if (obj != null) obj as T else null
    }

    fun <T> get(name: String): T? {
        val map = cachedField.computeIfAbsent(from.name) {
            Ref.getDeclaredFields(from).map {
                it.isAccessible = true
                it.name to it
            }.toMap(ConcurrentHashMap())
        }
        val obj = Ref.getField(instance, map[name] ?: throw NoSuchFieldException("$name ($from)"))
        return if (obj != null) obj as T else null
    }

    fun set(type: Class<*>, value: Any?, index: Int = 0) {
        val field = cachedField.computeIfAbsent(from.name) {
            Ref.getDeclaredFields(from).map {
                it.isAccessible = true
                it.name to it
            }.toMap(ConcurrentHashMap())
        }.values.filter { it.type == type }.getOrNull(index - 1) ?: throw NoSuchFieldException("$type($index) ($from)")
        Ref.putField(instance, field, value)
    }

    fun set(name: String, value: Any?) {
        val map = cachedField.computeIfAbsent(from.name) {
            Ref.getDeclaredFields(from).map {
                it.isAccessible = true
                it.name to it
            }.toMap(ConcurrentHashMap())
        }
        Ref.putField(instance, map[name] ?: throw NoSuchFieldException("$name ($from)"), value)
    }

    fun <T> invoke(name: String, vararg parameter: Any?): T? {
        val map = cachedMethod.computeIfAbsent(from.name) {
            Ref.getDeclaredMethods(from).map {
                it.isAccessible = true
                it.name to it
            }.toMap(ConcurrentHashMap())
        }
        val method = map[name] ?: throw NoSuchMethodException("$name ($from)")
        val obj = if (parameter.isEmpty()) method.invoke(instance) else method.invoke(instance, *parameter)
        return if (obj != null) obj as T else null
    }

    companion object {

        private val cachedField = Maps.newConcurrentMap<String, Map<String, Field>>()
        private val cachedMethod = Maps.newConcurrentMap<String, Map<String, Method>>()

        fun of(instance: Any): Reflex = Reflex(instance.javaClass).instance(instance)

        fun from(clazz: Class<*>): Reflex = Reflex(clazz)

        fun from(clazz: Class<*>, instance: Any?): Reflex = Reflex(clazz).instance(instance)
    }
}