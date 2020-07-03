package com.example.androidterm2020.RoomDB;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.AndroidViewModel;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class AlarmViewModel extends AndroidViewModel {
    private RoomDatabase mRoomDatabase;

//    private LiveData<List<Schedule>> mAllSchedules;
//    private LiveData<List<Schedule>> mDateSchedules;

    public AlarmViewModel(Application application) {
        super(application);
        mRoomDatabase = RoomDatabaseAccessor.getInstance(application);
    }

    public int InsertAlarm(Alarm alarm) {
        int id = -1;
        try {
            id = new insertAlarmAsyncTask(mRoomDatabase.alarmDao()).execute(alarm).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return id;
    }

    public void deleteAlarmByScheduleId(int scheduleId) {
        try {
            new deleteAlarmByRequestIdAsyncTask(mRoomDatabase.alarmDao()).execute(scheduleId).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public List<Alarm> getAllAlarms() {
        List<Alarm> result = null;
        try {
            result = new getAllAlarmsAsyncTask(mRoomDatabase.alarmDao()).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return result;
    }

    public class insertAlarmAsyncTask extends AsyncTask<Alarm, Void, Integer> {
        private AlarmDao alarmDao;

        public insertAlarmAsyncTask(AlarmDao alarmDao) {
            this.alarmDao = alarmDao;
        }

        @Override
        protected Integer doInBackground(Alarm... alarms) {
            alarmDao.insertAlarm(alarms[0]);
            return alarmDao.getLastIdAlarm();
        }
    }

    public class deleteAlarmByRequestIdAsyncTask extends AsyncTask<Integer, Void, Void> {
        private AlarmDao alarmDao;

        public deleteAlarmByRequestIdAsyncTask(AlarmDao alarmDao) {
            this.alarmDao = alarmDao;
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            alarmDao.deleteAlarmByScheduleId(integers[0]);
            return null;
        }
    }

    public class getAllAlarmsAsyncTask extends AsyncTask<Void, Void, List<Alarm>> {
        private AlarmDao alarmDao;

        public getAllAlarmsAsyncTask(AlarmDao alarmDao) {
            this.alarmDao = alarmDao;
        }

        @Override
        protected List<Alarm> doInBackground(Void... voids) {
            alarmDao.getAllAlarms();
            return null;
        }
    }
}
