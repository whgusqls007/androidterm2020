package com.example.androidterm2020;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.androidterm2020.Fragments.AchieveAllListFragment;
import com.example.androidterm2020.Fragments.DeleteAllSchedulesFragment;
import com.example.androidterm2020.Fragments.DeleteSchedulesFragment;
import com.example.androidterm2020.Fragments.ScheduleAllModificationFragment;
import com.example.androidterm2020.Receivers.Alarm_Receiver;
import com.example.androidterm2020.RoomDB.ScheduleViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

public class ShowAllSchedule extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, FragmentCallback {

    Toolbar myToolbar;
    DrawerLayout drawer;

    AchieveAllListFragment achieveAllListFragment;
    ScheduleAllModificationFragment scheduleAllModificationFragment;
    DeleteAllSchedulesFragment deleteAllSchedulesFragment;

    ScheduleViewModel scheduleViewModel;
    String date;
    FloatingActionButton floatingActionButton;

    final static int REGISTRATION_REQUEST_CODE = 111;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_detail);

        init();


        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, myToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        final Bundle bundle = new Bundle();
        bundle.putString("date", date); // 번들을 잘 넘김.

        scheduleViewModel = new ViewModelProvider(this).get(ScheduleViewModel.class);

        myToolbar.setTitle("달성도 확인");
        getSupportFragmentManager().beginTransaction().add(R.id.container, AchieveAllListFragment.class, bundle).commit();

    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) { // 사이드 매뉴에서 선택 되면.
        int id = item.getItemId();
        Bundle bundle = new Bundle();
        bundle.putString("date", date);

        if(id == R.id.achieveList) {
            Toast.makeText(this, "첫 번째 메뉴 선택됨.", Toast.LENGTH_LONG).show();
            onFragmentSelected(0, bundle);
            floatingActionButton.setImageDrawable(getResources().getDrawable(R.drawable.plus));
        }
        else if(id == R.id.scheduleModification) {
            Toast.makeText(this, "두 번째 메뉴 선택됨.", Toast.LENGTH_LONG).show();
            onFragmentSelected(1, bundle);
            floatingActionButton.setImageDrawable(getResources().getDrawable(R.drawable.plus));
        }
        else if (id == R.id.deleteSchedules) {
            Toast.makeText(this, "세 번째 메뉴 선택됨.", Toast.LENGTH_LONG).show();
            onFragmentSelected(2, bundle);
            floatingActionButton.setImageDrawable(getResources().getDrawable(R.drawable.delete));
        }

        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onFragmentSelected(int position, Bundle bundle) { // 여기로 와서 화면을 바꾼다.
        Fragment curFragment = null;
        String tag = null;
        if(position == 0) {
            curFragment = achieveAllListFragment;
            myToolbar.setTitle("달성도");
            tag = "achieve";
        }
        else if(position == 1) {
            curFragment = scheduleAllModificationFragment;
            myToolbar.setTitle("일정 수정");
            tag = "mofify";
        }
        else if(position == 2) {
            curFragment = deleteAllSchedulesFragment;
            myToolbar.setTitle("일정 삭제");
            tag = "delete";
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.container, curFragment,  tag).commit();
    }


    private void init() {
        initToolbar();
        initFragment();
        initViews();
    }

    private void initViews() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        TextView title = (TextView) headerView.findViewById(R.id.Title);
        TextView time = (TextView) headerView.findViewById(R.id.todayDatetime);

        title.setText("전체 일정");
        time.setHint("");

        floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setImageDrawable(getResources().getDrawable(R.drawable.plus));
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
                if(fragment instanceof DeleteSchedulesFragment && ((DeleteSchedulesFragment) fragment).getScheduleNum() > 0) {
                    Toast.makeText(getApplicationContext(), "일정삭제", Toast.LENGTH_SHORT).show();
                    DeleteSchedulesFragment delFrag = (DeleteSchedulesFragment)getSupportFragmentManager().findFragmentByTag("delete");
                    delFrag.deleteSchedules();
                    Bundle bundle = new Bundle();
                    bundle.putString("date", date);
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, delFrag.getClass(), bundle, delFrag.getTag()).commit();
                }
                else {
                    Toast.makeText(getApplicationContext(), "일정등록", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), ScheduleRegistrationActivity.class);
                    intent.putExtra("date", getIntent().getStringExtra("date"));
                    startActivityForResult(intent, REGISTRATION_REQUEST_CODE); // 화면이 자동으로 갱신되는가?
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REGISTRATION_REQUEST_CODE) {
            //해당 fragement의 화면 초기화.
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
            getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment,  fragment.getTag()).commit(); // 프래그먼트 재시작.
        }
    }


    private void initToolbar() {
        myToolbar = (Toolbar)findViewById(R.id.toolbar);
        myToolbar.setTitle("");
        setSupportActionBar(myToolbar);
    }

    private void initFragment() {
        achieveAllListFragment = new AchieveAllListFragment();
        scheduleAllModificationFragment = new ScheduleAllModificationFragment();
        deleteAllSchedulesFragment = new DeleteAllSchedulesFragment();
    }

    private long getLongDate(String date) {
        date = date.replaceAll("[ :-]", "");
        return Long.parseLong(date);
    }


    public void unregist(int deleteCode) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, Alarm_Receiver.class);
        PendingIntent pIntent = PendingIntent.getBroadcast(this, deleteCode, intent, 0);
        if(alarmManager != null) {
            alarmManager.cancel(pIntent);
        }
        SharedPreferences preference = getPreferences(this);
        SharedPreferences.Editor editor = preference.edit();
        editor.remove(Integer.toString(deleteCode));
        editor.apply();
    }

    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences("Alarm", Context.MODE_PRIVATE);
    }

    public void setFloatingActionButtonImgPlus() {
        floatingActionButton.setImageDrawable(getResources().getDrawable(R.drawable.plus));
    }
}