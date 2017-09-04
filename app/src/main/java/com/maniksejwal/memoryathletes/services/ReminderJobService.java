package com.maniksejwal.memoryathletes.services;

import android.os.AsyncTask;
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
        Log.v(LOG_TAG, "onStartJob() started");
        mBackgroundTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                Log.v(LOG_TAG, "entered doInBackground()");
                NotificationUtils.createNotification(ReminderJobService.this);
                //ReminderTask.executeTask(context, ReminderTask.);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                Log.v(LOG_TAG, "entered onPostExecute()");
                jobFinished(jobParameters, false);
            }
        };
        mBackgroundTask.execute();
        return false;
    }

    @Override
    public boolean onStopJob(com.firebase.jobdispatcher.JobParameters job) {
        Log.v(LOG_TAG, "Entered onStopJob()");
        return false;
    }
}
