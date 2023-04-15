package com.fnv_tw.database;

import com.fnv_tw.database.Entity.BorderEntity;
import com.fnv_tw.database.Entity.IslandEntity;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

public class BorderDAO extends BaseDaoImpl<BorderEntity, Integer> {
    private static BorderDAO instance;

    private BorderDAO(ConnectionSource connectionSource, Class<BorderEntity> dataClass) throws SQLException {
        super(connectionSource, dataClass);
        TableUtils.createTableIfNotExists(connectionSource, dataClass);
    }
    // Singleton
    public static synchronized BorderDAO getInstance(ConnectionSource connectionSource, Class<BorderEntity> dataClass) {
        if (instance == null) {
            try {
                instance = new BorderDAO(connectionSource, dataClass);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }
}
