package com.fnv_tw.configs;

import lombok.Data;

@Data
public class MainConfig {
    private boolean bungeeCord = false;
    private String currentBungeeCordServerName = "";
    private boolean recordBorderSizeWithPermission = true;
    private boolean islandLimitPerPlayer = false;
    private int islandLimit = 1;
    private int defaultBorderSize = 80;
    private int unloadIdleIslandTaskInterval = 30;
    private String defaultWorldName = "world";
    private String defaultNetherName = "world_nether";
    private String defaultTheEndName = "world_the_end";
    private String defaultWorldSpawn = "{\"x\":0.0,\"y\":0.0,\"z\":0.0}";
    private boolean defaultPublicIsland = false;
    private boolean voidTeleport = true;
}
