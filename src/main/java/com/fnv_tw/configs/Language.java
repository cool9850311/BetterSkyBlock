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
}
