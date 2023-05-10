package com.fnv_tw.commands;

import com.fnv_tw.BetterSkyBlock;
import com.fnv_tw.configs.Language;
import com.fnv_tw.configs.MainConfig;
import com.fnv_tw.utils.LocationUtil;
import com.fnv_tw.utils.SerializerUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TeleportNormalWorld implements TabCompleter, CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        BetterSkyBlock plugin = BetterSkyBlock.getInstance();
        Language languageConfig = plugin.getLanguageConfigManager().getConfig();
        MainConfig mainConfig = plugin.getMainConfigConfigManager().getConfig();
        if (!(commandSender instanceof Player)) {
            plugin.getLogger().info(languageConfig.getPlayerOnlyCommand());
            return false;
        }
        if(!commandSender.hasPermission("betterskyblock.usage")){
            commandSender.sendMessage(ChatColor.RED + languageConfig.getDoNotHasPermission());
            return false;
        }
        if(strings.length != 1){
            commandSender.sendMessage(ChatColor.RED + languageConfig.getWrongCommand());
            return false;
        }
        World world = Bukkit.getWorld(mainConfig.getDefaultWorldName());
        Vector position = SerializerUtil.deserialize(mainConfig.getDefaultWorldSpawn(),Vector.class);
        Location location = new Location(world,position.getX(),position.getY(),position.getZ());
        if (LocationUtil.isSafe(location)) {
            ((Player) commandSender).setFallDistance(0.0f);
            ((Player) commandSender).teleport(location);
            return true;
        }
        ((Player) commandSender).setFallDistance(0.0f);
        ((Player) commandSender).teleport(LocationUtil.getSafeLocation(new Location(world,position.getX(),position.getY(),position.getZ())));
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return null;
    }
}
