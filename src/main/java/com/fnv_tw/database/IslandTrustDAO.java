package com.fnv_tw.database;

import com.fnv_tw.database.Entity.IslandEntity;
import com.fnv_tw.database.Entity.IslandTrustEntity;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

public class IslandTrustDAO extends BaseDaoImpl<IslandTrustEntity, Integer> {
    private static IslandTrustDAO instance;

    private IslandTrustDAO(ConnectionSource connectionSource, Class<IslandTrustEntity> dataClass) throws SQLException {
        super(connectionSource, dataClass);
        TableUtils.createTableIfNotExists(connectionSource, dataClass);
    }
    // Singleton
    public static synchronized IslandTrustDAO getInstance(ConnectionSource connectionSource, Class<IslandTrustEntity> dataClass){
        if (instance == null) {
            try {
                instance = new IslandTrustDAO(connectionSource, dataClass);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }
}
