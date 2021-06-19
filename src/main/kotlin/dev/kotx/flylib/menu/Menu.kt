/*
 * Copyright (c) 2021 kotx__.
 * Twitter: https://twitter.com/kotx__
 */

package dev.kotx.flylib.menu

import dev.kotx.flylib.FlyLibComponent
import dev.kotx.flylib.menu.Menu.Action
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.inject

abstract class Menu(
    size: Size,
    val items: MutableList<MenuItem>
) : Listener, FlyLibComponent {
    internal var player: Player? = null

    private val plugin by inject<JavaPlugin>()
    protected val inventory = Bukkit.createInventory(null, size.size)

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    fun display(player: Player) {
        this.player = player

        display()
    }

    protected abstract fun display()

    @EventHandler
    fun handleClick(event: InventoryClickEvent) {
        if (event.whoClicked.uniqueId != player?.uniqueId) return
        if (event.inventory != inventory) return

        onClick(event)
    }

    abstract fun onClick(event: InventoryClickEvent)

    abstract class Builder<T : Menu> {
        protected val items = mutableListOf<MenuItem>()
        var size: Size = Size.CHEST

        fun item(index: Int, itemStack: ItemStack, onClick: Action = Action { }) {
            if (index >= size.size)
                throw IllegalArgumentException("index provided $index exceeds size: ${size.size}")

            items.removeIf { it.index == index }
            items.add(MenuItem(index, itemStack, onClick))
        }

        fun item(x: Int, y: Int, itemStack: ItemStack, onClick: Action = Action { }) {
            val index = (y - 1) * 9 + (x - 1)
            item(index, itemStack, onClick)
        }

        abstract fun build(): T
    }

    class MenuItem(
        val index: Int,
        val stack: ItemStack,
        val onClick: Action = Action { }
    )

    fun interface Action {
        fun handleClick(event: InventoryClickEvent)
    }

    enum class Size(val size: Int) {
        CHEST(27),
        LARGE_CHEST(56)
    }
}