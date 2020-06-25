package com.example.androidterm2020.RoomDB;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Schedule.class, Alarm.class}, version = 1, exportSchema = false)
public abstract class RoomDatabase extends androidx.room.RoomDatabase {
    public abstract ScheduleDao scheduleDao();
    abstract AlarmDao alarmDao();

//    private static volatile RoomDatabase INSTANCE;
//    private static final int NUMBER_OF_THREADS = 4;
//    static final ExecutorService databaseWriteExecutor =
//            Executors.newFixedThreadPool(NUMBER_OF_THREADS);
//
//    static RoomDatabase getDatabase(final Context context) {
//        if (INSTANCE == null) {
//            synchronized (RoomDatabase.class) {
//                if (INSTANCE == null) {
//                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
//                            RoomDatabase.class, "test4_database")
//                            .addCallback(sRoomDatabaseCallback)
//                            .build();
//                }
//            }
//        }
//        return INSTANCE;
//    }
//
//    /**
//     * Override the onOpen method to populate the database.
//     * For this sample, we clear the database every time it is created or opened.
//     *
//     * If you want to populate the database only when the database is created for the 1st time,
//     * override RoomDatabase.Callback()#onCreate
//     */
//    private static androidx.room.RoomDatabase.Callback sRoomDatabaseCallback = new androidx.room.RoomDatabase.Callback() {
//        @Override
//        public void onOpen(@NonNull SupportSQLiteDatabase db) {
//            super.onOpen(db);
//
//            // If you want to keep data through app restarts,
//            // comment out the following block
//            databaseWriteExecutor.execute(new Runnable() {
//                @Override
//                public void run() { // app을 실행하기 전에 뭔가해주는 곳. 할 이유가 없으면 안해도 됨.
//                    // Populate the database in the background.
//                    // If you want to start with more words, just add them.
//                    ScheduleDao scheduleDao = INSTANCE.scheduleDao();
//                    scheduleDao.deleteWordAll();
////                    AlarmDao alarmDao = INSTANCE.alarmDao();
//                    SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//                    Date strDate = null;
//                    Date endDate = null;
//                    // 만약 값을 추가하고 싶다면 예시
////                    Word word = new Word("Hello");
////                    dao.insert(word);
////                    word = new Word("World");
////                    dao.insert(word);
//                    try {
//                        strDate = transFormat.parse( "2020-06-20 10:11:12");
//                        endDate = transFormat.parse("2020-06-21 10:11:12");
//                    } catch (Exception e) {}
//                    Schedule schedule = new Schedule("테스트용 일정", strDate, endDate,
//                            "테스트용 세부사항", 0, 1, 0, "00");
//                    scheduleDao.insertSchedule(schedule);
//                }
//            });
//        }
//    };
}
