package com.example.androidterm2020.RoomDB;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Alarm {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "aid")
    private int aid;

    public int getAid() {
        return aid;
    }

    public void setAid(int aid) {
        this.aid = aid;
    }
}
