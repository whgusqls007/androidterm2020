package com.example.androidterm2020.RoomDB;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.TypeConverters;
import androidx.room.Update;

import java.util.Date;
import java.util.List;

@Dao
public interface ScheduleDao {
    @Query("SELECT * from schedule_tb ORDER BY strDate ASC")
    List<Schedule> getALLSchedules();

    @Query("SELECT * from schedule_tb WHERE period != 0 ORDER BY strDate ASC")
    List<Schedule> getALLRepeatSchedules();

    @Query("SELECT * from schedule_tb ORDER BY strDate ASC")
    LiveData<List<Schedule>> getALLSchedulesObserve();

    @Query("SELECT * from schedule_tb WHERE strDate >= :from  AND strDate <= :to ORDER BY strDate ASC")
    List<Schedule> getDateSchedules(long from, long to);

    @Query("SELECT * from schedule_tb WHERE endDate >= :from  AND endDate <= :to ORDER BY strDate ASC")
    List<Schedule> getEndDateSchedules(long from, long to);

    @Query("SELECT * from schedule_tb WHERE sid = :id LIMIT 1")
    Schedule getScheduleById(int id);

    @Query("SELECT COUNT(sid) from schedule_tb WHERE strDate >= :from  AND strDate <= :to")
    int getDateCountedSchedules(long from, long to);

    @Query("SELECT sid from schedule_tb ORDER BY sid DESC LIMIT 1")
    int getLastIdSchedule();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertSchedule(Schedule word);

    @Update
    void updateSchedule(Schedule schedule);


    @Query("DELETE FROM schedule_tb")
    void deleteScheduleAll();

    @Query("DELETE FROM schedule_tb WHERE sid = :targetId")
    void deleteScheduleById(int targetId);

}
