package com.fnv_tw.commands.admin;

import com.fnv_tw.BetterSkyBlock;
import com.fnv_tw.configs.Language;
import com.fnv_tw.managers.IslandManager;
import com.fnv_tw.managers.PlayerDataManager;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ChangeIslandNumberLimit implements TabCompleter, CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        BetterSkyBlock plugin = BetterSkyBlock.getInstance();
        Language languageConfig = plugin.getLanguageConfigManager().getConfig();
        PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
        if (!(commandSender instanceof ConsoleCommandSender) && !commandSender.hasPermission("betterskyblock.admin")) {
            commandSender.sendMessage(ChatColor.RED + languageConfig.getDoNotHasPermission());
            return false;
        }
        if (strings[1].equals("info") && strings.length == 3) {
            playerDataManager.getPlayerIslandLimit(commandSender, strings[2]);
            return true;
        }
        if(strings.length != 4){
            commandSender.sendMessage(ChatColor.RED + languageConfig.getWrongCommand());
            return false;
        }
        int size;
        try {
            size = Integer.parseInt(strings[3]);
        } catch (NumberFormatException e) {
            commandSender.sendMessage(ChatColor.RED + languageConfig.getWrongCommand());
            return false;
        }
        if (strings[1].equals("add")) {
            playerDataManager.addPlayerIslandLimit(commandSender, strings[2], size);
            return true;
        }
        if (strings[1].equals("set")) {
            playerDataManager.setPlayerIslandLimit(commandSender, strings[2], size);
            return true;
        }
        commandSender.sendMessage(ChatColor.RED + languageConfig.getWrongCommand());
        return false;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        List<String> islandLimitTabComplete = new ArrayList<>();
        islandLimitTabComplete.add("add");
        islandLimitTabComplete.add("set");
        islandLimitTabComplete.add("info");
        if (strings.length == 2) {
            return islandLimitTabComplete;
        }
        return null;
    }
}
