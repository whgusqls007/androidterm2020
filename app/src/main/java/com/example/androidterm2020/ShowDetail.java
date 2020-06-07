package com.example.androidterm2020;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import java.util.ArrayList;


public class ShowDetail extends AppCompatActivity {
    String date;
    ScheduleProvider scheduleProvider = new ScheduleProvider();

    LinearLayout.LayoutParams defaultParams1 = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
    );
    LinearLayout.LayoutParams defaultParams2 = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.MATCH_PARENT
    );

    LinearLayout.LayoutParams checkBoxParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
    LinearLayout.LayoutParams buttonsParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 3f);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        date = intent.getStringExtra("date");

        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);

        // 블럭 생성 및 설정
        LinearLayout block1 = new LinearLayout(this); // 윗 공간, 스크롤뷰가 차지할 공간
        LinearLayout block2 = new LinearLayout(this); // 아래 공간, 일단 비워둠. 임시 버튼 1개
        block1.setOrientation(LinearLayout.VERTICAL);
        block2.setOrientation(LinearLayout.VERTICAL);
        block1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 500)); // 일부만 차지.

        final ScrollView scrollView = new ScrollView(this);

        // 윗 공간을 차지할 버튼들, 스크롤 뷰에 들어감. // block1에 소속된 스크롤 뷰

        checkBoxParam.rightMargin = 5;
        buttonsParam.leftMargin = 10;
        buttonsParam.rightMargin = 10;

        final ArrayList<CheckBox> checkBoxes = new ArrayList<CheckBox>();
        final ArrayList<Button> buttons = new ArrayList<Button>();
        ArrayList<LinearLayout> scheduleSet = new ArrayList<LinearLayout>();

        scrollView.addView(createScheduleList(checkBoxes, buttons, scheduleSet));

        // 아랫 공간을 차지할 임시버튼.
        Button tempButton = new Button(this);
        tempButton.setText("임시 일정등록 버튼");
        tempButton.setLayoutParams(defaultParams1);

        LinearLayout settingButton = new LinearLayout(this);
        settingButton.setOrientation(LinearLayout.HORIZONTAL);
        settingButton.setLayoutParams(defaultParams1);
        settingButton.setPadding(130, 0, 0, 0);

        // 수정버튼
        Button modifyButton = new Button(this);
        modifyButton.setText("수정버튼");
        modifyButton.setLayoutParams(defaultParams2);

        // 일정 추가 버튼
        Button addButton = new Button(this);
        addButton.setText("새로운 일정 추가");
        //defaultParams2.leftMargin = 20;
        addButton.setLayoutParams(defaultParams2);

        // 삭제버튼
        Button deleteButton = new Button(this);
        deleteButton.setText("삭제 버튼");
        addButton.setLayoutParams(defaultParams2);

        settingButton.addView(modifyButton);
        settingButton.addView(addButton);
        settingButton.addView(deleteButton);

        // checkbox 1개만 사용하는 기능 만들어야함.
        // 수정버튼 만들어야함. intent 넘겨줄 때 이것저것 넘겨주자.  checkbox 1개만 check되어있는지 확인해주기.
        modifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                1. checkbox가 check된 것 찾기.
                2. 해당하는 일정을 찾기위한 data(시작일과 title)를 가져오기.
                3. 해당하는 일정의
                4. intent로 넘기기.
                5. intent 실행하기.
                 */
                String date = null;
                String title = null;
                int ID = 0;
                int index = -1;
                for(CheckBox cb: checkBoxes) {
                    if (cb.isChecked() == true) {
                        index = cb.getId();
                        break;
                    }
                }

                if( index != -1 ) { // 찾았다면.
                    String[] data = buttons.get(index).getText().toString().split(" ");
                    date = data[0];
                    title = data[1];
                    String selection = "date(" + DBHelper.SCHEDULE_START_DATE + ") = ? and " + DBHelper.SCHEDULE_TITLE + " = ?";

                    Cursor cursor = getContentResolver().query(ScheduleProvider.CONTENT_URI, new String[] {DBHelper.SCHEDULE_ID}, selection, new String[] {date, title}, DBHelper.SCHEDULE_ID + " ASC");
                    if (cursor.getCount() > 0) {
                        cursor.moveToNext();
                        ID = cursor.getInt(0);
                    }

                    Intent intent = new Intent(getApplicationContext(), ScheduleUpdateActivity.class);
                    intent.putExtra("ID", ID);
                    startActivityForResult(intent, 200);

                }
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean is_updated = false;
                Intent intent = new Intent(getApplicationContext(), ScheduleRegistrationActivity.class);
                intent.putExtra("date", date);
                startActivityForResult(intent, 200);
            }
        });

        // 여러개 지울수 있도록 개선해야함. 완료.
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = null;
                String title = null;
                int ID = 0;
                ArrayList<Integer> indexList = new ArrayList<Integer>();
                for(CheckBox cb: checkBoxes) {
                    if (cb.isChecked() == true) {
                        indexList.add(cb.getId());
                    }
                }

                if( indexList.size() > 0 ) { // 찾았다면.
                    for(int id : indexList) {
                        String[] data = buttons.get(id).getText().toString().split(" ");
                        date = data[0];
                        title = data[1];
                        String selection = "date(" + DBHelper.SCHEDULE_START_DATE + ") = ? and " + DBHelper.SCHEDULE_TITLE + " = ?";

                        Cursor cursor = getContentResolver().query(ScheduleProvider.CONTENT_URI, new String[]{DBHelper.SCHEDULE_ID}, selection, new String[]{date, title}, DBHelper.SCHEDULE_ID + " ASC");
                        if (cursor.getCount() > 0) {
                            cursor.moveToNext();
                            ID = cursor.getInt(0);
                        }

                        deleteSchedule(ID);
                    }
                }
            }
        });


        block1.addView(scrollView);
        block2.addView(settingButton);
        block2.setPadding(0, 100, 0, 0); // 내부의 뷰에 이격을 둠.

        // 2개의 레이아웃 부분을 등록. 윗화면 아래화면.
        container.addView(block1);
        container.addView(block2);

        // 화면을 띄운다.
        setContentView(container);
    }

    public void querySchedule() {
        try {
            Uri uri = ScheduleProvider.CONTENT_URI;

            String[] columns = DBHelper.ALL_COLUMNS;
            Cursor cursor = getContentResolver().query(uri, columns, null, null, DBHelper.SCHEDULE_START_DATE + " ASC");
            // println("query 결과 : " + cursor.getCount());

            int index = 0;
            while(cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(columns[0]));
                String title = cursor.getString(cursor.getColumnIndex(columns[1]));
                String strDate = cursor.getString(cursor.getColumnIndex(columns[2]));
                String endDate = cursor.getString(cursor.getColumnIndex(columns[3]));
                String details = cursor.getString(cursor.getColumnIndex(columns[4]));
                int period = cursor.getInt(cursor.getColumnIndex(columns[5]));
                int dateNum = cursor.getInt(cursor.getColumnIndex(columns[6]));
                int achIndex = cursor.getInt(cursor.getColumnIndex(columns[7]));
                String achData = cursor.getString(cursor.getColumnIndex(columns[8]));

                index += 1;
            }

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void restartScreen() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    private LinearLayout createScheduleList(ArrayList<CheckBox> checkBoxes, final ArrayList<Button> buttons, ArrayList<LinearLayout> scheduleSet) {
        // 하나의 공간을 만듬.
        LinearLayout scheduleList = new LinearLayout(this);
        scheduleList.setOrientation(LinearLayout.VERTICAL);

        Cursor cursor = getContentResolver().query(ScheduleProvider.CONTENT_URI, new String[]{DBHelper.SCHEDULE_START_DATE, DBHelper.SCHEDULE_TITLE, DBHelper.SCHEDULE_ID},
                "date(" + DBHelper.SCHEDULE_START_DATE + ") = ?", new String[] {date}, DBHelper.SCHEDULE_ID + " ASC");

        // 버튼들을 만든다.
        for(int i=0; i < cursor.getCount(); ++i) {
            cursor.moveToNext();
            scheduleSet.add(new LinearLayout(this));
            buttons.add(new Button(this));
            checkBoxes.add(new CheckBox(this));

            buttons.get(i).setId(i);
            checkBoxes.get(i).setId(i);

            scheduleSet.get(i).setOrientation(LinearLayout.HORIZONTAL); // checkbox랑 달성도로 이동하는 버튼

            buttons.get(i).setText(date + " " + cursor.getString(cursor.getColumnIndex(DBHelper.SCHEDULE_TITLE)));
            buttons.get(i).setLayoutParams(defaultParams1);
            buttons.get(i).setLayoutParams(buttonsParam);
            checkBoxes.get(i).setLayoutParams(checkBoxParam);
            buttons.get(i).getText().toString();

            scheduleSet.get(i).addView(checkBoxes.get(i));
            scheduleSet.get(i).addView(buttons.get(i));
            scheduleList.addView(scheduleSet.get(i));

            buttons.get(i).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    String[] data = buttons.get(0).getText().toString().split(" ");
                    String date = data[0];
                    String title = data[1];
                    int ID = 0;
                    String selection = "date(" + DBHelper.SCHEDULE_START_DATE + ") = ? and " + DBHelper.SCHEDULE_TITLE + " = ?";
                    Cursor cursor = getContentResolver().query(ScheduleProvider.CONTENT_URI, new String[] {DBHelper.SCHEDULE_ID}, selection, new String[] {date, title}, DBHelper.SCHEDULE_ID + " ASC");
                    if (cursor.getCount() > 0) {
                        cursor.moveToNext();
                        ID = cursor.getInt(0);
                        Intent intent = new Intent(getApplicationContext(), Achieve.class);
                        intent.putExtra("ID", ID);
                        startActivity(intent);
                    }
                }
            });
        }

        return scheduleList;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 200){
            finish();
            Intent intent = getIntent();
            startActivity(intent);
        }
    }

    private void deleteSchedule(final int ID) {
        Uri uri = ScheduleProvider.CONTENT_URI;

        String selection = DBHelper.SCHEDULE_ID + " = " + ID;

        int count = getContentResolver().delete(uri, selection, null);
        if (count > 0) {
            restartScreen();
        }
    }
}