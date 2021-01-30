package io.izzel.taboolib.kotlin.kether

import org.bukkit.event.Event
import kotlin.reflect.KClass

/**
 * @Author sky
 * @Since 2020-08-30 19:22
 */
class EventOperator<T : Event>(val event: KClass<out T>) {

    val reader = HashMap<String, Reader<T>>()
    val writer = HashMap<String, Writer<T>>()

    fun unit(name: String, builder: Builder<T>.() -> Unit): EventOperator<T> {
        Builder(name, this).also {
            builder(it)
            reader[name] = Reader(it.reader)
            writer[name] = Writer(it.writer)
        }
        return this
    }

    @Suppress("UNCHECKED_CAST")
    fun read(name: String, event: Event): Any? {
        val reader = reader[name] as? Reader<Event> ?: throw IllegalStateException("Operator \"$name\" not supported.")
        return reader.func(event)
    }

    @Suppress("UNCHECKED_CAST")
    fun readUnsafe(name: String, event: Event): Any? {
        val reader = reader[name] as? Reader<Event> ?: return null
        return reader.func(event)
    }

    @Suppress("UNCHECKED_CAST")
    fun write(name: String, event: Event, value: Any?) {
        val writer = writer[name] as? Writer<Event> ?: throw IllegalStateException("Operator \"$name\" not supported.")
        writer.func(event, value)
    }

    @Suppress("UNCHECKED_CAST")
    fun writeUnsafe(name: String, event: Event, value: Any?) {
        val writer = writer[name] as? Writer<Event> ?: return
        writer.func(event, value)
    }

    class Reader<T : Event>(val func: (T) -> Any?)

    class Writer<T : Event>(val func: (T, Any?) -> Unit)

    class Builder<T : Event>(val name: String, val event: EventOperator<T>) {

        var reader: (T) -> Any? = { null }
        var writer: (T, Any?) -> Unit = { _, _ -> }
    }
}