package com.fnv_tw.utils;

import org.bukkit.ChatColor;

public class ChatUtil {
    public static String format(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static String deFormat(String text) {
        return ChatColor.stripColor(text);
    }
}
