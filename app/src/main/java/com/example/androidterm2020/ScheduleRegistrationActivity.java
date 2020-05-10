package com.example.androidterm2020;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.time.Year;

public class ScheduleRegistrationActivity extends AppCompatActivity {
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_registeration_activity);
        Intent intent = getIntent();
        int year1 = intent.getIntExtra("Y", 0);
        int month1 = intent.getIntExtra("M", 0);
        int date1 = intent.getIntExtra("D", 0);
        TextView textView = (TextView) findViewById(R.id.editText3);
        TextView textView2 = (TextView) findViewById(R.id.editText4);
        textView.setText(year1 + "/" + (month1 + 1) + "/" + date1);
        textView2.setText(year1 + "/" + (month1 + 1) + "/" + date1);
        Button button = (Button) findViewById(R.id.aa);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(getApplicationContext(), PickDActivity.class);
                startActivityForResult(intent2, 101);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 101){
            int year2 = data.getIntExtra("Y2", 0);
            int month2 = data.getIntExtra("M2", 0);
            int date2 = data.getIntExtra("D2", 0);
            TextView textView = (TextView)findViewById(R.id.editText4);
            textView.setText(year2 + "/" + (month2 + 1) + "/" + date2);
        }
    }
}
