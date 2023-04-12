package com.fnv_tw.database;

import com.fnv_tw.database.Entity.IslandEntity;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

public class IslandDAO extends BaseDaoImpl<IslandEntity, Integer> {

    public IslandDAO(ConnectionSource connectionSource, Class<IslandEntity> dataClass) throws SQLException {
        super(connectionSource, dataClass);
        TableUtils.createTableIfNotExists(connectionSource, IslandEntity.class);
    }

}