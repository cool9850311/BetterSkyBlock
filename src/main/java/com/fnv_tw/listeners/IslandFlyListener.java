package com.fnv_tw.listeners;

import com.fnv_tw.BetterSkyBlock;
import com.fnv_tw.configs.Language;
import com.fnv_tw.configs.MainConfig;
import com.fnv_tw.managers.IslandManager;
import com.fnv_tw.utils.LocationUtil;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class IslandFlyListener implements Listener {
    private final BetterSkyBlock plugin;
    private final MainConfig mainConfig;
    private final IslandManager islandManager;
    private final Language languageConfig;
    public IslandFlyListener() {
        plugin = BetterSkyBlock.getInstance();
        mainConfig = plugin.getMainConfigConfigManager().getConfig();
        languageConfig = plugin.getLanguageConfigManager().getConfig();
        islandManager = plugin.getIslandManager();
    }
    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent e) {
        setIfPlayerAllowFly(e.getPlayer());
    }
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        setIfPlayerAllowFly(e.getPlayer());

    }
    private void setIfPlayerAllowFly(Player player) {

        if (!player.hasPermission("betterskyblock.fly") || player.getGameMode() == GameMode.SPECTATOR || player.hasPermission("betterskyblock.bypass.fly")) {
            return;
        }
        World world = player.getWorld();
        if (islandManager.isInIslandWorld(world.getName())) {
            if (!player.getAllowFlight()) {
                player.setAllowFlight(true);
                player.sendRawMessage(ChatColor.GOLD + languageConfig.getIslandFlyEnable());
            }
            return;
        }
        if (player.getAllowFlight()) {
            player.setAllowFlight(false);
            player.sendRawMessage(ChatColor.GOLD + languageConfig.getIslandFlyDisable());
        }


    }
}
