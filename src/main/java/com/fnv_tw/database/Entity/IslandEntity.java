package com.fnv_tw.database.Entity;

import com.fnv_tw.utils.SerializerUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.util.Vector;

import java.lang.reflect.Type;
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
    @DatabaseField(unique = true)
    private String name;
    @DatabaseField(columnName = "public_island")
    private boolean publicIsland;
    @DatabaseField(columnName = "bungee_server_name")
    private String bungeeServerName;
    @DatabaseField
    private String description;
    @DatabaseField
    private String home;
    @DatabaseField
    private boolean ban;
    @DatabaseField(columnName = "create_time",dataType = DataType.DATE_STRING, format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    @DatabaseField(columnName = "modify_time",dataType = DataType.DATE_STRING, version = true, format = "yyyy-MM-dd HH:mm:ss")
    private Date modifyTime;

    public IslandEntity() {
        this.createTime = new Date();
    }

    public Vector getHome() {
        return SerializerUtil.deserialize(this.home, Vector.class);
    }

    public void setHome(Vector home) {
        this.home = SerializerUtil.serialize(home);
    }
}
