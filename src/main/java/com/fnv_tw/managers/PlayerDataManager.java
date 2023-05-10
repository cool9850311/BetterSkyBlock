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
    public int getPlayerIslandLimit(UUID playerUUID) throws Exception {
        try {
            Optional<PlayerDataEntity> playerDataEntity = playerDataDAO.queryForEq("player_uuid", playerUUID).stream().findFirst();
            if (playerDataEntity.isPresent()) {
                int islandLimit = playerDataEntity.get().getIslandNumberLimit();
                if (islandLimit < mainConfig.getIslandLimit()) {
                    islandLimit = mainConfig.getIslandLimit();
                }
                return islandLimit;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("DATABASE ERROR");
        }
        return mainConfig.getIslandLimit();
    }
    //api
    public void addPlayerIslandLimit(UUID playerUUID, int addIslandLimit) throws Exception{
        try {
            int currentPlayerIslandLimit = getPlayerIslandLimit(playerUUID);
            int playerDataId = getPlayerDataIDByUUID(playerUUID);
            if (playerDataId == -1) {
                PlayerDataEntity playerDataEntity = new PlayerDataEntity();
                playerDataEntity.setPlayerUuid(playerUUID);
                playerDataEntity.setIslandNumberLimit(currentPlayerIslandLimit + addIslandLimit);
                playerDataDAO.create(playerDataEntity);
                return;
            }
            PlayerDataEntity playerDataEntity = playerDataDAO.queryForId(playerDataId);
            playerDataEntity.setIslandNumberLimit(currentPlayerIslandLimit + addIslandLimit);
            playerDataDAO.update(playerDataEntity);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("DATABASE ERROR");
        }
    }
    //api
    public void setPlayerIslandLimit(UUID playerUUID, int setIslandLimit) throws Exception{
        try {
            int playerDataId = getPlayerDataIDByUUID(playerUUID);
            if (playerDataId == -1) {
                PlayerDataEntity playerDataEntity = new PlayerDataEntity();
                playerDataEntity.setPlayerUuid(playerUUID);
                playerDataEntity.setIslandNumberLimit(setIslandLimit);
                playerDataDAO.create(playerDataEntity);
                return;
            }
            PlayerDataEntity playerDataEntity = playerDataDAO.queryForId(playerDataId);
            playerDataEntity.setIslandNumberLimit(setIslandLimit);
            playerDataDAO.update(playerDataEntity);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("DATABASE ERROR");
        }
    }
    // api
    public int getPlayerBorderSize(UUID playerUUID) throws Exception {
        try {
            Optional<PlayerDataEntity> playerDataEntity = playerDataDAO.queryForEq("player_uuid", playerUUID).stream().findFirst();
            if (playerDataEntity.isPresent()) {
                int borderSize = playerDataEntity.get().getBorderSize();
                if (borderSize < mainConfig.getDefaultBorderSize()) {
                    borderSize = mainConfig.getDefaultBorderSize();
                }
                return borderSize;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("DATABASE ERROR");
        }
        return mainConfig.getDefaultBorderSize();
    }
    // api
    public void addPlayerBorderSize(UUID playerUUID, int addBorderSize) throws Exception{
        try {
            int currentBorderSize = getPlayerBorderSize(playerUUID);
            int playerDataId = getPlayerDataIDByUUID(playerUUID);
            if (playerDataId == -1) {
                PlayerDataEntity playerDataEntity = new PlayerDataEntity();
                playerDataEntity.setPlayerUuid(playerUUID);
                playerDataEntity.setBorderSize(currentBorderSize + addBorderSize);
                playerDataDAO.create(playerDataEntity);
                return;
            }
            PlayerDataEntity playerDataEntity = playerDataDAO.queryForId(playerDataId);
            playerDataEntity.setBorderSize(currentBorderSize + addBorderSize);
            playerDataDAO.update(playerDataEntity);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("DATABASE ERROR");
        }
    }
    // api
    public void setPlayerBorderSize(UUID playerUUID, int setBorderSize) throws Exception{
        try {
            int playerDataId = getPlayerDataIDByUUID(playerUUID);
            if (playerDataId == -1) {
                PlayerDataEntity playerDataEntity = new PlayerDataEntity();
                playerDataEntity.setPlayerUuid(playerUUID);
                playerDataEntity.setBorderSize(setBorderSize);
                playerDataDAO.create(playerDataEntity);
                return;
            }
            PlayerDataEntity playerDataEntity = playerDataDAO.queryForId(playerDataId);
            playerDataEntity.setBorderSize(setBorderSize);
            playerDataDAO.update(playerDataEntity);

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

    public void setPlayerIslandLimit(CommandSender commandSender, String playerName, int setIslandLimit) {
        if (!(commandSender instanceof ConsoleCommandSender) && !commandSender.hasPermission("betterskyblock.admin")) {
            commandSender.sendMessage(ChatColor.RED + languageConfig.getDoNotHasPermission());
            return;
        }
        try {
            UUID playerUUID = PlayerUUIDUtil.getPlayerUUID(playerName);
            setPlayerIslandLimit(playerUUID, setIslandLimit);
        } catch (Exception e) {
            e.printStackTrace();
            commandSender.sendMessage(ChatColor.RED + languageConfig.getServerError());
            return;
        }
        commandSender.sendMessage(ChatColor.GOLD + languageConfig.getSetPlayerIslandLimitSuccess());

    }
    public void getPlayerIslandLimit(CommandSender commandSender, String playerName) {
        if (!(commandSender instanceof ConsoleCommandSender) && !commandSender.hasPermission("betterskyblock.admin")) {
            commandSender.sendMessage(ChatColor.RED + languageConfig.getDoNotHasPermission());
            return;
        }
        try {
            UUID playerUUID = PlayerUUIDUtil.getPlayerUUID(playerName);
            int playerIslandLimit = getPlayerIslandLimit(playerUUID);
            commandSender.sendMessage(ChatColor.GOLD + "The Island Limit of " + playerName + " is: " + playerIslandLimit);
        } catch (Exception e) {
            e.printStackTrace();
            commandSender.sendMessage(ChatColor.RED + languageConfig.getServerError());
        }

    }

    public void addPlayerIslandLimit(CommandSender commandSender, String playerName, int addIslandLimit) {
        if (!(commandSender instanceof ConsoleCommandSender) && !commandSender.hasPermission("betterskyblock.admin")) {
            commandSender.sendMessage(ChatColor.RED + languageConfig.getDoNotHasPermission());
            return;
        }
        try {
            UUID playerUUID = PlayerUUIDUtil.getPlayerUUID(playerName);
            addPlayerIslandLimit(playerUUID, addIslandLimit);
        } catch (Exception e) {
            e.printStackTrace();
            commandSender.sendMessage(ChatColor.RED + languageConfig.getServerError());
            return;
        }
        commandSender.sendMessage(ChatColor.GOLD + languageConfig.getAddPlayerIslandLimitSuccess());

    }

}
