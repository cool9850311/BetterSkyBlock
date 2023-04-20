package com.fnv_tw.managers;

import com.fnv_tw.BetterSkyBlock;
import com.fnv_tw.configs.Language;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandManager implements TabCompleter, CommandExecutor {
    private final Map<String, CommandExecutor> commands;
    private final BetterSkyBlock plugin;

    public CommandManager(String baseCommand) {
        plugin = BetterSkyBlock.getInstance();
        plugin.getCommand(baseCommand).setExecutor(this);
        plugin.getCommand(baseCommand).setTabCompleter(this);
        this.commands = new HashMap<>();
    }

    public void registerCommand(String label, CommandExecutor executor) {
        commands.put(label, executor);
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            for (String cmdLabel : commands.keySet()) {
                if (cmdLabel.startsWith(args[0])) {
                    completions.add(cmdLabel);
                }
            }
            return completions;
        } else {
            CommandExecutor executor = commands.get(args[0]);
            if (executor instanceof TabCompleter) {
                return ((TabCompleter) executor).onTabComplete(sender, command, label, args);
            } else {
                return null;
            }
        }
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        BetterSkyBlock plugin = BetterSkyBlock.getInstance();
        Language languageConfig = plugin.getLanguageConfigManager().getConfig();
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + languageConfig.getWrongCommand());
            return false;
        }
        CommandExecutor executor = commands.get(args[0]);
        if (executor != null) {
            return executor.onCommand(sender, command, label, args);
        }
        sender.sendMessage(ChatColor.RED + languageConfig.getWrongCommand());
        return false;
    }

}
