/*
 * Copyright (c) 2021 kotx__.
 * Twitter: https://twitter.com/kotx__
 */

package dev.kotx.flylib.menu.menus

import dev.kotx.flylib.FlyLibComponent
import dev.kotx.flylib.menu.Menu
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent

class ChestMenu(
        size: Size,
        items: MutableList<MenuItem>
) : Menu(size, items), FlyLibComponent {

    override fun display() {
        items.forEach {
            inventory.setItem(it.index, it.stack)
        }

        player?.openInventory(inventory)
    }

    override fun onClick(event: InventoryClickEvent) {
        event.isCancelled = true

        event.whoClicked.closeInventory()

        items.firstOrNull { it.index == event.slot }?.also { it.onClick.handleClick(event) }
    }

    class Builder : Menu.Builder<ChestMenu>() {
        override fun build(): ChestMenu = ChestMenu(size, items)

        fun interface Action {
            fun Builder.initialize()
        }
    }

    companion object {
        @JvmStatic
        fun display(player: Player, block: Builder.Action) {
            Builder().apply { block.apply { initialize() } }.build().display(player)
        }

        @JvmStatic
        fun menu(block: ChestMenu.Builder.Action) = ChestMenu.Builder().apply { block.apply { initialize() } }.build()
    }
}