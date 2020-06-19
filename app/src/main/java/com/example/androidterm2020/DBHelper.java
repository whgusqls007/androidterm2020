package com.example.androidterm2020;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "sch.db"; // 최종 단계에서 이름 수정예정.
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "sch_tb"; // 최종 단계에서 이름 수정예정.

    // 가지고 있는 column들의 이름.
    public static final String SCHEDULE_ID = "_id";
    public static final String SCHEDULE_TITLE = "title";
    public static final String SCHEDULE_START_DATE = "str_date";
    public static final String SCHEDULE_END_DATE = "end_date";
    public static final String SCHEDULE_DETAILS = "details";
    public static final String SCHEDULE_PERIOD = "period";
    public static final String SCHEDULE_DATE_NUM = "date_num"; // 시작일부터 마지막 일까지 크기.
    public static final String SCHEDULE_ACHIEVEMENT_INDEX = "index_"; // 현재 달성도 데이터를 가르키는 위치. 0~date_num -1이 벙위이다.
    public static final String SCHEDULE_ACHIEVEMENT_DATA = "achievement_data";

    public static final String[] ALL_COLUMNS = {SCHEDULE_ID, SCHEDULE_TITLE, SCHEDULE_START_DATE, SCHEDULE_END_DATE,
            SCHEDULE_DETAILS, SCHEDULE_PERIOD, SCHEDULE_DATE_NUM, SCHEDULE_ACHIEVEMENT_INDEX, SCHEDULE_ACHIEVEMENT_DATA};

    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    SCHEDULE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    SCHEDULE_TITLE + " TEXT, " +
                    SCHEDULE_START_DATE + " INTEGER, " +
                    SCHEDULE_END_DATE + " TEXT, " +
                    SCHEDULE_DETAILS + " TEXT, " +
                    SCHEDULE_PERIOD + " INTEGER, " +
                    SCHEDULE_DATE_NUM + " INTEGER, " +
                    SCHEDULE_ACHIEVEMENT_INDEX + " INTEGER, " +
                    SCHEDULE_ACHIEVEMENT_DATA + " TEXT " +
                    ")";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static String getDbName() {
        return DATABASE_NAME;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 일정제목, 시작일, 종료일, 세부내용, 주기까지 일단 추가하는 방식이다. 추후 달성도 계산하기위한 자료형(높은확률로 text나 varchar)도 고민해야함.
        // 지속적으로 수정예정이다.
        // 일단 주기 설정은 0, 1, 2이렇게 구분하려고 한다. -> 가장 최근에 선택한 checkbox를 기준으로 설정하거나, checkbox가 하나만 설정되게 만들어야한다.
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME);
        onCreate(db);
    }

}
