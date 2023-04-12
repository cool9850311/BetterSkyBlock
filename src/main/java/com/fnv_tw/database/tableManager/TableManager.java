package com.fnv_tw.database.tableManager;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.table.DatabaseTableConfig;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Deprecated
public class TableManager {
    private final ConnectionSource connectionSource;

    private final Map<Class<?>, Dao<?, ?>> daoMap;

    public TableManager(ConnectionSource connectionSource) {
        this.connectionSource = connectionSource;
        daoMap = new HashMap<>();
    }

    public <T> Dao<T, ?> getDao(Class<T> clazz) throws SQLException {
        if (daoMap.containsKey(clazz)) {
            return (Dao<T, ?>) daoMap.get(clazz);
        } else {
            Dao<T, ?> dao = DaoManager.createDao(connectionSource, clazz);
            daoMap.put(clazz, dao);
            createTableIfNotExists(clazz, dao);
            return dao;
        }
    }
    private <T> void createTableIfNotExists(Class<T> clazz, Dao<T, ?> dao) throws SQLException {
        DatabaseTableConfig<T> tableConfig = DatabaseTableConfig.fromClass(connectionSource, clazz);
        TableUtils.createTableIfNotExists(connectionSource, tableConfig);
    }

    public void close() throws SQLException {
        connectionSource.close();
    }
}
