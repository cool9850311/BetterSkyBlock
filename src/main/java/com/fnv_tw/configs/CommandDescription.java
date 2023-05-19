package com.fnv_tw.configs;

import lombok.Data;

@Data
public class CommandDescription {
    private String islandCreateCommand = "/is create <island name> - Create an island.";
    private String islandTeleportCommand = "/is tp <island name> - Teleport to the island.";
    private String islandBoatTeleportCommand = "/is boatTp <island name> - Teleport the Mob on the same boat as you to the island.";
    private String islandSetHomeCommand = "/is sethome - Set the home of your island.";
    private String islandRenameCommand = "/is rename <island name> - Rename your island.";
    private String islandTeleportNormalCommand = "/is tpNormal - Teleport to overworld.";
    private String islandTrustCommand = "/is trust (add|remove) <player name> - Add or Remove a player from trust list.";
    private String islandTrustListCommand = "/is trust list - Show the trust list.";
    private String islandPublicCommand = "/is public - Switch privacy of your island.";
}
