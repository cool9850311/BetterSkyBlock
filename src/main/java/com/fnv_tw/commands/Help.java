package com.fnv_tw.commands;

import com.fnv_tw.BetterSkyBlock;
import com.fnv_tw.configs.CommandDescription;
import com.fnv_tw.configs.Language;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.List;

public class Help implements TabCompleter, CommandExecutor {
    private final int commandsPerPage = 5;


    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        BetterSkyBlock plugin = BetterSkyBlock.getInstance();
        Language languageConfig = plugin.getLanguageConfigManager().getConfig();
        CommandDescription commandDescription = plugin.getCommandDescriptionConfigManager().getConfig();
        if (!(commandSender instanceof Player)) {
            plugin.getLogger().info(languageConfig.getPlayerOnlyCommand());
            return false;
        }
        if(!commandSender.hasPermission("betterskyblock.usage")){
            commandSender.sendMessage(ChatColor.RED + languageConfig.getDoNotHasPermission());
            return false;
        }
        if(strings.length != 1 && strings.length != 2){
            commandSender.sendMessage(ChatColor.RED + languageConfig.getWrongCommand());
            return false;
        }
        Field[] fields = CommandDescription.class.getDeclaredFields();

        String[] commands = new String[fields.length];
        int totalPages = (int) Math.ceil(commands.length / (double) commandsPerPage);

        for (int i = 0; i < fields.length; i++) {
//            String fieldName = fields[i].getName();
            String fieldValue = null;
            try {
                fields[i].setAccessible(true);
                fieldValue = (String) fields[i].get(commandDescription);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            commands[i] = fieldValue;
        }
        int page = 1;

        // 如果有指定頁數，則檢查是否合法
        if (strings.length > 1) {
            try {
                page = Integer.parseInt(strings[1]);
            } catch (NumberFormatException e) {
                commandSender.sendMessage(ChatColor.RED + languageConfig.getWrongCommand());
                return true;
            }
            if (page < 1 || page > totalPages) {
                commandSender.sendMessage(ChatColor.RED + languageConfig.getPageOutOfRange());
                return true;
            }
        }

        // 計算要顯示的指令的起始和結束索引
        int startIndex = (page - 1) * commandsPerPage;
        int endIndex = Math.min(page * commandsPerPage, commands.length);

        // 建立一個文字元件的建構器
        ComponentBuilder builder = new ComponentBuilder("");

        // 加入標題和當前頁數
        builder.append("===== " + languageConfig.getIslandHelp() + " (" + page + "/" + totalPages + ") =====\n")
                .color(ChatColor.GOLD)
                .bold(true);

        // 加入每個指令和說明
        for (int i = startIndex; i < endIndex; i++) {
            builder.append(commands[i] + "\n")
                    .color(ChatColor.WHITE)
                    .bold(false);
        }

        builder.append("      ").bold(true);;
        // 加入換頁的按鈕
        builder.append("[").color(ChatColor.GRAY);
        builder.append(languageConfig.getIslandHelpPreviousPage()).color(ChatColor.AQUA)
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/is help " + (page - 1))) // 點擊時執行/is help [上一頁]
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(languageConfig.getIslandHelpPreviousPageHover()).create())); // 懸停時顯示提示文字
        builder.append("] [").color(ChatColor.GRAY);
        builder.append(languageConfig.getIslandHelpNextPage()).color(ChatColor.AQUA)
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/is help " + (page + 1))) // 點擊時執行/is help [下一頁]
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(languageConfig.getIslandHelpNextPageHover()).create())); // 懸停時顯示提示文字
        builder.append("]").color(ChatColor.GRAY);

        // 將文字元件發送給玩家
        commandSender.spigot().sendMessage(builder.create());

        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return null;
    }
}
