package com.fnv_tw.listeners;

import com.fnv_tw.BetterSkyBlock;
import com.fnv_tw.configs.Language;
import com.fnv_tw.configs.MainConfig;
import com.fnv_tw.managers.IslandManager;
import com.fnv_tw.utils.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
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
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (!mainConfig.isVoidTeleport()) {
            return;
        }
        World world = e.getPlayer().getWorld();
        if (!islandManager.isInIslandWorld(world.getName())) {
            return;
        }
        if (e.getPlayer().getLocation().getY() > LocationUtil.getMinHeight(e.getPlayer().getWorld())){
            return;
        }
        int islandId = Integer.parseInt(world.getName().split("_")[1]);
        String islandName = plugin.getIslandManager().getIslandNameById(islandId);
        islandManager.teleportToIsland(e.getPlayer(), islandName, false);

    }
}
