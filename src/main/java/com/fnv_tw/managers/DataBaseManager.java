package com.fnv_tw.managers;

import com.fnv_tw.BetterSkyBlock;
import com.fnv_tw.configs.SQL;
import com.fnv_tw.database.tableManager.TableManager;
import com.j256.ormlite.db.DatabaseType;
import com.j256.ormlite.db.DatabaseTypeUtils;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import com.j256.ormlite.support.ConnectionSource;


import java.io.File;
import java.sql.SQLException;


public class DataBaseManager {
    private static final Logger logger = LoggerFactory.getLogger(DataBaseManager.class);
    private static final String SQLITE_DB_URL_PREFIX = "jdbc:sqlite:";
    private static final String MYSQL_DB_URL_PREFIX = "jdbc:mysql://";

    private final ConnectionSource connectionSource;

    // FIXME: you seem to not be using the Xerial SQLite driver. error
    public DataBaseManager(SQL sqlConfig) throws SQLException {
        String dbUrl;
        switch (sqlConfig.getEnumDriver()) {
            case SQLITE:
                dbUrl = SQLITE_DB_URL_PREFIX + new File(BetterSkyBlock.getInstance().getDataFolder(), sqlConfig.getDatabase() + ".db");
                break;
            case MYSQL:
                dbUrl = MYSQL_DB_URL_PREFIX + sqlConfig.getHost() + ":" + sqlConfig.getPort() + "/" + sqlConfig.getDatabase() + "?useSSL=" + sqlConfig.isUseSSL();
                break;
            default:
                throw new IllegalArgumentException("Unsupported database type: " + sqlConfig.getDriver());
        }

        logger.info("Connecting to {} database: {}", sqlConfig.getDriver(), dbUrl);
        connectionSource = new JdbcConnectionSource(dbUrl, sqlConfig.getUsername(), sqlConfig.getPassword(), DatabaseTypeUtils.createDatabaseType(dbUrl));
    }
    public ConnectionSource getConnectionSource() {
        return connectionSource;
    }

//    public void initializeDatabase() throws SQLException {
//        tableManager.createTables();
//    }
//
//    public void resetDatabase() throws SQLException {
//        tableManager.dropTables();
//        tableManager.createTables();
//    }
//
//    public void dropDatabase() throws SQLException {
//        tableManager.dropTables();
//    }

    public void close() throws SQLException {
        connectionSource.close();
    }
}