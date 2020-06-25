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

    @Query("SELECT * from schedule_tb ORDER BY strDate ASC")
    LiveData<List<Schedule>> getALLSchedulesObserve();

    @Query("SELECT * from schedule_tb WHERE strDate >= :from  AND strDate <= :to ORDER BY strDate ASC")
    List<Schedule> getDateSchedules(long from, long to);

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


//    @Query("SELECT * from schedule_tb ORDER BY strDate ASC")
//    LiveData<List<Schedule>> getAllSchedules();
//
//    @Query("SELECT * from schedule_tb WHERE strDate >= :date ORDER BY strDate ASC")
//    LiveData<List<Schedule>> getSchedulesByDate(String date); // 매인에서 그날에 등록된 일정이 있는지 없는지 확인용.
//
//    @Query("SELECT * from schedule_tb where sid = :id")
//    Schedule getScheduleById(int id); // 달성도 찾는데 사용함.
//
//    @Insert(onConflict = OnConflictStrategy.IGNORE)
//    void insert(Schedule name);
//
//    @Update
//    void updateSchedule(Schedule schedule); // PK가 같은 녀석을 찾아서 update
//
//    @Query("DELETE FROM schedule_tb")
//    void deleteScheduleAll();
//
//    @Delete
//    void deleteSchedules(Schedule... schedules);
//
//    @Query("DELETE FROM schedule_tb WHERE strDate < :date")
//    void deletePreviousDate(String date); // 보통은 지난 일정을 지우는 용으로 사용.
}
