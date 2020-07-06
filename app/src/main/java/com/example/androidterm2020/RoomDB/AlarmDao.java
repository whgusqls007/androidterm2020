package com.example.androidterm2020.RoomDB;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface AlarmDao {
    @Query("SELECT * from alarm_tb ORDER BY aid ASC")
    List<Alarm> getAllAlarms();

    @Query("SELECT * from alarm_tb ORDER BY aid ASC")
    LiveData<List<Alarm>> getAllAlarmsObserve();

    @Query("SELECT * from alarm_tb WHERE scheduleId = :targetScheduleId ORDER BY aid ASC LIMIT 1")
    Alarm getAlarmByScheduleId(int targetScheduleId);

    @Query("SELECT aid from alarm_tb WHERE scheduleId = :targetScheduleId ORDER BY aid ASC LIMIT 1")
    int getAlarmIdByScheduleId(int targetScheduleId);

    @Query("SELECT aid from alarm_tb ORDER BY aid DESC LIMIT 1")
    int getLastIdAlarm();

    @Query("SELECT scheduleId from alarm_tb WHERE aid = :targetId ORDER BY aid DESC LIMIT 1")
    int getScheduleIdByAlarmId(int targetId);

    @Query("SELECT * from alarm_tb WHERE aid = :targetId ORDER BY aid DESC LIMIT 1")
    Alarm getAlarmByAlarmId(int targetId);


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAlarm(Alarm alarm);

    @Update
    void updateAlarm(Alarm alarm);



    @Query("DELETE FROM alarm_tb")
    void deleteAlarmsAll();

    @Query("DELETE FROM alarm_tb WHERE ScheduleId = :targetScheduleId")
    void deleteAlarmByScheduleId(int targetScheduleId);
}
