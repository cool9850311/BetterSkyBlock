package com.fnv_tw.utils;

import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LocationUtil {
    private static List<Material> unsafeBlocks = Lists.newArrayList(Material.LAVA, Material.WATER, Material.END_PORTAL);

    public static boolean isSafe(Location location) {
        Block block = location.getBlock();
        Block above = location.clone().add(0, 1, 0).getBlock();
        Block below = location.clone().subtract(0, 1, 0).getBlock();
        return block.isPassable() && above.isPassable() && !below.isPassable() && !unsafeBlocks.contains(below.getType()) && !unsafeBlocks.contains(block.getType()) && !unsafeBlocks.contains(above.getType());
    }

    public static Location getSafeLocation(Location location) {
        World world = location.getWorld();
        if (world == null) return location;

        Location highest = getHighestLocation(location.getBlockX(), location.getBlockZ(), world);
        if (isSafe(highest)) return highest;

        Location pos1 = new Location(world, -50, 0, -50);
        Location pos2 = new Location(world, 50, 0, 50);
        for (int x = pos1.getBlockX(); x <= pos2.getBlockX(); x++) {
            for (int z = pos1.getBlockZ(); z <= pos2.getBlockZ(); z++) {
                Location newLocation = getHighestLocation(x, z, world);
                if (isSafe(newLocation)) return newLocation;
            }
        }
        return location;
    }

    /**
     * Gets the highest Location in a world
     * Mojang was dum and changed how this worked
     *
     * @param x     the x coord
     * @param z     the z coord
     * @param world The world
     * @return The highest AIR location
     */
    private static Location getHighestLocation(int x, int z, World world) {
        Block block = world.getHighestBlockAt(x, z);
        while (!block.isPassable()) {
            block = block.getLocation().add(0, 1, 0).getBlock();
        }
        return block.getLocation().add(0.5, 0, 0.5);
    }

    /**
     * With the data pack, you can modify the height limits and in the Spigot API.
     * It exists since 1.17 on Spigot and 1.16 at PaperMC.
     *
     * @param world The world
     * @return The lowest AIR location.
     */
    public static int getMinHeight(World world) {
        int version = Integer.parseInt(Bukkit.getVersion().split("-")[0].split("\\.")[1]);
        return version >= 17 ? world.getMinHeight() : 0;  // World#getMinHeight() -> Available only in 1.17 Spigot and 1.16.5 PaperMC
    }
}
