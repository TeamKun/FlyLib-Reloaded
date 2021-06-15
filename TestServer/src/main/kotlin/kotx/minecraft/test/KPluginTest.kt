/*
 * Copyright (c) 2021 kotx__.
 * Twitter: https://twitter.com/kotx__
 */

package kotx.minecraft.test

import dev.kotx.flylib.*
import dev.kotx.flylib.command.*
import dev.kotx.flylib.command.internal.*
import org.bukkit.plugin.java.*

class KTestPlugin : JavaPlugin() {
    override fun onEnable() {
        flyLib {
            command {
                defaultConfiguration {
                    permission(Permission.OP)
                }

                register(KPrintNumberCommand)
                register(KTabCompleteCommand)
                register(KParentCommand)
            }
        }
    }
}

object KPrintNumberCommand : Command("printnumber") {
    override val usages: MutableList<Usage> = mutableListOf(
        Usage(
            arrayOf(Argument.Integer("number", min = 0, max = 10))
        ) {
            sendMessage("You sent ${args.first()}!")
        }
    )
}

object KTabCompleteCommand : Command("tabcomplete") {
    override val usages: MutableList<Usage> = mutableListOf(
        Usage(
            arrayOf(
                Argument.Selection("mode", "active", "inactive"),
                Argument.Player("target"),
                Argument.Position("position")
            )
        )
    )
}

object KParentCommand : Command("parent") {
    override val children: MutableList<Command> = mutableListOf(ChildrenCommand)

    object ChildrenCommand : Command("children") {
        override fun CommandContext.execute() {
            sendMessage("You executed children command!")
        }
    }
}