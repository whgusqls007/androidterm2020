package com.example.androidterm2020;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.TextView;

public class TableTool {
    private static DBHelper dbHelper;
    private static SQLiteDatabase database;
    private static String dbName = "test2.db";
    private static String tableName = "test_tb";
    private static TextView logTxt;
    private int id;
    private String title;
    private String scheduleStrDate;
    private String scheduleEndDate ;
    private String detail;
    private int period;

    public TableTool() {
        createDatabase();
        createTable(tableName);
    }

    public void refreshCursorData(Cursor cursor) {
        id = cursor.getInt(0);
        title = cursor.getString(1);
        scheduleStrDate = cursor.getString(2);
        scheduleEndDate = cursor.getString(3);
        detail = cursor.getString(4);
        period = cursor.getInt(5);
    }

    public static String getDbName() {
        return dbName;
    }
    public static String getTableName() {
        return tableName;
    }

    public void selectQuery() {
        println("selectQuery 호출됨.");

        Cursor cursor = database.rawQuery("select * from " + tableName, null);
        int recordCount = cursor.getCount();
        println("레코드 개수: " + recordCount);

        for(int i = 0; i < recordCount; ++i) {
            cursor.moveToNext();

            refreshCursorData(cursor);

            println("레코드#" + i + " : "
                    + title + ", "
                    + scheduleStrDate + ", "
                    + scheduleEndDate + ", "
                    + detail + ", "
                    + period);
        }
        cursor.close();
    }

    // 데이터베이스를 만들거나 이미 있다면 가져오는 함수.
    private void createDatabase() {
        println("createDatabase 호출됨.");

        dbHelper = new DBHelper(this);
        database = dbHelper.getWritableDatabase();
        println("데이터베이스 생성함: " + dbName);
    }

    // 테이블을 만드는 함수 이미 있다면 만들지 않는다.
    private void createTable(String name) {
        println("createTable 호출됨.");

        if (database == null) {
            println("데이터베이스를 먼저 생성하세요.");
            return;
        }

        database.execSQL("create table if not exists " + name + "("
                + "_id integer PRIMARY KEY autoincrement, "
                + " title text, "
                + " str_date datetime, "
                + " end_date datetime, "
                + " details text, "
                + " sch_period integer)");

        println("테이블 생성함: " + name);
    }

    public void println(String data) {
        testLog.append(data + "\n");
    }
}
