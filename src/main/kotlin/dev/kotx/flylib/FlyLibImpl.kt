/*
 * Copyright (c) 2021 kotx__.
 * Twitter: https://twitter.com/kotx__
 */

package dev.kotx.flylib

import dev.kotx.flylib.command.*
import org.bukkit.event.*
import org.bukkit.event.server.*
import org.bukkit.plugin.*
import org.bukkit.plugin.java.*
import org.slf4j.*

internal class FlyLibImpl(override val plugin: JavaPlugin, commands: List<Command>) : FlyLib {
    private val logger = LoggerFactory.getLogger("FlyLib Reloaded")
    override val commandHandler = CommandHandlerImpl(this, commands)

    init {
        logger.info("Loading FlyLib...")

        register<PluginEnableEvent> {
            if (it.plugin == plugin)
                enable()
        }

        register<PluginDisableEvent> {
            if (it.plugin == plugin)
                disable()
        }

        register<ServerLoadEvent> {
            load()
        }
    }

    private fun enable() {
        commandHandler.enable()
        println("""
              ______ _
             |  ____| |
             | |__  | |      FlyLib Reloaded v0.2.43
             |  __| | |      by Kotx
             | |    | |____
             |_|    |______|
        """.trimIndent())
    }

    private fun disable() {
        logger.info("Unloading FlyLib...")
        commandHandler.disable()
        logger.info("FlyLib unloaded successfully.")
    }

    private fun load() {
        commandHandler.load()
    }

    private inline fun <reified T : Event> register(crossinline action: (T) -> Unit) {
        val handlerList = T::class.java.methods.find { it.name == "getHandlerList" }!!.invoke(null) as HandlerList
        val listener = RegisteredListener(
            object : Listener {},
            { _, event -> action(event as T) },
            EventPriority.NORMAL,
            plugin,
            false
        )

        handlerList.register(listener)
    }
}