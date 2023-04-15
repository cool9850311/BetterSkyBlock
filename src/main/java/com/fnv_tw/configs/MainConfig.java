package com.fnv_tw.configs;

import lombok.Data;

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

}
