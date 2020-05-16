package com.example.androidterm2020;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
    public static String DB_NAME = "test2.db"; // 임시 테스트용 db이고 추후 최종단계에서 이름변경예정.
    public static int VERSION = 1;
    public static String TB_NAME = "temp_tb"; // 임시 테스트용 테이블 이름이고 추후 최종단계에서 이름변경예정.

    public DBHelper(TableTool context) {
        super(context, DB_NAME, null, VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        println("onCreate 호출됨");

        // 일정제목, 시작일, 종료일, 세부내용, 주기까지 일단 추가하는 방식이다. 추후 달성도 계산하기위한 자료형(높은확률로 text나 varchar)도 고민해야함.
        String sql = "create table if not exists "+ TB_NAME +"("
                + "_id integer PRIMARY KEY autoincrement, "
                + " title text, "
                + " str_date datetime, "
                + " end_date datetime, "
                + " details text, "
                + " period integer)";
        // 지속적으로 수정예정이다.
        // 일단 주기 설정은 0, 1, 2이렇게 구분하려고 한다. -> 가장 최근에 선택한 checkbox를 기준으로 설정하거나, checkbox가 하나만 설정되게 만들어야한다.

        db.execSQL(sql);
    }

    public void onOpen(SQLiteDatabase db) {
        println("onOpen 호출됨");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        println("onUpgrade 호출됨: " + oldVersion + " -> " + newVersion);

        if (newVersion > 1) {
            db.execSQL("drop table if exists " + TB_NAME);
        }
    }

    public void println(String data) {
        Log.d("DatabaseHelper", data);
    }

}
