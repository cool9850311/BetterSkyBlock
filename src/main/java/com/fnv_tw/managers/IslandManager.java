package com.fnv_tw.managers;

import com.fnv_tw.BetterSkyBlock;
import com.fnv_tw.configs.Language;
import com.fnv_tw.configs.MainConfig;
import com.fnv_tw.database.PlayerDataDAO;
import com.fnv_tw.database.Entity.PlayerDataEntity;
import com.fnv_tw.database.Entity.IslandEntity;
import com.fnv_tw.database.Entity.IslandTrustEntity;
import com.fnv_tw.database.IslandDAO;
import com.fnv_tw.database.IslandTrustDAO;
import com.fnv_tw.utils.LocationUtil;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.onarandombox.MultiverseCore.MVWorld;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public class IslandManager {
    private final BetterSkyBlock plugin;
    private final MVWorldManager worldManager;
    private final IslandDAO islandDAO;
    private final IslandTrustDAO islandTrustDAO;
    private final Language languageConfig;
    private final MainConfig mainConfig;
    private final Set<String> unUsedWorld;
    private final  String  adminPermission = "betterskyblock.admin";

    public IslandManager(){
        plugin = BetterSkyBlock.getInstance();
        worldManager = plugin.getMultiverseCore().getMVWorldManager();
        languageConfig = plugin.getLanguageConfigManager().getConfig();
        mainConfig = plugin.getMainConfigConfigManager().getConfig();
        unUsedWorld = new HashSet<>();
        ConnectionSource connectionSource = BetterSkyBlock.getInstance().getDataBaseManager().getConnectionSource();
        islandDAO = IslandDAO.getInstance(connectionSource, IslandEntity.class);
        islandTrustDAO = IslandTrustDAO.getInstance(connectionSource, IslandTrustEntity.class);
    }
    // TODO:BungeeCord
    public void teleportToIsland(Player player, String islandName, boolean unsafe){
        if (!isIslandExist(islandName)) {
            player.sendMessage(ChatColor.RED + languageConfig.getIslandNameNotExist());
            return;
        }
        if (!isPlayerTrusted(player, islandName) && !isPublicIsland(islandName) && !player.hasPermission(adminPermission)) {
            player.sendMessage(ChatColor.RED + languageConfig.getNotInIslandTrustList());
            return;
        }
        if (islandIsBaned(islandName) && !player.hasPermission(adminPermission)) {
            player.sendMessage(ChatColor.RED + languageConfig.getIslandIsBanedWarning());
            return;
        }
        try {
            int islandId = getIslandId(islandName);

            // {uuid}_{islandId}
            String islandWorldName = getIslandOwnerUUID(islandName) + "_" + islandId;
            worldManager.loadWorld(islandWorldName);
            World world = Bukkit.getWorld(islandWorldName);
            IslandEntity islandIdEntity = islandDAO.queryForId(islandId);
            Vector vector =  islandIdEntity.getHome();
            Location homeLocation = new Location(world,vector.getX(),vector.getY(),vector.getZ());
            Location tpLocation = LocationUtil.getSafeLocation(homeLocation);
            if (unsafe) {
                player.setFallDistance(0.0f);
                player.teleport(homeLocation);
                return;
            }
            if (tpLocation == null) {
                // island void case
                if (player.getWorld().getName().equals(world.getName())){
                    player.performCommand("is tpNormal");
                    return;
                }
                player.sendMessage(ChatColor.RED + languageConfig.getTeleportUnsafe());
                return;
            }

            player.setFallDistance(0.0f);
            player.teleport(tpLocation);
        } catch (Exception e) {
            e.printStackTrace();
            player.sendMessage(ChatColor.RED + languageConfig.getServerError());
        }
    }
    public void createWorld(Player player, String islandName) {
        if (isIslandExist(islandName)) {
            player.sendMessage(ChatColor.RED + languageConfig.getIslandNameAlreadyExist());
            return;
        }
        PlayerDataManager playerDataManager = plugin.getPlayerDataManager();

        try {
            if (getPlayerIslandCount(player) >= playerDataManager.getPlayerIslandLimit(player.getUniqueId()) && !player.hasPermission(adminPermission)) {
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
            islandDAO.create(islandEntity);
            player.sendMessage(ChatColor.GOLD + languageConfig.getLoadIslandPleaseWait());
            createAndLoadActualWorld(World.Environment.NORMAL, player.getUniqueId() + "_" + getIslandId(islandName));
        } catch (Exception e) {
            e.printStackTrace();
            player.sendMessage(ChatColor.RED + languageConfig.getServerError());
            return;
        }
        player.sendMessage(languageConfig.getCreateIslandSuccess());
        teleportToIsland(player, islandName, false);

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
    public void unloadIsland(CommandSender commandSender, String islandName) {
        if (!(commandSender instanceof ConsoleCommandSender) && !commandSender.hasPermission("betterskyblock.admin")) {
            commandSender.sendMessage(ChatColor.RED + languageConfig.getDoNotHasPermission());
            return;
        }
        if (!isIslandExist(islandName)) {
            commandSender.sendMessage(ChatColor.RED + languageConfig.getIslandNameNotExist());
            return;
        }
        try {
            int islandId = getIslandId(islandName);
            IslandEntity islandEntity = islandDAO.queryForId(islandId);
            String worldName = islandEntity.getOwnerUuid() + "_" + islandId;
            World world = Bukkit.getWorld(worldName);
            for (Player player:world.getPlayers()) {
                player.performCommand("is tpNormal");
            }
            worldManager.unloadWorld(worldName);
            //Bukkit.unloadWorld(worldName, true);
            commandSender.sendMessage(ChatColor.GOLD + languageConfig.getUnloadIslandSuccess());
        } catch (Exception e) {
            e.printStackTrace();
        }


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
    // if exist load only
    public World createAndLoadActualWorld(World.Environment environment, String name) {
//        WorldCreator worldCreator = new WorldCreator(name)
//                .generator(BetterSkyBlock.getInstance().getDefaultWorldGenerator(name, null))
//                .environment(environment);

        worldManager.addWorld(
                name, // The worldname
                environment, // The overworld environment type.
                null, // The world seed. Any seed is fine for me, so we just pass null.
                WorldType.NORMAL, // Nothing special. If you want something like a flat world, change this.
                false, // This means we want to structures like villages to generator, Change to false if you don't want this.
                "BetterSkyBlock" // Specifies a custom generator. We are not using any so we just pass null.
        );
        MultiverseWorld multiverseWorld = worldManager.getMVWorld(name);
        multiverseWorld.setDifficulty(Difficulty.HARD);
        multiverseWorld.setAutoLoad(false);
        World world = Bukkit.getWorld(name);
        initIsland(world);
        return world;
    }
    private int getIslandId(String islandName) throws Exception{
        Optional<IslandEntity> islandIdEntity = islandDAO.queryForEq("name", islandName).stream().findFirst();
        if (islandIdEntity.isPresent()) {
            return islandIdEntity.get().getId();
        }
        throw new Exception("Can not found Island id with" + islandName);
    }

    private void initIsland(World world) {
        Location loc = new Location(world,0,0,0);
        if (loc.getBlock().isEmpty()) {
            loc.getBlock().setType(Material.BEDROCK);
        }
        UUID playerUUID = UUID.fromString(world.getName().split("_")[0]);
        PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
        try {
            world.getWorldBorder().setSize(playerDataManager.getPlayerBorderSize(playerUUID));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void unloadUnusedWorldTask() {
        List<World> worlds = Bukkit.getWorlds();
        for (World world : worlds) {
            String worldName = world.getName();
            if (!isInIslandWorld(worldName)){
                continue;
            }
            if (world.getPlayers().size() == 0) {
                if (unUsedWorld.contains(worldName)){
                    worldManager.unloadWorld(world.getName());
                    // Bukkit.unloadWorld(world.getName(), true);
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
    public UUID getIslandOwnerUUID(String islandName) {
        try {
            int islandId = getIslandId(islandName);
            return islandDAO.queryForId(islandId).getOwnerUuid();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public boolean isPlayerTrusted(Player player, String islandName) {
        try {

            int islandId = getIslandId(islandName);
            // Owner must be trusted
            if (isIslandOwner(player, islandName)) {
                return true;
            }
            List<UUID> islandTrustEntity = islandTrustDAO.queryForEq("island_id", islandId).stream().map(IslandTrustEntity::getPlayerUuid).toList();
            if (islandTrustEntity.contains(player.getUniqueId())) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }
    public void addPlayerTrusted(Player operator, String islandName, String addPlayerString) {
        try {
            Player addPlayer = Bukkit.getPlayer(addPlayerString);
            if (addPlayer == null) {
                operator.sendMessage(ChatColor.RED + languageConfig.getPlayerNotFound());
                return;
            }
            if (!isIslandExist(islandName)) {
                operator.sendMessage(ChatColor.RED + languageConfig.getNotOnIsland());
                return;
            }
            if (!isIslandOwner(operator, islandName) && !operator.hasPermission(adminPermission)) {
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
            if (!isIslandExist(islandName)) {
                operator.sendMessage(ChatColor.RED + languageConfig.getNotOnIsland());
                return;
            }
            if (!isIslandOwner(operator, islandName) && !operator.hasPermission(adminPermission)) {
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
    public List<String> getTrustedIslandName (Player player) {
        List<String> allTrustedIsland = new ArrayList<>();
        try {
            List<String> ownedIsland = islandDAO.queryForEq("owner_uuid", player.getUniqueId()).stream()
                    .map(IslandEntity::getName).toList();
            //
            String sql = "SELECT i.name FROM islands i JOIN island_trust t ON i.id = t.island_id WHERE t.player_uuid = ?";
            GenericRawResults<String[]> rawResults = islandDAO.queryRaw(sql, player.getUniqueId().toString());
            List<String> trustedIsland = rawResults.getResults().stream().map(result -> result[0]).toList();

            allTrustedIsland.addAll(ownedIsland);
            allTrustedIsland.addAll(trustedIsland);

        } catch (SQLException e) {
            e.printStackTrace();
            return allTrustedIsland;
        }
        return allTrustedIsland;
    }
    public boolean isInIslandWorld(String islandWorldName) {
        if (islandWorldName.equals(mainConfig.getDefaultWorldName()) || islandWorldName.equals(mainConfig.getDefaultNetherName()) ||
                islandWorldName.equals(mainConfig.getDefaultTheEndName())) {
            return false;
        }
        return true;
    }
    public void changeIslandHome(Player player) {
        String worldName = player.getWorld().getName();
        if (!isInIslandWorld(worldName)){
            player.sendMessage(ChatColor.RED + languageConfig.getNotOnIsland());
            return;
        }
        int islandId = Integer.parseInt(player.getWorld().getName().split("_")[1]);
        String islandName = getIslandNameById(islandId);
        if (!isIslandExist(islandName)) {
            player.sendMessage(ChatColor.RED + languageConfig.getNotOnIsland());
            return;
        }
        if (!isIslandOwner(player, islandName)) {
            player.sendMessage(ChatColor.RED + languageConfig.getDoNotHasPermission());
            return;
        }
        try {
            IslandEntity islandEntity = islandDAO.queryForId(islandId);
            islandEntity.setHome(player.getLocation().toVector());
            islandDAO.update(islandEntity);
        } catch (SQLException e) {
            player.sendMessage(ChatColor.RED + languageConfig.getServerError());
            e.printStackTrace();
        }
        player.sendMessage(ChatColor.GOLD + languageConfig.getSetHomeSuccess());
    }
    public void changeIslandName(Player player, String newIslandName) {
        String worldName = player.getWorld().getName();
        if (!isInIslandWorld(worldName)){
            player.sendMessage(ChatColor.RED + languageConfig.getNotOnIsland());
            return;
        }
        int islandId = Integer.parseInt(player.getWorld().getName().split("_")[1]);
        String islandName = getIslandNameById(islandId);
        if (!isIslandExist(islandName)) {
            player.sendMessage(ChatColor.RED + languageConfig.getNotOnIsland());
            return;
        }
        if (!isIslandOwner(player, islandName)) {
            player.sendMessage(ChatColor.RED + languageConfig.getDoNotHasPermission());
            return;
        }
        if (isIslandExist(newIslandName)) {
            player.sendMessage(ChatColor.RED + languageConfig.getIslandNameAlreadyExist());
            return;
        }

        try {
            IslandEntity islandEntity = islandDAO.queryForId(islandId);
            islandEntity.setName(newIslandName);
            islandDAO.update(islandEntity);
        } catch (SQLException e) {
            player.sendMessage(ChatColor.RED + languageConfig.getServerError());
            e.printStackTrace();
        }
        player.sendMessage(ChatColor.GOLD + languageConfig.getRenameIslandSuccess());
    }

    public void banOrUnbanIsland(CommandSender commandSender, String islandName) {
        if (!(commandSender instanceof ConsoleCommandSender) && !commandSender.hasPermission("betterskyblock.admin")) {
            commandSender.sendMessage(ChatColor.RED + languageConfig.getDoNotHasPermission());
            return;
        }
        if (!isIslandExist(islandName)) {
            commandSender.sendMessage(ChatColor.RED + languageConfig.getIslandNameNotExist());
            return;
        }
        try {
            int islandId = getIslandId(islandName);
            IslandEntity islandEntity = islandDAO.queryForId(islandId);
            if (islandEntity.isBan()) {
                // unban
                islandEntity.setBan(false);
                islandDAO.update(islandEntity);
                commandSender.sendMessage(ChatColor.GOLD + languageConfig.getIslandUnbanSuccess());
                return;
            }
            islandEntity.setBan(true);
            islandDAO.update(islandEntity);
            commandSender.sendMessage(ChatColor.GOLD + languageConfig.getIslandBanSuccess());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // usage:islandName must exist
    private boolean islandIsBaned (String islandName) {
        try {
            int islandId = getIslandId(islandName);
            IslandEntity islandEntity = islandDAO.queryForId(islandId);
            if (islandEntity.isBan()) {
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public void setPublicIsland(Player player) {
        String worldName = player.getWorld().getName();
        if (!isInIslandWorld(worldName)){
            player.sendMessage(ChatColor.RED + languageConfig.getNotOnIsland());
            return;
        }
        int islandId = Integer.parseInt(player.getWorld().getName().split("_")[1]);
        String islandName = getIslandNameById(islandId);
        if (!isIslandExist(islandName)) {
            player.sendMessage(ChatColor.RED + languageConfig.getNotOnIsland());
            return;
        }
        if (!isIslandOwner(player, islandName)) {
            player.sendMessage(ChatColor.RED + languageConfig.getDoNotHasPermission());
            return;
        }
        try {
            IslandEntity islandEntity = islandDAO.queryForId(islandId);
            if (islandEntity.isPublicIsland()){
                islandEntity.setPublicIsland(false);
                islandDAO.update(islandEntity);
                player.sendMessage(ChatColor.GOLD + languageConfig.getIslandNowPrivate());
                return;
            }
            islandEntity.setPublicIsland(true);
            islandDAO.update(islandEntity);
            player.sendMessage(ChatColor.GOLD + languageConfig.getIslandNowPublic());

        } catch (SQLException e) {
            player.sendMessage(ChatColor.RED + languageConfig.getServerError());
            e.printStackTrace();
        }
    }

    public List<String> getIslandTrustList (String islandName){
        try {
            int islandId = getIslandId(islandName);
            return islandTrustDAO.queryForEq("island_id", islandId).stream()
                    .map(IslandTrustEntity::getPlayerUuid)
                    .map(uuid -> {
                        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
                        return player.getName() != null ? player.getName() : uuid.toString();
                    })
                    .filter(Objects::nonNull)
                    .toList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public void getIslandTrustList (Player player, String islandName){
        if (!isIslandExist(islandName)) {
            player.sendMessage(ChatColor.RED + languageConfig.getNotOnIsland());
            return;
        }
        if (!isIslandOwner(player, islandName) && !player.hasPermission(adminPermission)) {
            player.sendMessage(ChatColor.RED + languageConfig.getDoNotHasPermission());
            return;
        }
        String result = languageConfig.getTrustList() + String.join(", ", getIslandTrustList(islandName));
        player.sendMessage(ChatColor.GOLD + result);

    }

}
