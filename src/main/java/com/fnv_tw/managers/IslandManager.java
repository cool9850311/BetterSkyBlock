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
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
    public void teleportToIsland(Player player, String islandName){
        if (!isIslandExist(islandName)) {
            player.sendMessage(ChatColor.RED + languageConfig.getIslandNameNotExistOnTeleport());
            return;
        }
        // {uuid}_{islandId}
        String islandWorldName = null;
        try {
            islandWorldName = player.getUniqueId() + "_" + getIslandId(islandName);
        } catch (Exception e) {
            e.printStackTrace();
            player.sendMessage(ChatColor.RED + languageConfig.getServerError());
            return;
        }
        // load
        World world = createActualWorld(World.Environment.NORMAL, islandWorldName);
        // TODO:tp home first
        // TODO:BungeeCord
        player.setFallDistance(0.0f);
        player.teleport(LocationUtil.getSafeLocation(new Location(world,0,0,0)));
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
        if (mainConfig.isBungeeCord()){
            islandEntity.setBungeeServerName(mainConfig.getCurrentBungeeCordServerName());
        }
        int islandId;
        try {
            islandDAO.create(islandEntity);
            player.sendMessage(ChatColor.GOLD + languageConfig.getLoadIslandPleaseWait());
            World world = createActualWorld(World.Environment.NORMAL, player.getUniqueId() + "_" + getIslandId(islandName));

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
    public World createActualWorld(World.Environment environment, String name) {
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
}
