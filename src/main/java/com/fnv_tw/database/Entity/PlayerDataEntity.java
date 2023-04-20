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
@DatabaseTable(tableName = "player_data")
public class PlayerDataEntity {
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(columnName = "player_uuid", unique = true)
    private UUID playerUuid;
    @DatabaseField(columnName = "border_size")
    private int borderSize;
    @DatabaseField(columnName = "create_time",dataType = DataType.DATE_STRING, format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    @DatabaseField(columnName = "modify_time",dataType = DataType.DATE_STRING, version = true, format = "yyyy-MM-dd HH:mm:ss")
    private Date modifyTime;
    public PlayerDataEntity() {
        this.createTime = new Date();
    }
}
