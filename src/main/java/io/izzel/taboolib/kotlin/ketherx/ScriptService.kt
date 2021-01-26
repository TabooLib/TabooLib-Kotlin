package io.izzel.taboolib.kotlin.ketherx

import com.google.common.collect.ImmutableMap
import com.google.common.collect.Multimap
import io.izzel.kether.common.api.*
import io.izzel.taboolib.TabooLib
import io.izzel.taboolib.module.db.local.SecuredFile
import io.izzel.taboolib.module.inject.TSchedule
import io.izzel.taboolib.util.Files
import io.izzel.taboolib.util.IO
import io.izzel.taboolib.util.Strings
import java.io.File
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import kotlin.collections.HashMap


/**
 * @author IzzelAliz
 */
object ScriptService : QuestService<ScriptContext> {

    private val registry = DefaultRegistry()
    private val syncExecutor = ScriptSchedulerExecutor
    private val asyncExecutor = Executors.newScheduledThreadPool(2)
    private val locale = SecuredFile.loadConfiguration(IO.readFully(Files.getTabooLibResource("__resources__/ketherx.yml"), StandardCharsets.UTF_8))

    val workspace = HashMap<String, Workspace>()
    val mainspace = Workspace(File(TabooLib.getPlugin().dataFolder, "script"))

    init {
        workspace["TabooLib"] = mainspace
    }

    @TSchedule
    fun loadAll() {
        workspace.values.forEach {
            try {
                it.loadAll()
            } catch (e: Exception) {
                println("[TabooLib] An error occurred while loading the script")
                e.printStackTrace()
            }
        }
    }

    override fun getRegistry(): QuestRegistry {
        return registry
    }

    override fun getQuest(id: String): Optional<Quest> {
        return Optional.ofNullable(mainspace.scripts[id])
    }

    override fun getQuestSettings(id: String): Map<String, Any> {
        return Collections.unmodifiableMap(mainspace.scriptsSetting.getOrDefault(id, ImmutableMap.of()))
    }

    override fun getQuests(): Map<String, Quest> {
        return Collections.unmodifiableMap(mainspace.scripts)
    }

    override fun getRunningQuests(): Multimap<String, ScriptContext> {
        return mainspace.runningScripts
    }

    override fun getRunningQuests(playerIdentifier: String): List<ScriptContext> {
        return Collections.unmodifiableList(mainspace.runningScripts[playerIdentifier])
    }

    override fun getExecutor(): Executor {
        return syncExecutor
    }

    override fun getAsyncExecutor(): ScheduledExecutorService {
        return asyncExecutor
    }

    override fun getLocalizedText(node: String, vararg params: Any): String {
        return Strings.replaceWithOrder(locale.getString(node), *params)
    }

    override fun getStorage(): QuestStorage {
        return KetherX.storage!!
    }

    override fun startQuest(context: ScriptContext) {
        mainspace.runScript(UUID.randomUUID().toString(), context)
    }

    override fun terminateQuest(context: ScriptContext) {
        mainspace.terminateScript(context)
    }
}