package io.izzel.taboolib.kotlin.kether

import io.izzel.kether.common.api.QuestActionParser
import io.izzel.taboolib.TabooLibLoader
import io.izzel.taboolib.module.inject.TInjectHelper
import io.izzel.taboolib.util.Ref
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin

/**
 * TabooLibKotlin
 * io.izzel.taboolib.kotlin.kether.KetherLoader
 *
 * @author sky
 * @since 2021/2/6 3:33 下午
 */
class KetherLoader : TabooLibLoader.Loader, Listener {

    override fun activeLoad(plugin: Plugin, pluginClass: Class<*>) {
        pluginClass.declaredMethods.forEach {
            if (it.isAnnotationPresent(KetherParser::class.java)) {
                it.isAccessible = true
                val instance = TInjectHelper.getInstance(it, pluginClass, plugin)
                val parser = it.getAnnotation(KetherParser::class.java)
                try {
                    if (parser.value.isEmpty()) {
                        it.invoke(instance[0])
                    } else {
                        parser.value.forEach { name ->
                            Kether.addAction(name, it.invoke(instance[0]) as QuestActionParser, parser.namespace)
                        }
                    }
                } catch (e: IllegalArgumentException) {
                    val unsafe = Ref.getUnsafe().allocateInstance(pluginClass)
                    if (parser.value.isEmpty()) {
                        it.invoke(unsafe)
                    } else {
                        parser.value.forEach { name ->
                            Kether.addAction(name, it.invoke(unsafe) as QuestActionParser, parser.namespace)
                        }
                    }
                }
            }
        }
    }

    override fun unload(plugin: Plugin, pluginClass: Class<*>) {
        pluginClass.declaredMethods.forEach {
            if (it.isAnnotationPresent(KetherParser::class.java)) {
                val parser = it.getAnnotation(KetherParser::class.java)
                if (parser.value.isEmpty()) {
                    parser.release.forEach { name ->
                        Kether.removeAction(name, parser.namespace)
                    }
                } else {
                    parser.value.forEach { name ->
                        Kether.removeAction(name, parser.namespace)
                    }
                }
            }
        }
    }
}