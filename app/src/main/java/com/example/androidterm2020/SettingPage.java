package com.example.androidterm2020;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.widget.BaseAdapter;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.prefs.Preferences;

public class SettingPage extends PreferenceFragment {

    SharedPreferences prefs;

    PreferenceScreen NotifyView;
    ListPreference NotifySelct;
    ListPreference NotifyTime;
    SettingPage context = this;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.setting_page);
        NotifyView = (PreferenceScreen)findPreference("notify");
        NotifySelct = (ListPreference)findPreference("notify_select");
        NotifyTime = (ListPreference)findPreference("notify_time");;
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if(prefs.getBoolean("notify_on_off", true)){
            NotifyView.setSummary("사용");
            SettingInfo.Noti_on_off = true;
        }

        if(prefs.getString("notify_select", "All").equals("All")){
            NotifySelct.setSummary(prefs.getString("notify_select", "All"));
            SettingInfo.Noti_kind = 3;
        }
        else if(prefs.getString("notify_select", "일정").equals("일정")){
            NotifySelct.setSummary(prefs.getString("notify_select", "일정"));
            SettingInfo.Noti_kind = 1;
        }
        else if(prefs.getString("notify_select", "기상 정보").equals("기상 정보")){
            NotifySelct.setSummary(prefs.getString("notify_select", "기상 정보"));
            SettingInfo.Noti_kind = 2;
        }

        if(prefs.getString("notify_time", "1시간 전").equals("1시간 전")){
            NotifyTime.setSummary(prefs.getString("notify_time", "1시간 전"));
            SettingInfo.Noti_time = 1;
        }
        else if(prefs.getString("notify_time", "2시간 전").equals("2시간 전")){
            NotifyTime.setSummary(prefs.getString("notify_time", "2시간 전"));
            SettingInfo.Noti_time = 2;
        }
        else if(prefs.getString("notify_time", "3시간 전").equals("3시간 전")){
            NotifyTime.setSummary(prefs.getString("notify_time", "3시간 전"));
            SettingInfo.Noti_time = 3;
        }

        prefs.registerOnSharedPreferenceChangeListener(prefListener);
    }

    SharedPreferences.OnSharedPreferenceChangeListener prefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {

        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if(key.equals("notify_on_off")){
                if(prefs.getBoolean("notify_on_off", false)){
                    NotifyView.setSummary("사용");
                    NotifyView.setDefaultValue(true);
                    SettingInfo.Noti_on_off = true;
                }
                else{
                    NotifyView.setSummary("사용 안함");
                    NotifyView.setDefaultValue(false);
                    SettingInfo.Noti_on_off = false;
                }
                ((BaseAdapter)getPreferenceScreen().getRootAdapter()).notifyDataSetChanged();
            }

            if(key.equals("notify_select")){
                if(prefs.getString("notify_select", "All").equals("All")){
                    NotifySelct.setSummary("All");
                    NotifySelct.setDefaultValue("All");
                    SettingInfo.Noti_kind = 3;
                }
                else if(prefs.getString("notify_select", "일정").equals("일정")){
                    NotifySelct.setSummary("일정");
                    NotifySelct.setDefaultValue("일정");
                    SettingInfo.Noti_kind = 1;
                }
                else{
                    NotifySelct.setSummary("기상 정보");
                    NotifySelct.setDefaultValue("기상 정보");
                    SettingInfo.Noti_kind = 2;
                }
                ((BaseAdapter)getPreferenceScreen().getRootAdapter()).notifyDataSetChanged();
            }

            if(key.equals("notify_time")){
                if(prefs.getString("notify_time", "1시간 전").equals("1시간 전")){
                    NotifyTime.setSummary("1시간 전");
                    NotifyTime.setDefaultValue("1시간 전");
                    SettingInfo.Noti_time = 1;
                }
                else if(prefs.getString("notify_time", "2시간 전").equals("2시간 전")){
                    NotifyTime.setSummary("2시간 전");
                    NotifyTime.setDefaultValue("2시간 전");
                    SettingInfo.Noti_time = 2;
                }
                else{
                    NotifyTime.setSummary("3시간 전");
                    NotifyTime.setDefaultValue("3시간 전");
                    SettingInfo.Noti_time = 3;
                }
                ((BaseAdapter)getPreferenceScreen().getRootAdapter()).notifyDataSetChanged();
            }
        }
    };
}
