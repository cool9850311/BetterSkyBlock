package com.fnv_tw.gui;

import com.fnv_tw.BetterSkyBlock;
import com.fnv_tw.utils.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public abstract class AbstractGUI  implements Listener {
    public Player player;
    public int size;
    public Inventory inv;
    public ItemStack[] guiItems;
    public String name;


    @EventHandler
    public void InventoryClickEvent(InventoryClickEvent event) {
        if (!event.getInventory().equals(inv)) return;
        event.setCancelled(true);
        final ItemStack clickedItem = event.getCurrentItem();

        // verify current item is not null
        if (clickedItem == null || clickedItem.getType().isAir()) return;

        ItemStack item = event.getCurrentItem();
        clickItemOperation(item);
    }
    @EventHandler
    public void onInventoryClick(final InventoryDragEvent e) {
        if (e.getInventory().equals(inv)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event){
        if (event.getInventory().equals(inv)) {
            close(false);
        }
    }
    public void close(boolean close){
        HandlerList.unregisterAll(this);
        // Check for items
        this.player.updateInventory();
        Bukkit.getScheduler().runTaskLater(BetterSkyBlock.getInstance(), this.player::updateInventory, 1);
        if (close) this.player.closeInventory();

    }
    public void open(){
        BetterSkyBlock.getInstance().getServer().getPluginManager().registerEvents(this,BetterSkyBlock.getInstance());
        this.inv = Bukkit.createInventory(player, this.size, ChatUtil.format(this.name));
        inv.setContents(this.guiItems);
        player.openInventory(inv);
    }

    public abstract void clickItemOperation(ItemStack item);
}
