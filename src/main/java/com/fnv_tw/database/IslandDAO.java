package com.fnv_tw.database;

import com.fnv_tw.database.Entity.IslandEntity;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

public class IslandDAO extends BaseDaoImpl<IslandEntity, Integer> {
    private static IslandDAO instance;

    private IslandDAO(ConnectionSource connectionSource, Class<IslandEntity> dataClass) throws SQLException {
        super(connectionSource, dataClass);
        TableUtils.createTableIfNotExists(connectionSource, dataClass);
    }
    // Singleton
    public static synchronized IslandDAO getInstance(ConnectionSource connectionSource, Class<IslandEntity> dataClass) {
        if (instance == null) {
            try {
                instance = new IslandDAO(connectionSource, dataClass);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

}