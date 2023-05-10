package com.fnv_tw.api;

import java.util.UUID;

public interface IslandPlayerData {
    int getPlayerBorderSize(UUID playerUUID) throws Exception;
    void addPlayerBorderSize(UUID playerUUID, int addBorderSize) throws Exception;
    void setPlayerBorderSize(UUID playerUUID, int setBorderSize) throws Exception;
    int getPlayerIslandLimit(UUID playerUUID) throws Exception;
    void addPlayerIslandLimit(UUID playerUUID, int addBorderSize) throws Exception;
    void setPlayerIslandLimit(UUID playerUUID, int setBorderSize) throws Exception;

}
