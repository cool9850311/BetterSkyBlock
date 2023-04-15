package com.fnv_tw.managers;

import com.fnv_tw.BetterSkyBlock;
import org.bukkit.Bukkit;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ConfigManager<T> {
    private Yaml yaml;
    private final String configFilePath;
    private final T defaultConfig;
    private T configObject;

    public ConfigManager(T defaultConfig) {
        this.configFilePath = BetterSkyBlock.getInstance().getDataFolder() + "/" + defaultConfig.getClass().getSimpleName() + ".yml";
        // Bukkit.getLogger().info("configFilePath:" + configFilePath);
        this.defaultConfig = defaultConfig;
        // TODO: encapsulate new Yaml with options in method
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        yaml = new Yaml(options);
        this.loadConfig();
    }

    public T getConfig() {
        return configObject;
    }
    public void saveConfig() {
        try {
            FileWriter writer = new FileWriter(configFilePath);
            yaml.dump(configObject, writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadConfig() {
        File configFile = new File(configFilePath);
        if (configFile.exists()) {
            try (InputStream inputStream = Files.newInputStream(Paths.get(configFilePath))) {
                yaml = new Yaml(new Constructor(defaultConfig.getClass()));
                configObject = yaml.load(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                configFile.getParentFile().mkdirs();
                configFile.createNewFile();
                FileWriter writer = new FileWriter(configFile);
                yaml.dump(defaultConfig, writer);
                writer.close();
                configObject = defaultConfig;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}