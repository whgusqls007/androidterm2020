package com.example.androidterm2020;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

public class Achieve extends AppCompatActivity {
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.achieve_graph);

        Intent intent = getIntent();
        final int ID = intent.getIntExtra("ID", 0);

        String data = getAchievement(ID); //checking(); //
        int count = !data.equals("") ? Integer.parseInt(data.split("/")[0]) : 0;
        int size = !data.equals("") ? Integer.parseInt(data.split("/")[1]) : 0;
        double value = size > 0 ? (double) count/(double)size * 100 : 0;

        PieChart mPieChart = (PieChart) findViewById(R.id.piechart);
        EditText Ac = (EditText) findViewById(R.id.ac);
        EditText Nac = (EditText) findViewById(R.id.Nac);

        mPieChart.clearChart();

        mPieChart.addPieSlice(new PieModel("달성", (int)value, Color.parseColor("#CDA67F")));
        mPieChart.addPieSlice(new PieModel("미달성", 100-(int)value, Color.parseColor("#00BFFF")));

        // 화면에 보여주는 것.
        Ac.setText((size > 1 ? String.format("%.2f", value) : 0.0) + "%");
        Nac.setText((size > 1 ? String.format("%.2f", 100.0 - value) : 100.0) + "%");

        mPieChart.startAnimation();
    }

    private String getAchievement(final int ID) {
        Uri uri = ScheduleProvider.CONTENT_URI;

        String result = "";
        String[] columns = DBHelper.ALL_COLUMNS;
        Cursor cursor = getContentResolver().query(uri, columns, DBHelper.SCHEDULE_ID + " = " + ID, null, null);
        cursor.moveToNext();

        // 임시 데이터 표시용.
        int size = 4 + 1;// cursor.getInt(cursor.getColumnIndex(DBHelper.SCHEDULE_ACHIEVEMENT_INDEX)) + 1;

        String achievementData = null;
        if(size > 1) {
            achievementData = cursor.getString(cursor.getColumnIndex(DBHelper.SCHEDULE_ACHIEVEMENT_DATA)).substring(0, size);

            int count = 0;
            for(int i=0; i<size; ++i) {
                if(achievementData.charAt(i) == '1') {
                    count++;
                }
            }
            result += count + "/" + size;
        }

        return result;
    }

    private String checking() {
        String data = "0101001110";
        String result = "";
        int size = 5 + 1;
        int count = 0;
        for(int i=0; i<size; ++i) {
            if(data.charAt(i) == '1') {
                count++;
            }
        }
        result += count + "/" + size;

        return result;
    }
}
