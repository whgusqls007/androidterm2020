package com.example.androidterm2020.RoomDB;


import androidx.room.TypeConverter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimestampConverter {
    static DateFormat df = new SimpleDateFormat(Constants.TIME_STAMP_FORMAT);

    @TypeConverter
    public static Date toDate(Long timestamp) {
        return timestamp == null ? null : new Date(timestamp);
    }

    @TypeConverter
    public static Long toTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
