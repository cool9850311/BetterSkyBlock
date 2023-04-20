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
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.sql.SQLException;
import java.util.*;

public class IslandManager {
    private final IslandDAO islandDAO;
    private final IslandTrustDAO islandTrustDAO;
    private final PlayerDataDAO playerDataDAO;
    private BetterSkyBlock plugin;
    private final Language languageConfig;
    private final MainConfig mainConfig;
    private final Set<String> unUsedWorld;
    private final  String  adminPermission = "betterskyblock.admin";

    public IslandManager(){
        plugin = BetterSkyBlock.getInstance();
        languageConfig = plugin.getLanguageConfigManager().getConfig();
        mainConfig = plugin.getMainConfigConfigManager().getConfig();
        unUsedWorld = new HashSet<>();
        islandDAO = IslandDAO.getInstance(BetterSkyBlock.getInstance().getDataBaseManager().getConnectionSource(), IslandEntity.class);
        islandTrustDAO = IslandTrustDAO.getInstance(BetterSkyBlock.getInstance().getDataBaseManager().getConnectionSource(), IslandTrustEntity.class);
        playerDataDAO = PlayerDataDAO.getInstance(BetterSkyBlock.getInstance().getDataBaseManager().getConnectionSource(), PlayerDataEntity.class);
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
            String islandWorldName = player.getUniqueId() + "_" + islandId;
            World world = createAndLoadActualWorld(World.Environment.NORMAL, islandWorldName);
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
        if (getPlayerIslandCount(player) >= mainConfig.getIslandLimit() && !player.hasPermission(adminPermission)) {
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
        try {
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
            Bukkit.unloadWorld(worldName, true);
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
        WorldCreator worldCreator = new WorldCreator(name)
                .generator(BetterSkyBlock.getInstance().getDefaultWorldGenerator(name, null))
                .environment(environment);
        World world = Bukkit.createWorld(worldCreator);
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
    private int getPlayerBorderSize(UUID playerUUID) {
        try {
            Optional<PlayerDataEntity> playerDataEntity = playerDataDAO.queryForEq("player_uuid", playerUUID).stream().findFirst();
            if (playerDataEntity.isPresent()) {
                return playerDataEntity.get().getBorderSize();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mainConfig.getDefaultBorderSize();
    }

    private void initIsland(World world) {
        Location loc = new Location(world,0,0,0);
        if (loc.getBlock().isEmpty()) {
            loc.getBlock().setType(Material.BEDROCK);
        }
        UUID playerUUID = UUID.fromString(world.getName().split("_")[0]);
        world.getWorldBorder().setSize(getPlayerBorderSize(playerUUID));
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
                    Bukkit.unloadWorld(world.getName(), true);
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
        if (!plugin.getIslandManager().isInIslandWorld(worldName)){
            player.sendMessage(ChatColor.RED + languageConfig.getNotOnIsland());
            return;
        }
        int islandId = Integer.parseInt(player.getWorld().getName().split("_")[1]);
        String islandName = plugin.getIslandManager().getIslandNameById(islandId);
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
        if (!plugin.getIslandManager().isInIslandWorld(worldName)){
            player.sendMessage(ChatColor.RED + languageConfig.getNotOnIsland());
            return;
        }
        int islandId = Integer.parseInt(player.getWorld().getName().split("_")[1]);
        String islandName = plugin.getIslandManager().getIslandNameById(islandId);
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
    public void setPublicIsland(Player player, String islandName) {

    }
}
