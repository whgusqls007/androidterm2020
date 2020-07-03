package com.example.androidterm2020.RoomDB;

import android.content.Context;

import androidx.room.Room;

public class RoomDatabaseAccessor {
    private static RoomDatabase mRoomDatabaseInstance;
    private static final String DB_NAME = "test5.db";

//    static final ExecutorService databaseWriteExecutor =
//            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    private RoomDatabaseAccessor() {}

    public static RoomDatabase getInstance(Context context) {
        if(mRoomDatabaseInstance == null) {
            mRoomDatabaseInstance = Room.databaseBuilder(context,
                    RoomDatabase.class, DB_NAME).build();
        }

        return mRoomDatabaseInstance;
    }

    // 미리 해야할 것이 있다면, 여기를 살려서 작동시켜라.
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
////                    ScheduleDao scheduleDao = INSTANCE.scheduleDao();
////                    scheduleDao.deleteWordAll();
////                    AlarmDao alarmDao = INSTANCE.alarmDao();
////                    SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
////                    Date strDate = null;
////                    Date endDate = null;
////                    // 만약 값을 추가하고 싶다면 예시
//////                    Word word = new Word("Hello");
//////                    dao.insert(word);
//////                    word = new Word("World");
//////                    dao.insert(word);
////                    try {
////                        strDate = transFormat.parse( "2020-06-20 10:11:12");
////                        endDate = transFormat.parse("2020-06-21 10:11:12");
////                    } catch (Exception e) {}
////                    Schedule schedule = new Schedule("테스트용 일정", strDate, endDate,
////                            "테스트용 세부사항", 0, 1, 0, "00");
////                    scheduleDao.insertSchedule(schedule);
//                }
//            });
//        }
//    };
}
