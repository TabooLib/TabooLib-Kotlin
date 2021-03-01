package io.izzel.taboolib.kotlin

fun MutableList<Any>.setSafely(index: Int, element: Any, def: Any = "") {
    while (size <= index) {
        add(def)
    }
    this[index] = element
}

fun MutableList<Any>.addSafely(index: Int, element: Any, def: Any = "") {
    while (size <= index) {
        add(def)
    }
    this.add(index, element)
}