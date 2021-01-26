package io.izzel.taboolib.kotlin

 /**
 * @author sky
 * @since 2020-10-02 03:20
 */
object Indexed {

    fun join(args: Array<String>, start: Int = 0, separator: String = " "): String {
        return args.filterIndexed { index, _ -> index >= start }.joinToString(separator)
    }

    fun <T> subList(list: List<T>, start: Int = 0, end: Int = list.size - 1): List<T> {
        return list.filterIndexed { index, _ -> index in start..end }
    }

    fun <K, V> subMap(map: Map<K, V>, start: Int = 0, end: Int = map.size - 1): List<Map.Entry<K, V>> {
        return map.entries.filterIndexed { index, _ -> index in start..end }
    }
}