package com.fnv_tw.configs;

import lombok.Data;

@Data
public class Language {
    private String playerOnlyCommand = "Player only command.";
    private String doNotHasPermission = "You don't have permission to do that.";
    private String wrongCommand = "You type the wrong command.";
    private String islandNameExistOnCreate = "Island name already exist.";
    private String islandLimitReached = "Island limit reached, you can not create new islands.";
    private String serverError = "Internal server error pls, contact server owner.";
    private String createIslandSuccess = "Island create successfully, wait for teleport.";
    private String islandNameNotExistOnTeleport = "Island name does not exist.";
    private String loadIslandPleaseWait = "Loading Island, please wait.";
    private String notInIslandTrustList = "You can not join an untrusted island.";
    private String alreadyTrusted = "This Player is already in trust list.";
    private String notTrusted = "This Player is not in trust list.";
    private String playerNotFound = "Player not found or offline.";
    private String addTrustSuccess = "Add player to trust list successfully.";
    private String removeTrustSuccess = "Remove player from trust list successfully.";
    private String ownerAlwaysTrusted = "Island owner is always trusted.";
    private String notOnIsland = "You are not on an island.";
}
