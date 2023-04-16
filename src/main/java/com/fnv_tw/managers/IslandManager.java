package com.fnv_tw.managers;

import com.fnv_tw.BetterSkyBlock;
import com.fnv_tw.configs.Language;
import com.fnv_tw.configs.MainConfig;
import com.fnv_tw.database.BorderDAO;
import com.fnv_tw.database.Entity.BorderEntity;
import com.fnv_tw.database.Entity.IslandEntity;
import com.fnv_tw.database.Entity.IslandTrustEntity;
import com.fnv_tw.database.IslandDAO;
import com.fnv_tw.database.IslandTrustDAO;
import com.fnv_tw.utils.LocationUtil;
import com.j256.ormlite.stmt.DeleteBuilder;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class IslandManager {
    private final IslandDAO islandDAO;
    private IslandTrustDAO islandTrustDAO;
    private BorderDAO borderDAO;
    private BetterSkyBlock plugin;
    private final Language languageConfig;
    private final MainConfig mainConfig;
    private Set<String> unUsedWorld;

    public IslandManager(){
        plugin = BetterSkyBlock.getInstance();
        languageConfig = plugin.getLanguageConfigManager().getConfig();
        mainConfig = plugin.getMainConfigConfigManager().getConfig();
        unUsedWorld = new HashSet<>();
        islandDAO = IslandDAO.getInstance(BetterSkyBlock.getInstance().getDataBaseManager().getConnectionSource(), IslandEntity.class);
        islandTrustDAO = IslandTrustDAO.getInstance(BetterSkyBlock.getInstance().getDataBaseManager().getConnectionSource(), IslandTrustEntity.class);
        borderDAO = BorderDAO.getInstance(BetterSkyBlock.getInstance().getDataBaseManager().getConnectionSource(), BorderEntity.class);
    }
    //TODO: trust and owner only
    // TODO: home point first
    // TODO:BungeeCord
    public void teleportToIsland(Player player, String islandName){
        if (!isIslandExist(islandName)) {
            player.sendMessage(ChatColor.RED + languageConfig.getIslandNameNotExistOnTeleport());
            return;
        }
        if (!isPlayerTrusted(player, islandName) && !isPublicIsland(islandName)) {
            player.sendMessage(ChatColor.RED + languageConfig.getNotInIslandTrustList());
            return;
        }
        try {
            int islandId = getIslandId(islandName);
            // {uuid}_{islandId}
            String islandWorldName = player.getUniqueId() + "_" + islandId;
            World world = createAndLoadActualWorld(World.Environment.NORMAL, islandWorldName);

            IslandEntity islandIdEntity = islandDAO.queryForId(islandId);
            Vector vector =  islandIdEntity.getHome();
            player.setFallDistance(0.0f);
            player.teleport(LocationUtil.getSafeLocation(new Location(world,vector.getX(),vector.getY(),vector.getZ())));
        } catch (Exception e) {
            e.printStackTrace();
            player.sendMessage(ChatColor.RED + languageConfig.getServerError());
        }
    }
    public void createWorld(Player player, String islandName) {
        if (isIslandExist(islandName)) {
            player.sendMessage(ChatColor.RED + languageConfig.getIslandNameExistOnCreate());
            return;
        }
        if (getPlayerIslandCount(player) >= mainConfig.getIslandLimit()) {
            player.sendMessage(ChatColor.RED + languageConfig.getIslandLimitReached());
            return;
        }
        IslandEntity islandEntity = new IslandEntity();
        islandEntity.setOwnerUuid(player.getUniqueId());
        islandEntity.setName(islandName);
        islandEntity.setHome(new Vector(0,0,0));
        islandEntity.setPublicIsland(mainConfig.isDefaultPublicIsland());
        if (mainConfig.isBungeeCord()){
            islandEntity.setBungeeServerName(mainConfig.getCurrentBungeeCordServerName());
        }
        int islandId;
        try {
            islandDAO.create(islandEntity);
            player.sendMessage(ChatColor.GOLD + languageConfig.getLoadIslandPleaseWait());
            World world = createAndLoadActualWorld(World.Environment.NORMAL, player.getUniqueId() + "_" + getIslandId(islandName));
            initIsland(world, getPlayerBorderSize(player));
        } catch (Exception e) {
            e.printStackTrace();
            player.sendMessage(ChatColor.RED + languageConfig.getServerError());
            return;
        }
        player.sendMessage(languageConfig.getCreateIslandSuccess());
        teleportToIsland(player, islandName);

    }
    public boolean isIslandExist(String islandName) {
        try {
            List<IslandEntity> islandEntities = islandDAO.queryForEq("name", islandName);
            if (islandEntities.isEmpty()) {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public void changeIslandName(Player player, String islandName) {

    }
    public void unloadIsland(String worldName) {
        Bukkit.unloadWorld(worldName, true);
        // Bukkit.getLogger().info("unload world:" + worldName);
    }
    public int getPlayerIslandCount(Player player) {
        try {
            List<IslandEntity> islandEntities = islandDAO.queryForEq("owner_uuid", player.getUniqueId());
            return islandEntities.size();
        } catch (SQLException e) {
            e.printStackTrace();
            return Integer.MAX_VALUE;
        }
    }
    // TODO: Different World.Environment
    // TODO: update border size to owner's border size
    // if exist load only
    public World createAndLoadActualWorld(World.Environment environment, String name) {
        WorldCreator worldCreator = new WorldCreator(name)
                .generator(BetterSkyBlock.getInstance().getDefaultWorldGenerator(name, null))
                .environment(environment);
        return Bukkit.createWorld(worldCreator);
    }
    private int getIslandId(String islandName) throws Exception{
        Optional<IslandEntity> islandIdEntity = islandDAO.queryForEq("name", islandName).stream().findFirst();
        if (islandIdEntity.isPresent()) {
            return islandIdEntity.get().getId();
        }
        throw new Exception("Can not found Island id with" + islandName);
    }
    private int getPlayerBorderSize(Player player) {
        try {
            Optional<BorderEntity> borderEntity = borderDAO.queryForEq("player_uuid", player.getUniqueId()).stream().findFirst();
            if (borderEntity.isPresent()) {
                return borderEntity.get().getBorderSize();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mainConfig.getDefaultBorderSize();
    }

    private void initIsland(World world, int size) {
        Location loc = new Location(world,0,0,0);
        loc.getBlock().setType(Material.BEDROCK);
        world.getWorldBorder().setSize(size);
    }
    public void unloadUnusedWorldTask() {
        List<World> worlds = Bukkit.getWorlds();
        for (World world : worlds) {
            String worldName = world.getName();
            if (worldName.equals(mainConfig.getDefaultWorldName()) || worldName.equals(mainConfig.getDefaultNetherName()) ||
                    worldName.equals(mainConfig.getDefaultTheEndName())){
                continue;
            }
            if (world.getPlayers().size() == 0) {
                if (unUsedWorld.contains(worldName)){
                     unloadIsland(world.getName());
                     unUsedWorld.remove(worldName);
                     continue;
                }
                unUsedWorld.add(worldName);
            }
        }
    }
    public boolean isIslandOwner(Player player, String islandName) {
        UUID owner = null;
        try {
            int islandId = getIslandId(islandName);
            owner = islandDAO.queryForId(islandId).getOwnerUuid();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        if (owner.equals(player.getUniqueId())) {
            return true;
        }
        return false;
    }
    public boolean isPlayerTrusted(Player player, String islandName) {
        try {

            int islandId = getIslandId(islandName);
            // Owner must be trusted
            if (isIslandOwner(player, islandName)) {
                return true;
            }
            List<UUID> islandTrustEntity = islandTrustDAO.queryForEq("islandId", islandId).stream().map(IslandTrustEntity::getPlayerUuid).toList();
            if (islandTrustEntity.contains(player.getUniqueId())) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }
    // islandName must exist
    public void addPlayerTrusted(Player operator, String islandName, String addPlayerString) {
        try {
            Player addPlayer = Bukkit.getPlayer(addPlayerString);
            if (addPlayer == null) {
                operator.sendMessage(ChatColor.RED + languageConfig.getPlayerNotFound());
                return;
            }
            // islandName must exist
            if (!isIslandOwner(operator, islandName)) {
                operator.sendMessage(ChatColor.RED + languageConfig.getDoNotHasPermission());
                return;
            }
            if (addPlayer.equals(operator)) {
                operator.sendMessage(ChatColor.RED + languageConfig.getOwnerAlwaysTrusted());
                return;
            }
            if (isPlayerTrusted(addPlayer, islandName)) {
                operator.sendMessage(ChatColor.RED + languageConfig.getAlreadyTrusted());
                return;
            }
            IslandTrustEntity islandTrustEntity = new IslandTrustEntity();
            int islandId = getIslandId(islandName);
            islandTrustEntity.setIslandId(islandId);
            islandTrustEntity.setPlayerUuid(addPlayer.getUniqueId());
            islandTrustEntity.setOperatorUuid(operator.getUniqueId());
            islandTrustDAO.create(islandTrustEntity);
            operator.sendMessage(ChatColor.GOLD + languageConfig.getAddTrustSuccess());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void removePlayerTrusted(Player operator, String islandName, String removePlayerString) {
        try {
            Player removePlayer = Bukkit.getPlayer(removePlayerString);
            if (removePlayer == null) {
                operator.sendMessage(ChatColor.RED + languageConfig.getPlayerNotFound());
                return;
            }
            // islandName must exist
            if (!isIslandOwner(operator, islandName)) {
                operator.sendMessage(ChatColor.RED + languageConfig.getDoNotHasPermission());
                return;
            }
            if (removePlayer.equals(operator)) {
                operator.sendMessage(ChatColor.RED + languageConfig.getOwnerAlwaysTrusted());
                return;
            }
            if (!isPlayerTrusted(removePlayer, islandName)) {
                operator.sendMessage(ChatColor.RED + languageConfig.getNotTrusted());
                return;
            }
            IslandTrustEntity islandTrustEntity = new IslandTrustEntity();
            int islandId = getIslandId(islandName);
            DeleteBuilder<IslandTrustEntity, Integer> deleteBuilder = islandTrustDAO.deleteBuilder();
            deleteBuilder.where().eq("player_uuid", removePlayer.getUniqueId()).and().eq("island_id", islandId);
            deleteBuilder.delete();
            operator.sendMessage(ChatColor.GOLD + languageConfig.getRemoveTrustSuccess());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public boolean isPublicIsland(String islandName) {
        try {
            int islandId = getIslandId(islandName);
            return islandDAO.queryForId(islandId).isPublicIsland();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public String getIslandNameById(int islandId) {
        try {
            return islandDAO.queryForId(islandId).getName();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
