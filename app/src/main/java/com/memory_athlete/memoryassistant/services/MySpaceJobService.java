package com.memory_athlete.memoryassistant.services;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.memory_athlete.memoryassistant.language.LocaleHelper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import timber.log.Timber;

public class MySpaceJobService extends JobService {
    //private AsyncTask mBackgroundTask;

    @Override
    public boolean onStartJob(final JobParameters job) {
        Timber.v("onStartJob() started");
//        mBackgroundTask = new AsyncTask() {
//            @Override
//            protected Object doInBackground(Object[] objects) {
//                Timber.v("entered doInBackground()");
//                Bundle bundle = job.getExtras();
//                if (bundle != null) {
//                    String fPath = bundle.getString("fPath");
//                    NotificationUtils.createMySpaceNotification(MySpaceJobService.this, fPath);
//                    //ReminderTask.executeTask(context, ReminderTask.);
//                }
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Object o) {
//                Timber.v("entered onPostExecute()");
//                jobFinished(job, false);
//            }
//        };
//        mBackgroundTask.execute();

        //alternative to AsyncTask
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(new Runnable() {
            @Override
            public void run() {
                //Background work here
                Timber.v("entered doInBackground()");

                Bundle bundle = job.getExtras();
                if (bundle != null) {
                    String fPath = bundle.getString("fPath");
                    NotificationUtils.createMySpaceNotification(MySpaceJobService.this, fPath);
                    //ReminderTask.executeTask(context, ReminderTask.);
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //UI Thread work here
                        Timber.v("entered onPostExecute()");
                        jobFinished(job, false);
                    }
                });
            }
        });

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base, "en"));
    }
}
