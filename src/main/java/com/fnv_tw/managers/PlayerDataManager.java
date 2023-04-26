package com.fnv_tw.managers;

import com.fnv_tw.BetterSkyBlock;
import com.fnv_tw.api.IslandPlayerData;
import com.fnv_tw.configs.Language;
import com.fnv_tw.configs.MainConfig;
import com.fnv_tw.database.Entity.PlayerDataEntity;
import com.fnv_tw.database.PlayerDataDAO;
import com.fnv_tw.utils.PlayerUUIDUtil;
import com.j256.ormlite.support.ConnectionSource;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public class PlayerDataManager implements IslandPlayerData {
    private final PlayerDataDAO playerDataDAO;
    private final Language languageConfig;
    private final MainConfig mainConfig;

    public PlayerDataManager() {
        BetterSkyBlock plugin = BetterSkyBlock.getInstance();
        languageConfig = plugin.getLanguageConfigManager().getConfig();
        mainConfig = plugin.getMainConfigConfigManager().getConfig();
        ConnectionSource connectionSource = BetterSkyBlock.getInstance().getDataBaseManager().getConnectionSource();
        playerDataDAO = PlayerDataDAO.getInstance(connectionSource, PlayerDataEntity.class);
    }
    // api
    public int getPlayerBorderSize(UUID playerUUID) throws Exception {
        try {
            Optional<PlayerDataEntity> playerDataEntity = playerDataDAO.queryForEq("player_uuid", playerUUID).stream().findFirst();
            if (playerDataEntity.isPresent()) {
                return playerDataEntity.get().getBorderSize();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("DATABASE ERROR");
        }
        return mainConfig.getDefaultBorderSize();
    }
    // api
    public boolean addPlayerBorderSize(UUID playerUUID, int addBorderSize) throws Exception{
        try {
            int currentBorderSize = getPlayerBorderSize(playerUUID);
            int playerDataId = getPlayerDataIDByUUID(playerUUID);
            if (playerDataId == -1) {
                PlayerDataEntity playerDataEntity = new PlayerDataEntity();
                playerDataEntity.setPlayerUuid(playerUUID);
                playerDataEntity.setBorderSize(currentBorderSize + addBorderSize);
                playerDataDAO.create(playerDataEntity);
                return true;
            }
            PlayerDataEntity playerDataEntity = playerDataDAO.queryForId(playerDataId);
            playerDataEntity.setBorderSize(currentBorderSize + addBorderSize);
            playerDataDAO.update(playerDataEntity);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("DATABASE ERROR");
        }
        return true;
    }
    // api
    public boolean setPlayerBorderSize(UUID playerUUID, int setBorderSize) throws Exception{
        try {
            int playerDataId = getPlayerDataIDByUUID(playerUUID);
            if (playerDataId == -1) {
                PlayerDataEntity playerDataEntity = new PlayerDataEntity();
                playerDataEntity.setPlayerUuid(playerUUID);
                playerDataEntity.setBorderSize(setBorderSize);
                playerDataDAO.create(playerDataEntity);
                return true;
            }
            PlayerDataEntity playerDataEntity = playerDataDAO.queryForId(playerDataId);
            playerDataEntity.setBorderSize(setBorderSize);
            playerDataDAO.update(playerDataEntity);
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("DATABASE ERROR");
        }
    }
    private int getPlayerDataIDByUUID (UUID playerUUID) throws Exception {
        Optional<PlayerDataEntity> playerDataEntity = playerDataDAO.queryForEq("player_uuid", playerUUID).stream().findFirst();
        return playerDataEntity.map(PlayerDataEntity::getId).orElse(-1);
    }

    public void addPlayerBorderSize(CommandSender commandSender, String playerName, int addBorderSize) {
        if (!(commandSender instanceof ConsoleCommandSender) && !commandSender.hasPermission("betterskyblock.admin")) {
            commandSender.sendMessage(ChatColor.RED + languageConfig.getDoNotHasPermission());
            return;
        }
        try {
            UUID playerUUID = PlayerUUIDUtil.getPlayerUUID(playerName);
            addPlayerBorderSize(playerUUID, addBorderSize);
        } catch (Exception e) {
            e.printStackTrace();
            commandSender.sendMessage(ChatColor.RED + languageConfig.getServerError());
            return;
        }
        commandSender.sendMessage(ChatColor.GOLD + languageConfig.getAddPlayerBorderSizeSuccess());

    }
    public void setPlayerBorderSize(CommandSender commandSender, String playerName, int setBorderSize) {
        if (!(commandSender instanceof ConsoleCommandSender) && !commandSender.hasPermission("betterskyblock.admin")) {
            commandSender.sendMessage(ChatColor.RED + languageConfig.getDoNotHasPermission());
            return;
        }
        try {
            UUID playerUUID = PlayerUUIDUtil.getPlayerUUID(playerName);
            setPlayerBorderSize(playerUUID, setBorderSize);
        } catch (Exception e) {
            e.printStackTrace();
            commandSender.sendMessage(ChatColor.RED + languageConfig.getServerError());
            return;
        }
        commandSender.sendMessage(ChatColor.GOLD + languageConfig.getSetPlayerBorderSizeSuccess());

    }
    public void getPlayerBorderSize(CommandSender commandSender, String playerName) {
        if (!(commandSender instanceof ConsoleCommandSender) && !commandSender.hasPermission("betterskyblock.admin")) {
            commandSender.sendMessage(ChatColor.RED + languageConfig.getDoNotHasPermission());
            return;
        }
        try {
            UUID playerUUID = PlayerUUIDUtil.getPlayerUUID(playerName);
            int playerBorderSize = getPlayerBorderSize(playerUUID);
            commandSender.sendMessage(ChatColor.GOLD + "The Border Size of " + playerName + " is: " + playerBorderSize);
        } catch (Exception e) {
            e.printStackTrace();
            commandSender.sendMessage(ChatColor.RED + languageConfig.getServerError());
        }

    }
}
