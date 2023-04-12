package com.fnv_tw.managers;

import com.fnv_tw.BetterSkyBlock;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

public class IslandManager {
    public void createWorld(World.Environment environment, String name) {
        WorldCreator worldCreator = new WorldCreator(name)
                .generator(BetterSkyBlock.getInstance().getDefaultWorldGenerator(name, null))
                .environment(environment);
        Bukkit.createWorld(worldCreator);
    }
}
