package com.example.androidterm2020.RoomDB;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ScheduleViewModel extends AndroidViewModel {
    private RoomDatabase mRoomDatabase;

    private LiveData<List<Schedule>> mAllSchedules;
    private LiveData<List<Schedule>> mDateSchedules;

    public ScheduleViewModel(Application application) {
        super(application);
        mRoomDatabase = RoomDatabaseAccessor.getInstance(application);
    }

    public int insertSchedule(Schedule schedule) {
        int id = -1;
        try {
            id = new insertScheduleAsyncTask(mRoomDatabase.scheduleDao()).execute(schedule).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return id;
    }

    public List<Schedule> getAllSchedules() {
        List<Schedule> schedules = null;
        try {
            schedules = new getAllScheduleAsyncTask(mRoomDatabase.scheduleDao()).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return schedules;
    }

    public LiveData<List<Schedule>> getAllSchedulesObserve() {
        LiveData<List<Schedule>> schedules = null;
        try {
            schedules = new getAllScheduleObserveAsyncTask(mRoomDatabase.scheduleDao()).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return schedules;
    }

    public List<Schedule> getDateSchedules(long date) {
        long today = (date/10000)*10000;
        List<Schedule> schedules = null;
        try {
            schedules = new getDateScheduleAsyncTask(mRoomDatabase.scheduleDao()).execute(today, today+2359).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return schedules;
    }

    public int getDateCountedSchedules(long date) {
        int count = 0;
        try {
            count = new getDateCountedScheduleAsyncTask(mRoomDatabase.scheduleDao()).execute(date).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return count;
    }

    public Schedule getScheduleById(int sid) {
        Schedule Schedule = null;
        try {
            Schedule = new getScheduleByIdAsyncTask(mRoomDatabase.scheduleDao()).execute(sid).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return Schedule;
    }

    public List<Schedule> getAllRepeatSchedule() {
        List<Schedule> Schedules = null;
        try {
            Schedules = new getAllRepeatScheduleAsyncTask(mRoomDatabase.scheduleDao()).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return Schedules;
    }

    public void updateSchedule(Schedule schedule) {
        try {
            new updateScheduleAsyncTask(mRoomDatabase.scheduleDao()).execute(schedule).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void deleteScheduleById(int sid) {
        try {
            new delScheduleByIdAsyncTask(mRoomDatabase.scheduleDao()).execute(sid).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public List<Schedule> getFromToSchedules(long[] fromTo) {
        List<Schedule> Schedules = null;
        try {
            Schedules = new getDateScheduleAsyncTask(mRoomDatabase.scheduleDao()).execute(fromTo[0], fromTo[1]).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return Schedules;
    }

    public List<Schedule> getFromToEndDateSchedules(long[] fromTo) {
        List<Schedule> Schedules = null;
        try {
            Schedules = new getFromToEndDateScheduleAsyncTask(mRoomDatabase.scheduleDao()).execute(fromTo[0], fromTo[1]).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return Schedules;
    }


    public static class getAllScheduleAsyncTask extends AsyncTask<Void, Void, List<Schedule>> {
        private ScheduleDao scheduleDao;

        public getAllScheduleAsyncTask(ScheduleDao scheduleDao) {
            this.scheduleDao = scheduleDao;
        }

        @Override
        protected List<Schedule> doInBackground(Void... voids) {
            return scheduleDao.getALLSchedules();
        }
    }

    public class getAllScheduleObserveAsyncTask extends AsyncTask<Void, Void, LiveData<List<Schedule>>> {
        private ScheduleDao scheduleDao;

        public getAllScheduleObserveAsyncTask(ScheduleDao scheduleDao) {
            this.scheduleDao = scheduleDao;
        }

        @Override
        protected LiveData<List<Schedule>> doInBackground(Void... voids) {
            return scheduleDao.getALLSchedulesObserve();
        }
    }


    public class insertScheduleAsyncTask extends AsyncTask<Schedule, Void, Integer> {
        private ScheduleDao scheduleDao;

        public insertScheduleAsyncTask(ScheduleDao scheduleDao) {
            this.scheduleDao = scheduleDao;
        }

        @Override
        protected Integer doInBackground(Schedule... schedules) {
            scheduleDao.insertSchedule(schedules[0]);
            return scheduleDao.getLastIdSchedule();
        }
    }


    public static class getDateScheduleAsyncTask extends AsyncTask<Long, Void, List<Schedule>> {
        private ScheduleDao scheduleDao;

        public getDateScheduleAsyncTask(ScheduleDao scheduleDao) {
            this.scheduleDao = scheduleDao;
        }

        @Override
        protected List<Schedule> doInBackground(Long... days) {
            List<Schedule> schedules = null;
            if(days != null && days.length > 0) {
                schedules = scheduleDao.getDateSchedules(days[0], days[1]); // days[0], days[1]
            }
            return schedules;
        }
    }

    public static class getFromToEndDateScheduleAsyncTask extends AsyncTask<Long, Void, List<Schedule>> {
        private ScheduleDao scheduleDao;

        public getFromToEndDateScheduleAsyncTask(ScheduleDao scheduleDao) {
            this.scheduleDao = scheduleDao;
        }

        @Override
        protected List<Schedule> doInBackground(Long... days) {
            List<Schedule> schedules = null;
            if(days != null && days.length > 0) {
                schedules = scheduleDao.getEndDateSchedules(days[0], days[1]); // days[0], days[1]
            }
            return schedules;
        }
    }

    public class getDateCountedScheduleAsyncTask extends  AsyncTask<Long, Void, Integer> {
        private ScheduleDao scheduleDao;

        public getDateCountedScheduleAsyncTask(ScheduleDao scheduleDao) {
            this.scheduleDao = scheduleDao;
        }

        @Override
        protected Integer doInBackground(Long... days) {
            long today = (days[0]/10000)*10000;
            int result = scheduleDao.getDateCountedSchedules(today, today + 2359);
            return result;
        }
    }

    public static class getScheduleByIdAsyncTask extends  AsyncTask<Integer, Void, Schedule> {
        private ScheduleDao scheduleDao;

        public getScheduleByIdAsyncTask(ScheduleDao scheduleDao) {
            this.scheduleDao = scheduleDao;
        }

        @Override
        protected Schedule doInBackground(Integer... sids) {
            Schedule result = scheduleDao.getScheduleById(sids[0]);
            return result;
        }
    }

    public static class updateScheduleAsyncTask extends AsyncTask<Schedule, Void, Void> {
        private ScheduleDao scheduleDao;

        public updateScheduleAsyncTask(ScheduleDao scheduleDao) {
            this.scheduleDao = scheduleDao;
        }

        @Override
        protected Void doInBackground(Schedule... schedules) {
            scheduleDao.updateSchedule(schedules[0]);
            return null;
        }
    }

    public static class delScheduleByIdAsyncTask extends  AsyncTask<Integer, Void, Void> {
        private ScheduleDao scheduleDao;

        public delScheduleByIdAsyncTask(ScheduleDao scheduleDao) {
            this.scheduleDao = scheduleDao;
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            scheduleDao.deleteScheduleById(integers[0]);
            return null;
        }
    }

    public class getAllRepeatScheduleAsyncTask extends AsyncTask<Void, Void, List<Schedule>> {
        private ScheduleDao scheduleDao;

        public getAllRepeatScheduleAsyncTask(ScheduleDao scheduleDao) {
            this.scheduleDao = scheduleDao;
        }

        @Override
        protected List<Schedule> doInBackground(Void... voids) {
            return scheduleDao.getALLRepeatSchedules();
        }
    }


}
