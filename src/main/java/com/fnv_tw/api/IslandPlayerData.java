package com.fnv_tw.api;

import java.util.UUID;

public interface IslandPlayerData {
    int getPlayerBorderSize(UUID playerUUID) throws Exception;
    boolean addPlayerBorderSize(UUID playerUUID, int addBorderSize) throws Exception;
    boolean setPlayerBorderSize(UUID playerUUID, int setBorderSize) throws Exception;

}
