package com.fnv_tw.database.Entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@DatabaseTable(tableName = "islands")
public class IslandEntity {
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(columnName = "owner_uuid")
    private UUID ownerUuid;
    @DatabaseField
    private String name;
    @DatabaseField(columnName = "border_size")
    private int borderSize;
    @DatabaseField
    private String description;
    @DatabaseField
    private String home;
    @DatabaseField(columnName = "create_time", defaultValue = "CURRENT_TIMESTAMP")
    private Date createTime;
    @DatabaseField(columnName = "modify_time", defaultValue = "CURRENT_TIMESTAMP", version = true)
    private Date modifyTime;
}
