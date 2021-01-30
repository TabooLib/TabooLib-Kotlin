package io.izzel.taboolib.kotlin.kether

import io.izzel.taboolib.kotlin.Tasks
import org.bukkit.Bukkit
import java.util.concurrent.Executor

object ScriptSchedulerExecutor : Executor {

    override fun execute(command: Runnable) {
        if (Bukkit.isPrimaryThread()) {
            command.run()
        } else {
            Tasks.task { command.run() }
        }
    }
}