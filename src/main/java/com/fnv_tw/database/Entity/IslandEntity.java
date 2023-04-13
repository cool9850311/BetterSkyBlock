package com.fnv_tw.database.Entity;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
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
    @DatabaseField(columnName = "create_time",dataType = DataType.DATE_STRING, format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    @DatabaseField(columnName = "modify_time",dataType = DataType.DATE_STRING, version = true, format = "yyyy-MM-dd HH:mm:ss")
    private Date modifyTime;

    public IslandEntity() {
        this.createTime = new Date();
    }
}
