/*
 * Copyright (c) 2021 kotx__.
 * Twitter: https://twitter.com/kotx__
 */

package dev.kotx.flylib.command

import com.mojang.brigadier.builder.*
import com.mojang.brigadier.tree.*
import dev.kotx.flylib.*
import net.minecraft.server.v1_16_R3.*
import org.bukkit.*
import org.bukkit.command.*
import org.bukkit.craftbukkit.v1_16_R3.*
import org.bukkit.craftbukkit.v1_16_R3.command.*
import java.lang.invoke.*

typealias BukkitPermission = org.bukkit.permissions.Permission

internal class CommandHandlerImpl(override val flyLib: FlyLibImpl, private val commands: List<Command>) : CommandHandler {
    internal fun enable() {
        val commandDispatcher = ((Bukkit.getServer() as CraftServer).server as MinecraftServer).commandDispatcher
        val commandNodes = MethodHandles.privateLookupIn(SimpleCommandMap::class.java, MethodHandles.lookup())
            .findVarHandle(SimpleCommandMap::class.java, "knownCommands", MutableMap::class.java)
            .get(Bukkit.getCommandMap()) as MutableMap<String, org.bukkit.command.Command>

        commands.forEach { command ->
            val cmdArgument = getArgument(command.name, command)
            commandDispatcher.a().root.addChild(cmdArgument)
            commandNodes[command.name] = VanillaCommandWrapper(commandDispatcher, cmdArgument)

            command.aliases.forEach { alias ->
                val aliasArgument = getArgument(alias, command)
                commandDispatcher.a().root.addChild(aliasArgument)
                commandNodes[alias] = VanillaCommandWrapper(commandDispatcher, aliasArgument)
            }
        }

        val permissions = (commands.map { it.getCommandPermission() } + commands.flatMap { cmd -> cmd.usages.map { cmd.getUsagePermission(it) } }).distinct()
        permissions.forEach {
            flyLib.plugin.server.pluginManager.addPermission(BukkitPermission(it.first, it.second.defaultPermission))
        }
    }

    internal fun disable() {
        val root = ((Bukkit.getServer() as CraftServer).server as MinecraftServer).commandDispatcher.a().root
        val commandNodes = MethodHandles.privateLookupIn(SimpleCommandMap::class.java, MethodHandles.lookup())
            .findVarHandle(SimpleCommandMap::class.java, "knownCommands", MutableMap::class.java)
            .get(Bukkit.getCommandMap()) as MutableMap<String, org.bukkit.command.Command>

        commands.forEach { cmd ->
            root.removeCommand(cmd.name)
            root.removeCommand("minecraft:${cmd.name}")
            commandNodes.remove(cmd.name)
            commandNodes.remove("minecraft:${cmd.name}")

            cmd.aliases.forEach {
                root.removeCommand(it)
                root.removeCommand("minecraft:$it")
                commandNodes.remove(it)
                commandNodes.remove("minecraft:$it")
            }

            flyLib.plugin.server.pluginManager.removePermission(cmd.getCommandPermission().first)

            cmd.usages.forEach {
                flyLib.plugin.server.pluginManager.removePermission(cmd.getUsagePermission(it).first)
            }
        }
    }

    internal fun load() {
        commands.forEach { cmd ->
            flyLib.plugin.server.commandMap.getCommand(cmd.name)?.permission = cmd.getCommandPermission().first
            flyLib.plugin.server.commandMap.getCommand("minecraft:${cmd.name}")?.permission = cmd.getCommandPermission().first
            cmd.aliases.forEach {
                flyLib.plugin.server.commandMap.getCommand(it)?.permission = cmd.getCommandPermission().first
                flyLib.plugin.server.commandMap.getCommand("minecraft:$it")?.permission = cmd.getCommandPermission().first
            }
        }
    }

    private fun getArgument(name: String, command: Command): LiteralCommandNode<CommandListenerWrapper> {
        val commandArgument = LiteralArgumentBuilder.literal<CommandListenerWrapper>(name).apply {
            requires {
                it.bukkitSender.hasPermission(command.getCommandPermission().first)
            }

            executes { ctx ->
                val context = CommandContext(
                    flyLib.plugin,
                    command,
                    ctx.source.bukkitSender,
                    ctx.source.bukkitWorld,
                    ctx.input
                )

                command.apply { context.execute() }

                1
            }
        }

        command.usages.forEach { usage ->
            var usageArgument: ArgumentBuilder<CommandListenerWrapper, *>? = null
            usage.arguments.reversed().forEach { argument ->
                val argumentBuilder: ArgumentBuilder<CommandListenerWrapper, *> = if (argument.type == null)
                    LiteralArgumentBuilder.literal(argument.name)
                else
                    RequiredArgumentBuilder.argument(argument.name, argument.type)

                argumentBuilder.apply {
                    requires {
                        it.bukkitSender.hasPermission(command.getUsagePermission(usage).first)
                    }

                    executes { ctx ->
                        val context = CommandContext(
                            flyLib.plugin,
                            command,
                            ctx.source.bukkitSender,
                            ctx.source.bukkitWorld,
                            ctx.input
                        )

                        usage.action?.invoke(context) ?: command.apply { context.execute() }

                        1
                    }

                    if (this is RequiredArgumentBuilder<CommandListenerWrapper, *> && argument.suggestion != null) suggests { context, builder ->
                        val suggestions = SuggestionBuilder(
                            flyLib.plugin,
                            command,
                            context.source.bukkitSender,
                            context.input,
                            emptyList()
                        ).apply(argument.suggestion!!).build()

                        suggestions.forEach {
                            if (it.tooltip == null)
                                builder.suggest(it.content)
                            else
                                builder.suggest(it.content) { it.tooltip }
                        }

                        builder.buildFuture()
                    }
                }

                if (usageArgument != null)
                    argumentBuilder.then(usageArgument)

                usageArgument = argumentBuilder
            }

            if (usageArgument != null)
                commandArgument.then(usageArgument)
        }

        command.children.forEach {
            commandArgument.then(getArgument(it.name, it))
        }

        return commandArgument.build()
    }

    private fun Command.getCommandPermission() = ((permission ?: Permission.OP).name
        ?: flyLib.plugin.name.lowercase() +
        ".command" +
        ".${fullCommand.joinToString(".") { it.name }.lowercase()}") to (permission ?: Permission.OP)

    private fun Command.getUsagePermission(usage: Usage) = ((usage.permission ?: permission ?: Permission.OP).name
        ?: flyLib.plugin.name.lowercase() +
        ".command" +
        ".${fullCommand.joinToString(".") { it.name }.lowercase()}" +
        ".${usage.arguments.joinToString(".") { it.name }.lowercase()}") to (usage.permission ?: permission ?: Permission.OP)

    private val Command.fullCommand: List<Command>
        get() {
            val commands = mutableListOf<Command>()
            var current: Command? = this

            do {
                commands.add(current!!)
                current = current.parent
            } while (current != null)

            return commands.reversed()
        }
}