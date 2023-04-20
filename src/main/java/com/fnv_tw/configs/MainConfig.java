package com.fnv_tw.configs;

import com.fnv_tw.utils.SerializerUtil;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.util.Vector;

@Data
public class MainConfig {
    private boolean bungeeCord = false;
    private String currentBungeeCordServerName = "";
    private boolean recordBorderSizeWithPermission = true;
    private boolean islandLimitPerPlayer = false;
    private int islandLimit = 1;
    private int defaultBorderSize = 100;
    private int unloadIslandAfterIdleSeconds = 30;
    private String defaultWorldName = "world";
    private String defaultNetherName = "world_nether";
    private String defaultTheEndName = "world_the_end";
    private String defaultWorldSpawn = "{\"x\":0.0,\"y\":0.0,\"z\":0.0}";
    private boolean defaultPublicIsland = false;
    private boolean voidTeleport = true;
}
