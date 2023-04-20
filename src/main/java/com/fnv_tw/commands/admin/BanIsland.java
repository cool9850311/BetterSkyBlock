package com.fnv_tw.commands.admin;

import com.fnv_tw.BetterSkyBlock;
import com.fnv_tw.configs.Language;
import com.fnv_tw.managers.IslandManager;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BanIsland  implements TabCompleter, CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        BetterSkyBlock plugin = BetterSkyBlock.getInstance();
        Language languageConfig = plugin.getLanguageConfigManager().getConfig();
        IslandManager islandManager = plugin.getIslandManager();
        if (!(commandSender instanceof ConsoleCommandSender) && !commandSender.hasPermission("betterskyblock.admin")) {
            commandSender.sendMessage(ChatColor.RED + languageConfig.getDoNotHasPermission());
            return false;
        }
        if(strings.length != 2){
            commandSender.sendMessage(ChatColor.RED + languageConfig.getWrongCommand());
            return false;
        }
        islandManager.banOrUnbanIsland(commandSender, strings[1]);
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return null;
    }
}
