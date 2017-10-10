package com.memory_athlete.memoryassistant.services;

import android.os.AsyncTask;
import android.os.Bundle;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import timber.log.Timber;

public class MySpaceJobService extends JobService {
    private AsyncTask mBackgroundTask;

    @Override
    public boolean onStartJob(final JobParameters job) {
        Timber.v("onStartJob() started");
        mBackgroundTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                Timber.v("entered doInBackground()");
                Bundle bundle = job.getExtras();
                if (bundle != null) {
                    String fPath = bundle.getString("fPath");
                    NotificationUtils.createMySpaceNotification(MySpaceJobService.this, fPath);
                    //ReminderTask.executeTask(context, ReminderTask.);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                Timber.v("entered onPostExecute()");
                jobFinished(job, false);
            }
        };
        mBackgroundTask.execute();
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false;
    }
}
