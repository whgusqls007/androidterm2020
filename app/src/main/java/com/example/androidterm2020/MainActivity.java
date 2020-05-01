//커밋테스트입니다
//클론테스트입니다
package com.example.androidterm2020;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button)findViewById(R.id.nextActivity);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ScheduleRegistrationActivity.class);
                intent.putExtra("message", "반갑습니다.");
                startActivity(intent);
            }
        });
    }
}
//마지막 커밋 테스트 입니다.