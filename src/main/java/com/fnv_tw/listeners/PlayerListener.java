package com.fnv_tw.listeners;

import com.fnv_tw.BetterSkyBlock;
import com.fnv_tw.configs.Language;
import com.fnv_tw.configs.MainConfig;
import com.fnv_tw.managers.IslandManager;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PlayerListener implements Listener {
    private final BetterSkyBlock plugin;
    private final MainConfig mainConfig;
    private final IslandManager islandManager;
    private final Language languageConfig;

    public PlayerListener() {
        plugin = BetterSkyBlock.getInstance();
        mainConfig = plugin.getMainConfigConfigManager().getConfig();
        languageConfig = plugin.getLanguageConfigManager().getConfig();
        islandManager = plugin.getIslandManager();
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent e) {
        String worldName = e.getTo().getWorld().getName();
        if (!islandManager.isInIslandWorld(worldName)){
            return;
        }
        int islandId = Integer.parseInt(e.getTo().getWorld().getName().split("_")[1]);
        String islandName = plugin.getIslandManager().getIslandNameById(islandId);
        if (!islandManager.isPlayerTrusted(e.getPlayer(), islandName) && !islandManager.isPublicIsland(islandName)){
            e.getPlayer().sendMessage(ChatColor.RED + languageConfig.getNotInIslandTrustList());
            e.setCancelled(true);
        }
    }
}
