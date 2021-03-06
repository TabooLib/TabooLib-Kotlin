package io.izzel.taboolib.kotlin

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import io.izzel.taboolib.TabooLib
import io.izzel.taboolib.kotlin.Reflex.Companion.reflex
import io.izzel.taboolib.kotlin.Reflex.Companion.reflexInvoke
import io.izzel.taboolib.kotlin.Reflex.Companion.static
import io.izzel.taboolib.kotlin.Reflex.Companion.staticInvoke
import io.izzel.taboolib.module.hologram.Hologram
import io.izzel.taboolib.module.hologram.THologram
import io.izzel.taboolib.module.inject.PlayerContainer
import io.izzel.taboolib.module.locale.TLocale
import io.izzel.taboolib.util.Reflection
import org.bukkit.Bukkit
import org.bukkit.Bukkit.getAdvancement
import org.bukkit.Bukkit.getUnsafe
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@PlayerContainer
private val holographicMap = ConcurrentHashMap<String, MutableMap<String, Holographic>>()
private val toastMap = ConcurrentHashMap<Toast, NamespacedKey>()

fun Player.sendHolographic(location: Location, vararg message: String) {
    val key = "${location.world!!.name},${location.x},${location.y},${location.z}"
    val messages = holographicMap.computeIfAbsent(name) { ConcurrentHashMap() }
    if (messages.containsKey(key)) {
        return
    }
    val holographic = Holographic(this, location, TLocale.Translate.setColored(message.toList()))
    messages[key] = holographic
    Tasks.delay(40) {
        messages.remove(key)
        holographic.cancel()
    }
}

fun Player.sendToast(icon: Material, message: String, frame: ToastFrame = ToastFrame.TASK, background: ToastBackground = ToastBackground.ADVENTURE) {
    Tasks.task {
        val cache = Toast(icon, message, frame)
        val namespaceKey = toastMap.computeIfAbsent(cache) {
            inject(
                NamespacedKey(TabooLib.getPlugin(), "toast_${UUID.randomUUID()}"),
                toJsonToast(icon.key.toString(), message, frame, background)
            )
        }
        // 注册成就
        grant(this, namespaceKey)
        // 延迟注销，否则会出问题
        Tasks.delay(20) {
            revoke(this, namespaceKey)
        }
    }
}

private fun grant(player: Player, key: NamespacedKey) {
    val advancement = getAdvancement(key)!!
    if (!player.getAdvancementProgress(advancement).isDone) {
        player.getAdvancementProgress(advancement).remainingCriteria.forEach {
            player.getAdvancementProgress(advancement).awardCriteria(it)
        }
    }
}

private fun revoke(player: Player, key: NamespacedKey) {
    val advancement = getAdvancement(key)
    if (advancement != null && player.getAdvancementProgress(advancement).isDone) {
        player.getAdvancementProgress(advancement).awardedCriteria.forEach {
            player.getAdvancementProgress(advancement).revokeCriteria(it)
        }
    }
}

private fun inject(key: NamespacedKey, toast: String): NamespacedKey {
    if (getAdvancement(key) == null) {
        val localMinecraftKey = obcClass("util.CraftNamespacedKey").staticInvoke<Any>("toMinecraft", key)
        val localJsonObject = nmsClass("AdvancementDataWorld").static<Gson>("DESERIALIZER")!!.fromJson(toast, JsonElement::class.java).asJsonObject
        val localMinecraftServer = nmsClass("MinecraftServer").staticInvoke<Any>("getServer")!!
        val localLootPredicateManager = localMinecraftServer.reflexInvoke<Any>("getLootPredicateManager")
        val localSerializedAdvancement = nmsClass("Advancement\$SerializedAdvancement").staticInvoke<Any>(
            "a",
            localJsonObject,
            Reflection.instantiateObject(nmsClass("LootDeserializationContext"), localMinecraftKey, localLootPredicateManager)
        )
        if (localSerializedAdvancement != null) {
            localMinecraftServer.reflexInvoke<Any>("getAdvancementData")!!.reflex<Any>("REGISTRY")!!.reflexInvoke<Any>("a", HashMap(Collections.singletonMap(localMinecraftKey, localSerializedAdvancement)))
        }
    }
    return key
}

private fun eject(key: NamespacedKey): NamespacedKey {
    try {
        getUnsafe().removeAdvancement(key)
        val console = Bukkit.getServer().reflex<Any>("console")!!
        val advancements = console.reflexInvoke<Any>("getAdvancementData")!!.reflex<MutableMap<Any, Any>>("REGISTRY/advancements")!!
        for ((k, v) in advancements) {
            if (v.reflex<Any>("name/key") == key.key) {
                advancements.remove(k)
                break
            }
        }
    } catch (t: Throwable) {
        t.printStackTrace()
    }
    return key
}

private fun toJsonToast(icon: String, title: String, frame: ToastFrame, background: ToastBackground): String {
    val json = JsonObject()
    json.add("display", JsonObject().run {
        this.add("icon", JsonObject().run {
            this.addProperty("item", icon)
            this
        })
        this.addProperty("title", title)
        this.addProperty("description", "")
        this.addProperty("background", background.url)
        this.addProperty("frame", frame.name.toLowerCase())
        this.addProperty("announce_to_chat", false)
        this.addProperty("show_toast", true)
        this.addProperty("hidden", true)
        this
    })
    json.add("criteria", JsonObject().run {
        this.add("IMPOSSIBLE", JsonObject().run {
            this.addProperty("trigger", "minecraft:impossible")
            this
        })
        this
    })
    return json.toString()
}

enum class ToastFrame {

    TASK, GOAL, CHALLENGE
}

enum class ToastBackground(val url: String) {

    ADVENTURE("minecraft:textures/gui/advancements/backgrounds/adventure.png"), END("minecraft:textures/gui/advancements/backgrounds/end.png")
}

private class Toast(val material: Material, val message: String, val frame: ToastFrame)

private class Holographic(val player: Player, val location: Location, val message: List<String>) {

    val holograms = ArrayList<Hologram>()
    val time = System.currentTimeMillis()

    init {
        message.forEachIndexed { index, content ->
            holograms.add(THologram.create(location.clone().add(0.0, (((message.size - 1) - index) * 0.3), 0.0), content).also {
                if (content.isNotEmpty()) {
                    it.addViewer(player)
                    it.flash(content.toPrinted("_"), 1)
                }
            })
        }
    }

    fun cancel() {
        holograms.forEach { it.delete() }
    }
}