package com.fnv_tw.listeners;

import com.fnv_tw.BetterSkyBlock;
import com.fnv_tw.configs.Language;
import com.fnv_tw.configs.MainConfig;
import com.fnv_tw.managers.IslandManager;
import com.fnv_tw.utils.LocationUtil;
import com.fnv_tw.utils.SerializerUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

public class IslandPlayerListener implements Listener {
    private final BetterSkyBlock plugin;
    private final MainConfig mainConfig;
    private final IslandManager islandManager;
    private final Language languageConfig;

    public IslandPlayerListener() {
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
        if (!islandManager.isPlayerTrusted(e.getPlayer(), islandName) && !islandManager.isPublicIsland(islandName) && !e.getPlayer().hasPermission(IslandManager.ADMIN_PERMISSION)){
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
    // PlayerSpawnLocationEvent
    @EventHandler
    public void onSpawnLocation(PlayerSpawnLocationEvent e) {
        // FIXME: USE BETTER WAY to deal with player get spawn in world 000 after their island get unlaod
        World world = Bukkit.getWorld(mainConfig.getDefaultWorldName());
        if (!e.getSpawnLocation().getWorld().getName().equals(world.getName())){
            return;
        }
        if (e.getSpawnLocation().distance(new Location(e.getSpawnLocation().getWorld(), 0,0,0)) < 100) {
            if (LocationUtil.isSafe(e.getSpawnLocation())) {
                return;
            }
            // Bukkit.getLogger().info("PlayerSpawnLocationEvent: IF CLAUSE");

            Vector position = SerializerUtil.deserialize(mainConfig.getDefaultWorldSpawn(),Vector.class);
            Location location = new Location(world,position.getX(),position.getY(),position.getZ());
            if (LocationUtil.isSafe(location)) {
                e.setSpawnLocation(location);
                return;
            }
            e.setSpawnLocation(LocationUtil.getSafeLocation(location));
        }
    }
}
