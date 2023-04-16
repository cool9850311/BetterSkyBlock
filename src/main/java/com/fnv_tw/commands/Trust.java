package com.fnv_tw.commands;

import com.fnv_tw.BetterSkyBlock;
import com.fnv_tw.configs.Language;
import com.fnv_tw.configs.MainConfig;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Trust implements TabCompleter, CommandExecutor {
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
        if(strings.length != 3){
            commandSender.sendMessage(ChatColor.RED + languageConfig.getWrongCommand());
            return false;
        }
        int islandId = Integer.parseInt(((Player) commandSender).getWorld().getName().split("_")[1]);
        String islandName = plugin.getIslandManager().getIslandNameById(islandId);
        if (islandName == null) {
            commandSender.sendMessage(ChatColor.RED + languageConfig.getServerError());
            return false;
        }
        if (strings[1].equals("add")) {
            plugin.getIslandManager().addPlayerTrusted(((Player) commandSender), islandName, strings[2]);
        }
        if (strings[1].equals("remove")) {
            plugin.getIslandManager().removePlayerTrusted(((Player) commandSender), islandName, strings[2]);
        }
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return null;
    }
}
