package com.fnv_tw.api;

import java.util.UUID;

public interface IslandPlayerData {
    int getPlayerBorderSize(UUID playerUUID) throws Exception;
    boolean addPlayerBorderSize(UUID playerUUID, int addBorderSize) throws Exception;
    boolean setPlayerBorderSize(UUID playerUUID, int setBorderSize) throws Exception;
    int getPlayerIslandLimit(UUID playerUUID) throws Exception;
    boolean addPlayerIslandLimit(UUID playerUUID, int addBorderSize) throws Exception;
    boolean setPlayerIslandLimit(UUID playerUUID, int setBorderSize) throws Exception;

}
