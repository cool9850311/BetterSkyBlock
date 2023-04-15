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
}
