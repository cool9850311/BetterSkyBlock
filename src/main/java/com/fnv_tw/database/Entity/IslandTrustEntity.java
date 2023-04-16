package com.fnv_tw.database.Entity;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@DatabaseTable(tableName = "island_trust")
public class IslandTrustEntity {
    @DatabaseField(columnName = "island_id", uniqueCombo = true)
    private int islandId;
    @DatabaseField(columnName = "player_uuid", uniqueCombo = true)
    private UUID playerUuid;
    @DatabaseField(columnName = "operator_uuid")
    private UUID operatorUuid;
    @DatabaseField(columnName = "create_time",dataType = DataType.DATE_STRING, format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    public IslandTrustEntity() {
        this.createTime = new Date();
    }
}
