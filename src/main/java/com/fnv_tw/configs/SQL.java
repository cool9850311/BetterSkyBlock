package com.fnv_tw.configs;

import lombok.Data;

@Data
public class SQL {

    private String driver = "SQLITE";
    private String host = "localhost";
    private String database = "BetterSkyBlock";
    private String username = "";
    private String password = "";
    private int port = 3306;
    private boolean useSSL = false;


    /**
     * Represents a Driver of a database.
     */
    public enum Driver {
        MYSQL,
        SQLITE
    }
    public Driver getEnumDriver(){
        if (Driver.SQLITE.toString().equals(driver)) {
            return Driver.SQLITE;
        }
        return Driver.MYSQL;
    }

}
