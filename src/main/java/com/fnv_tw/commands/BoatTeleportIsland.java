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

import java.util.Collections;
import java.util.List;

public class BoatTeleportIsland implements TabCompleter, CommandExecutor {
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
        if(strings.length != 2 && strings.length != 3){
            commandSender.sendMessage(ChatColor.RED + languageConfig.getWrongCommand());
            return false;
        }
        boolean unsafe = false;
        if (strings.length == 3 && !strings[2].equals("unsafe") ) {
            commandSender.sendMessage(ChatColor.GOLD + languageConfig.getWrongCommand());
            return false;
        }
        if (strings.length == 3) {
            unsafe = true;
        }
        commandSender.sendMessage(ChatColor.GOLD + languageConfig.getLoadIslandPleaseWait());
        plugin.getIslandManager().boatTeleportToIsland(((Player) commandSender).getPlayer(), strings[1], unsafe);
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length == 2) {
            BetterSkyBlock plugin = BetterSkyBlock.getInstance();
            return plugin.getIslandManager().getTrustedIslandName((Player) commandSender);
        }
        if (strings.length == 3) {
            BetterSkyBlock plugin = BetterSkyBlock.getInstance();
            return Collections.singletonList("unsafe");
        }
        return null;
    }
}
