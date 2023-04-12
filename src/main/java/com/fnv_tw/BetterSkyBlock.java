package com.fnv_tw;

import com.fnv_tw.configs.SQL;
import com.fnv_tw.database.Entity.IslandEntity;
import com.fnv_tw.database.IslandDAO;
import com.fnv_tw.generator.VoidGenerator;
import com.fnv_tw.managers.DataBaseManager;
import org.bukkit.Bukkit;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public class BetterSkyBlock extends JavaPlugin {
    private static BetterSkyBlock instance;
    private ChunkGenerator chunkGenerator;
    private DataBaseManager dataBaseManager;
    @Override
    public void onLoad() {
        super.onLoad();
        this.chunkGenerator = new VoidGenerator();
    }
    @Override
    public void onEnable() {
        super.onEnable();
        instance = this;
        try {
            dataBaseManager = new DataBaseManager(new SQL());
        } catch (SQLException e) {
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
    }
    public static BetterSkyBlock getInstance() {
        return instance;
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        return this.chunkGenerator;
    }
}
