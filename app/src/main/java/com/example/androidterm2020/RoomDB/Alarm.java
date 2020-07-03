package com.example.androidterm2020.RoomDB;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "alarm_tb", foreignKeys = @ForeignKey(entity = Schedule.class,
        parentColumns = "sid",
        childColumns = "scheduleId"))
public class Alarm {


    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "aid")
    private int requestId;


    @ColumnInfo(name = "scheduleId")
    private final int scheduleId;

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public int getScheduleId() {
        return scheduleId;
    }

    public int getRequestId() {
        return requestId;
    }

    public Alarm(int scheduleId) {
        this.scheduleId = scheduleId;
    }
}
