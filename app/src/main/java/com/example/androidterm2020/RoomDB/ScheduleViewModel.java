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
        List<Schedule> schedules = null;
        try {
            schedules = new getDateScheduleAsyncTask(mRoomDatabase.scheduleDao()).execute(date).get();
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


    public class getAllScheduleAsyncTask extends AsyncTask<Void, Void, List<Schedule>> {
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
            long today = (days[0]/10000)*10000;
            List<Schedule> schedules = null;
            if(days != null && days.length > 0) {
                schedules = scheduleDao.getDateSchedules(today, today + 2359);
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
            int result = scheduleDao.getDateCountedSchedules(today, today+2359);
            return result;
        }
    }

    public class getScheduleByIdAsyncTask extends  AsyncTask<Integer, Void, Schedule> {
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

    public class updateScheduleAsyncTask extends AsyncTask<Schedule, Void, Void> {
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

    public class delScheduleByIdAsyncTask extends  AsyncTask<Integer, Void, Void> {
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


}
