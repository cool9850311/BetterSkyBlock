package com.fnv_tw.database;

import com.fnv_tw.database.Entity.PlayerDataEntity;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

public class PlayerDataDAO extends BaseDaoImpl<PlayerDataEntity, Integer> {
    private static PlayerDataDAO instance;

    private PlayerDataDAO(ConnectionSource connectionSource, Class<PlayerDataEntity> dataClass) throws SQLException {
        super(connectionSource, dataClass);
        TableUtils.createTableIfNotExists(connectionSource, dataClass);
    }
    // Singleton
    public static synchronized PlayerDataDAO getInstance(ConnectionSource connectionSource, Class<PlayerDataEntity> dataClass) {
        if (instance == null) {
            try {
                instance = new PlayerDataDAO(connectionSource, dataClass);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }
}
