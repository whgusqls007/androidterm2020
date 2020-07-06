package com.example.androidterm2020.RoomDB;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.AndroidViewModel;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class AlarmViewModel extends AndroidViewModel {
    private RoomDatabase mRoomDatabase;


    public AlarmViewModel(Application application) {
        super(application);
        mRoomDatabase = RoomDatabaseAccessor.getInstance(application);
    }

    public int insertAlarm(Alarm alarm) {
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
            new deleteAlarmByScheduleIdAsyncTask(mRoomDatabase.alarmDao()).execute(scheduleId).get();
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

    public int getScheduleIdByAlarmId(int aid) {
        int result = -1;
        try {
            result = new getScheduleIdByAlarmIdAsyncTask(mRoomDatabase.alarmDao()).execute(aid).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return result;
    }

    public Alarm getAlarmByScheduleId(int scheduleId) {
        Alarm result = null;
        try {
            result = new getAlarmByScheduleIdAsyncTask(mRoomDatabase.alarmDao()).execute(scheduleId).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return result;
    }

    public int getAlarmIdByScheduleId(int scheduleId) {
        int result = -1;
        try {
            result = new getAlarmIdByScheduleIdAsyncTask(mRoomDatabase.alarmDao()).execute(scheduleId).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return result;
    }

    public void updateAlarm(Alarm alarm) {
        try {
            new updateAlarmAsyncTask(mRoomDatabase.alarmDao()).execute(alarm).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
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

    public static class deleteAlarmByScheduleIdAsyncTask extends AsyncTask<Integer, Void, Void> {
        private AlarmDao alarmDao;

        public deleteAlarmByScheduleIdAsyncTask(AlarmDao alarmDao) {
            this.alarmDao = alarmDao;
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            alarmDao.deleteAlarmByScheduleId(integers[0]);
            return null;
        }
    }

    public static class getAllAlarmsAsyncTask extends AsyncTask<Void, Void, List<Alarm>> {
        private AlarmDao alarmDao;

        public getAllAlarmsAsyncTask(AlarmDao alarmDao) {
            this.alarmDao = alarmDao;
        }

        @Override
        protected List<Alarm> doInBackground(Void... voids) {
            return alarmDao.getAllAlarms();
        }
    }

    public class getAlarmByScheduleIdAsyncTask extends AsyncTask<Integer, Void, Alarm> {
        private AlarmDao alarmDao;

        public getAlarmByScheduleIdAsyncTask(AlarmDao alarmDao) {
            this.alarmDao = alarmDao;
        }


        @Override
        protected Alarm doInBackground(Integer... integers) {
            return alarmDao.getAlarmByScheduleId(integers[0]);
        }
    }

    public class getAlarmIdByScheduleIdAsyncTask extends AsyncTask<Integer, Void, Integer> {
        private AlarmDao alarmDao;

        public getAlarmIdByScheduleIdAsyncTask(AlarmDao alarmDao) {
            this.alarmDao = alarmDao;
        }


        @Override
        protected Integer doInBackground(Integer... integers) {
            return alarmDao.getAlarmIdByScheduleId(integers[0]);
        }
    }

    public class updateAlarmAsyncTask extends AsyncTask<Alarm, Void, Void> {
        private AlarmDao alarmDao;

        public updateAlarmAsyncTask(AlarmDao alarmDao) {
            this.alarmDao = alarmDao;
        }


        @Override
        protected Void doInBackground(Alarm... alarms) {
            alarmDao.updateAlarm(alarms[0]);
            return null;
        }
    }

    public static class getScheduleIdByAlarmIdAsyncTask extends AsyncTask<Integer, Void, Integer> {
        private AlarmDao alarmDao;

        public getScheduleIdByAlarmIdAsyncTask(AlarmDao alarmDao) {
            this.alarmDao = alarmDao;
        }

        @Override
        protected Integer doInBackground(Integer... integers) {
            return alarmDao.getScheduleIdByAlarmId(integers[0]);
        }
    }
}
