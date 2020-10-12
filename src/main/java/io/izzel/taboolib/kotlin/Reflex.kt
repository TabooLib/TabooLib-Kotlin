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
        val obj = method.invoke(instance, parameter)
        return if (obj != null) obj as T else null
    }

    companion object {

        private val cachedField = Maps.newConcurrentMap<String, Map<String, Field>>()
        private val cachedMethod = Maps.newConcurrentMap<String, Map<String, Method>>()

        fun from(clazz: Class<*>): Reflex = Reflex(clazz)

        fun from(clazz: Class<*>, instance: Any?): Reflex = Reflex(clazz).instance(instance)
    }
}