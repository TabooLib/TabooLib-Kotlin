package io.izzel.taboolib.kotlin

object SerializerAdapter {

    val map = HashMap<Class<*>, Any>()

    fun registerTypeHierarchyAdapter(baseType: Class<*>, typeAdapter: Any) {
        map[baseType] = typeAdapter
    }
}