package com.fnv_tw;

import com.fnv_tw.commands.*;
import com.fnv_tw.commands.admin.ChangeBorderSize;
import com.fnv_tw.commands.admin.UnloadIsland;
import com.fnv_tw.configs.MainConfig;
import com.fnv_tw.configs.Language;
import com.fnv_tw.configs.SQL;
import com.fnv_tw.generator.VoidGenerator;
import com.fnv_tw.managers.CommandManager;
import com.fnv_tw.managers.ConfigManager;
import com.fnv_tw.managers.DataBaseManager;
import com.fnv_tw.managers.IslandManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
@Getter
public class BetterSkyBlock extends JavaPlugin {
    private static BetterSkyBlock instance;
    private ChunkGenerator chunkGenerator;
    private DataBaseManager dataBaseManager;
    private IslandManager islandManager;
    private CommandManager commandManager;
    private CommandManager adminCommandManager;

    private ConfigManager<SQL> sqlConfigManager;
    private ConfigManager<Language> languageConfigManager;
    private ConfigManager<MainConfig> mainConfigConfigManager;
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

        commandManager = new CommandManager("BetterSkyBlockIsland");
        adminCommandManager = new CommandManager("AdminIslandCommand");
        registerCommands();
        loadConfigs();
        try {
            dataBaseManager = new DataBaseManager(sqlConfigManager.getConfig());
        } catch (SQLException e) {
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        islandManager = new IslandManager();
        // 30s
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            public void run() {
                islandManager.unloadUnusedWorldTask();
            }
        }, 0L, 600L);
        Bukkit.getLogger().info("------------------------------------");
        Bukkit.getLogger().info("");
        Bukkit.getLogger().info("BetterSkyBlock Enabled");
        Bukkit.getLogger().info("");
        Bukkit.getLogger().info("------------------------------------");
    }

    @Override
    public void onDisable() {
        super.onDisable();
        Bukkit.getLogger().info("------------------------------------");
        Bukkit.getLogger().info("");
        Bukkit.getLogger().info("BetterSkyBlock Disabled");
        Bukkit.getLogger().info("");
        Bukkit.getLogger().info("------------------------------------");
    }

    public static BetterSkyBlock getInstance() {
        return instance;
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        return this.chunkGenerator;
    }

    private void loadConfigs() {
        mainConfigConfigManager = new ConfigManager<>(new MainConfig());
        sqlConfigManager = new ConfigManager<>(new SQL());
        languageConfigManager = new ConfigManager<>(new Language());
        // Bukkit.getLogger().info("sqlConfig:" + sqlConfig.getPort());
    }
    private void registerCommands() {
        // admin commands
        adminCommandManager.registerCommand("changeBorderSize",new ChangeBorderSize());
        adminCommandManager.registerCommand("unloadIsland", new UnloadIsland());
        // general commands
        commandManager.registerCommand("create",new CreateIsland());
        commandManager.registerCommand("tp",new TeleportIsland());
        commandManager.registerCommand("tpNormal",new TeleportNormalWorld());
        commandManager.registerCommand("changeIslandName",new ChangeIslandName());
        commandManager.registerCommand("sethome",new ChangeHome());
        commandManager.registerCommand("trust",new Trust());
    }
}
