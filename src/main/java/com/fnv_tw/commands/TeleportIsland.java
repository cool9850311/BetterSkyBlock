package com.fnv_tw.commands;

import com.fnv_tw.BetterSkyBlock;
import com.fnv_tw.configs.Language;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TeleportIsland implements TabCompleter, CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        BetterSkyBlock plugin = BetterSkyBlock.getInstance();
        Language languageConfig = plugin.getLanguageConfigManager().getConfig();
        if (!(commandSender instanceof Player)) {
            plugin.getLogger().info(languageConfig.getPlayerOnlyCommand());
            return false;
        }
        if(!commandSender.hasPermission("betterskyblock.usage")){
            commandSender.sendMessage(ChatColor.RED + languageConfig.getDoNotHasPermission());
            return false;
        }
        if(strings.length != 2){
            commandSender.sendMessage(ChatColor.RED + languageConfig.getWrongCommand());
            return false;
        }
        commandSender.sendMessage(ChatColor.GOLD + languageConfig.getLoadIslandPleaseWait());
        plugin.getIslandManager().teleportToIsland(((Player) commandSender).getPlayer(), strings[1]);
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if (strings.length == 2) {
            BetterSkyBlock plugin = BetterSkyBlock.getInstance();
            return plugin.getIslandManager().getTrustedIslandName((Player) commandSender);
        }
        return null;
    }
}