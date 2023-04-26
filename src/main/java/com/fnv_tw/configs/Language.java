package com.fnv_tw.configs;

import lombok.Data;

@Data
public class Language {
    private String playerOnlyCommand = "Player only command.";
    private String doNotHasPermission = "You don't have permission to do that.";
    private String wrongCommand = "You type the wrong command.";
    private String islandNameAlreadyExist = "Island name already exist.";
    private String islandLimitReached = "Island limit reached, you can not create new islands.";
    private String serverError = "Internal server error pls, contact server owner.";
    private String createIslandSuccess = "Island create successfully, wait for teleport.";
    private String islandNameNotExist = "Island name does not exist.";
    private String loadIslandPleaseWait = "Loading Island, please wait.";
    private String notInIslandTrustList = "You can not join an untrusted island.";
    private String alreadyTrusted = "This Player is already in trust list.";
    private String notTrusted = "This Player is not in trust list.";
    private String playerNotFound = "Player not found or offline.";
    private String addTrustSuccess = "Add player to trust list successfully.";
    private String removeTrustSuccess = "Remove player from trust list successfully.";
    private String ownerAlwaysTrusted = "Island owner is always trusted.";
    private String notOnIsland = "You are not on an island.";
    private String setHomeSuccess = "Set island home successfully.";
    private String renameIslandSuccess = "Rename island successfully.";
    private String islandBanSuccess = "Ban island successfully.";
    private String islandUnbanSuccess = "Unban island successfully.";
    private String islandIsBanedWarning = "This island is baned.";
    private String teleportUnsafe = "Teleport spot is unsafe. Use the unsafe suffix if you still want to teleport.";
    private String unloadIslandSuccess = "Unload island successfully.";
    private String islandNowPublic = "The island is now public.";
    private String islandNowPrivate = "The island is now private.";
    private String trustList = "Trust List:";
    private String addPlayerBorderSizeSuccess = "Add Player Border Size Successfully.";
    private String setPlayerBorderSizeSuccess = "Set Player Border Size Successfully.";
    private String addPlayerIslandLimitSuccess = "Add Player Island Limit Successfully.";
    private String setPlayerIslandLimitSuccess = "Set Player Island Limit Successfully.";
}
