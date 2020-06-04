package com.example.androidterm2020;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class ShowDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        final int YEAR = intent.getIntExtra("Y", 0);
        final int MONTH = intent.getIntExtra("M", 0);
        final int DAY_OF_MONTH = intent.getIntExtra("D", 0);
        String date = YEAR  + "-" + (MONTH > 9 ? "" : "0") + (MONTH+1) + "-" + DAY_OF_MONTH;

        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams defaultParams1 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        LinearLayout.LayoutParams defaultParams2 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );

        // 블럭 생성 및 설정
        LinearLayout block1 = new LinearLayout(this); // 윗 공간, 스크롤뷰가 차지할 공간
        LinearLayout block2 = new LinearLayout(this); // 아래 공간, 일단 비워둠. 임시 버튼 1개
        block1.setOrientation(LinearLayout.VERTICAL);
        block2.setOrientation(LinearLayout.VERTICAL);
        block1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 500)); // 일부만 차지.

        ScrollView scrollView = new ScrollView(this);
        LinearLayout scheduleList = new LinearLayout(this);
        scheduleList.setOrientation(LinearLayout.VERTICAL);

        // 윗 공간을 차지할 버튼들, 스크롤 뷰에 들어감. // block1에 소속된 스크롤 뷰
        Button[] buttons = new Button[20];
        LinearLayout[] scheduleSet = new LinearLayout[20];

        LinearLayout.LayoutParams checkBoxParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
        LinearLayout.LayoutParams buttonsParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 3f);

        checkBoxParam.rightMargin = 5;
        buttonsParam.leftMargin = 10;
        buttonsParam.rightMargin = 10;

        Cursor cursor = getContentResolver().query(ScheduleProvider.CONTENT_URI, new String[]{DBHelper.SCHEDULE_START_DATE, DBHelper.SCHEDULE_TITLE, DBHelper.SCHEDULE_ID},
                "date(" + DBHelper.SCHEDULE_START_DATE + ") = ?", new String[] {date}, DBHelper.SCHEDULE_ID + " ASC");

        // 버튼들을 만든다.
        for(int i=0; i < cursor.getCount(); ++i) {
            cursor.moveToNext();
            scheduleSet[i] = new LinearLayout(this);
            buttons[i] = new Button(this);
            CheckBox checkBox = new CheckBox(this);
            checkBox.setId(i);

            scheduleSet[i].setOrientation(LinearLayout.HORIZONTAL); // checkbox랑 달성도로 이동하는 버튼


            buttons[i].setText(date + "    " + cursor.getString(cursor.getColumnIndex(DBHelper.SCHEDULE_TITLE)));
            buttons[i].setLayoutParams(defaultParams1);
            buttons[i].setLayoutParams(buttonsParam);
            checkBox.setLayoutParams(checkBoxParam);

            scheduleSet[i].addView(checkBox);
            scheduleSet[i].addView(buttons[i]);
            scheduleList.addView(scheduleSet[i]);
        }
        scrollView.addView(scheduleList);


        // 아랫 공간을 차지할 임시버튼.
        Button tempButton = new Button(this);
        tempButton.setText("임시 일정등록 버튼");
        tempButton.setLayoutParams(defaultParams1);

        LinearLayout settingButton = new LinearLayout(this);
        settingButton.setOrientation(LinearLayout.HORIZONTAL);
        settingButton.setLayoutParams(defaultParams1);
        settingButton.setPadding(130, 0, 0, 0);
        Button modifyButton = new Button(this);
        modifyButton.setText("수정버튼");
        modifyButton.setLayoutParams(defaultParams2);
        Button addButton = new Button(this);
        addButton.setText("새로운 일정 추가");
        defaultParams2.leftMargin = 20;
        addButton.setLayoutParams(defaultParams2);
        settingButton.addView(modifyButton);
        settingButton.addView(addButton);

        // checkbox 1개만 사용하는 기능 만들어야함.
        // 수정버튼 만들어야함. intent 넘겨줄 때 이것저것 넘겨주자.
        modifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ;
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ScheduleRegistrationActivity.class);
                intent.putExtra("Y", YEAR);
                intent.putExtra("M", MONTH);
                intent.putExtra("D", DAY_OF_MONTH);
                startActivity(intent);
            }
        });
        // 등록후 다시 화면을 초기화하고 다시 버튼 만들어야함.


        block1.addView(scrollView);
        block2.addView(settingButton);
        block2.setPadding(0, 100, 0, 0); // 내부의 뷰에 이격을 둠.

        // 2개의 레이아웃 부분을 등록. 윗화면 아래화면.
        container.addView(block1);
        container.addView(block2);

        // 화면을 띄운다.
        setContentView(container);
    }
}