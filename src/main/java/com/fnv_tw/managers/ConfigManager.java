package com.fnv_tw.managers;

import com.fnv_tw.BetterSkyBlock;
import com.fnv_tw.utils.ObjectToLinkedHashMapConverter;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;

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
        this.loadConfig(defaultConfig);
    }

    public T getConfig() {
        return configObject;
    }
    public void saveConfig(T configObject) {
        try {
            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            yaml = new Yaml(options);
            FileWriter writer = new FileWriter(configFilePath);
            ObjectToLinkedHashMapConverter<T> converter = new ObjectToLinkedHashMapConverter<>();
            LinkedHashMap<String, Object> map = converter.toLinkedHashMap(configObject);
            yaml.dump(map, writer);
            writer.close();
        } catch (IOException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void loadConfig(T defaultConfig) {
        File configFile = new File(configFilePath);
        if (configFile.exists()) {
            try (InputStream inputStream = Files.newInputStream(Paths.get(configFilePath))) {
                Representer representer = new Representer();
                representer.getPropertyUtils().setSkipMissingProperties(true);
                yaml = new Yaml(new Constructor(defaultConfig.getClass()),representer);
                configObject = yaml.load(inputStream);
                saveConfig(configObject);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                configFile.getParentFile().mkdirs();
                configFile.createNewFile();
                FileWriter writer = new FileWriter(configFile);
                ObjectToLinkedHashMapConverter<T> converter = new ObjectToLinkedHashMapConverter<>();
                LinkedHashMap<String, Object> map = converter.toLinkedHashMap(defaultConfig);
                yaml.dump(map, writer);
                writer.close();
                configObject = defaultConfig;
            } catch (IOException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}