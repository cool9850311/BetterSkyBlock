package com.fnv_tw;

import com.fnv_tw.configs.SQL;
import com.fnv_tw.database.Entity.IslandEntity;
import com.fnv_tw.database.IslandDAO;
import com.fnv_tw.generator.VoidGenerator;
import com.fnv_tw.managers.ConfigManager;
import com.fnv_tw.managers.DataBaseManager;
import com.fnv_tw.managers.IslandManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public class BetterSkyBlock extends JavaPlugin {
    private static BetterSkyBlock instance;
    private ChunkGenerator chunkGenerator;
    private DataBaseManager dataBaseManager;
    private IslandManager islandManager;

    private SQL sqlConfig;
    @Override
    public void onLoad() {
        super.onLoad();
        this.chunkGenerator = new VoidGenerator();
    }
    @Override
    public void onEnable() {
        super.onEnable();
        Bukkit.getLogger().info("------------------------------------");
        Bukkit.getLogger().info("");
        Bukkit.getLogger().info("Start Enabling BetterSkyBlock");
        Bukkit.getLogger().info("");
        Bukkit.getLogger().info("------------------------------------");
        instance = this;
        islandManager = new IslandManager();
//        islandManager.createWorld(World.Environment.NORMAL, "test_world");
        loadConfigs();
        try {
            dataBaseManager = new DataBaseManager(sqlConfig);
        } catch (SQLException e) {
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
//        IslandEntity islandEntityTest = new IslandEntity();
//        islandEntityTest.setBorderSize(11);
//        islandEntityTest.setName("Test");
//
//        try {
//            IslandDAO islandDAO = new IslandDAO(dataBaseManager.getConnectionSource(), IslandEntity.class);
//            islandDAO.create(islandEntityTest);
//
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
        Bukkit.getLogger().info("------------------------------------");
        Bukkit.getLogger().info("");
        Bukkit.getLogger().info("BetterSkyBlock Enabled");
        Bukkit.getLogger().info("");
        Bukkit.getLogger().info("------------------------------------");
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    public static BetterSkyBlock getInstance() {
        return instance;
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        return this.chunkGenerator;
    }

    private void loadConfigs() {
        sqlConfig = new ConfigManager<>(new SQL()).getConfig();
        // Bukkit.getLogger().info("sqlConfig:" + sqlConfig.getPort());
    }
}
