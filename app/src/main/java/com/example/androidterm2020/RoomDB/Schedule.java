package com.example.androidterm2020.RoomDB;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import java.util.Date;

@Entity(tableName = "schedule_tb")
public class Schedule {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "sid")
    private int sid;

    @NonNull
    @ColumnInfo(name = "title")
    private String title;


    @NonNull
    @ColumnInfo(name = "strDate")
    private long strDate;

    @NonNull
    @ColumnInfo(name = "endDate")
    @TypeConverters({TimestampConverter.class})
    private long endDate;

    @NonNull
    @ColumnInfo(name = "details")
    private String details;

    @NonNull
    @ColumnInfo(name = "period")
    private int period;

    @NonNull
    @ColumnInfo(name = "dateNum")
    private int dateNum;

    @NonNull
    @ColumnInfo(name = "index")
    private int index;

    @NonNull
    @ColumnInfo(name = "achievementData")
    private String achievementData;

    public Schedule(@NonNull String title, long strDate, long endDate, String details, int period, int dateNum, int index, String achievementData) {
        this.title = title;
        this.strDate = strDate;
        this.endDate = endDate;
        this.details = details;
        this.period = period;
        this. dateNum = dateNum;
        this.index = index;
        this.achievementData = achievementData;
    }

    public int getSid() {
        return sid;
    }
    public void setSid(int sid) {
        this.sid = sid;
    }

    @NonNull
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {this.title = title;}

    @NonNull
    public long getStrDate() {
        return strDate;
    }
    public void setStrDate(@NonNull long strDate) {
        this.strDate = strDate;
    }

    @NonNull
    public long getEndDate() {
        return endDate;
    }
    public void setEndDate(@NonNull long endDate) {
        this.endDate = endDate;
    }

    @NonNull
    public String getDetails() {
        return details;
    }
    public void setDetails(@NonNull String details) {
        this.details = details;
    }

    public int getPeriod() {
        return period;
    }
    public void setPeriod(int period) {
        this.period = period;
    }

    public int getDateNum() {
        return dateNum;
    }
    public void setDateNum(int dateNum) {
        this.dateNum = dateNum;
    }

    public int getIndex() {
        return index;
    }
    public void setIndex(int index) {
        this.index = index;
    }

    @NonNull
    public String getAchievementData() {
        return achievementData;
    }
    public void setAchievementData(@NonNull String achievementData) {
        this.achievementData = achievementData;
    }
}
