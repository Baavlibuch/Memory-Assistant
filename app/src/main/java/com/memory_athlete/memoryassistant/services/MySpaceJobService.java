package com.memory_athlete.memoryassistant.services;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.compat.BuildConfig;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

public class MySpaceJobService extends JobService {
    private final String LOG_TAG = getClass().getSimpleName();
    private AsyncTask mBackgroundTask;

    @Override
    public boolean onStartJob(final JobParameters job) {
        if (BuildConfig.DEBUG) Log.v(LOG_TAG, "onStartJob() started");
        mBackgroundTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                if (BuildConfig.DEBUG) Log.v(LOG_TAG, "entered doInBackground()");
                Bundle bundle = job.getExtras();
                if(bundle!=null) {
                    String fname = bundle.getString("fname");
                    NotificationUtils.createMySpaceNotification(MySpaceJobService.this, fname);
                    //ReminderTask.executeTask(context, ReminderTask.);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                if (BuildConfig.DEBUG) Log.v(LOG_TAG, "entered onPostExecute()");
                jobFinished(job, false);
            }
        };
        mBackgroundTask.execute();
        return false;    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false;
    }


}
