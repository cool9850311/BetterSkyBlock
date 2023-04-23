package com.fnv_tw.utils;

import org.bukkit.Bukkit;
import org.bukkit.util.Vector;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class PlayerUUIDUtil {
    public static UUID getPlayerUUID(String playerName) throws Exception {
        String url = "https://api.mojang.com/users/profiles/minecraft/" + playerName;
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line = reader.lines().collect(Collectors.joining());
//        Bukkit.getLogger().info("getPlayerUUID:" + line);
        reader.close();
        String uuidToFormat = (String) SerializerUtil.deserialize(line, Map.class).get("id");
        String playerUUID = uuidToFormat.substring(0, 8) + "-"
                + uuidToFormat.substring(8, 12) + "-"
                + uuidToFormat.substring(12, 16) + "-"
                + uuidToFormat.substring(16, 20) + "-"
                + uuidToFormat.substring(20, 32);
        return UUID.fromString(playerUUID);
    }
}
