package com.memory_athlete.memoryassistant.services;

import android.os.AsyncTask;
import android.support.compat.BuildConfig;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;


/**
 * Created by Manik on 26/08/17.
 */

public class ReminderJobService extends JobService {
    private final String LOG_TAG = getClass().getSimpleName();
    private AsyncTask mBackgroundTask;

    @Override
    public boolean onStartJob(final JobParameters jobParameters) {
        if (BuildConfig.DEBUG) Log.v(LOG_TAG, "onStartJob() started");
        mBackgroundTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                if (BuildConfig.DEBUG) Log.v(LOG_TAG, "entered doInBackground()");
                NotificationUtils.createNotification(ReminderJobService.this);
                //ReminderTask.executeTask(context, ReminderTask.);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                if (BuildConfig.DEBUG) Log.v(LOG_TAG, "entered onPostExecute()");
                jobFinished(jobParameters, false);
            }
        };
        mBackgroundTask.execute();
        return false;
    }

    @Override
    public boolean onStopJob(com.firebase.jobdispatcher.JobParameters job) {
        if (BuildConfig.DEBUG) Log.v(LOG_TAG, "Entered onStopJob()");
        return false;
    }
}
