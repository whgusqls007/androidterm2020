package com.example.androidterm2020.RoomDB;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "alarm_tb")
public class Alarm {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "aid")
    private int aid;

    private int requestId;

    public int getAid() {
        return aid;
    }

    public void setAid(int aid) {
        this.aid = aid;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }
}
