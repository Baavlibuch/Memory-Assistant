package com.memory_athlete.memoryassistant.services;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.memory_athlete.memoryassistant.language.LocaleHelper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import timber.log.Timber;

/**
 * Created by Manik on 26/08/17.
 */

public class ReminderJobService extends JobService {

    @Override
    public boolean onStartJob(final JobParameters jobParameters) {
        Timber.v("onStartJob() started");
//        AsyncTask backgroundTask = new AsyncTask() {
//            @Override
//            protected Object doInBackground(Object[] objects) {
//                Timber.v("entered doInBackground()");
//                NotificationUtils.createNotification(ReminderJobService.this);
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Object o) {
//                Timber.v("entered onPostExecute()");
//                jobFinished(jobParameters, false);
//            }
//        };
//        backgroundTask.execute();

        //alternative to AsyncTask
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(new Runnable() {
            @Override
            public void run() {
                //Background work here
                Timber.v("entered doInBackground()");
                NotificationUtils.createNotification(ReminderJobService.this);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //UI Thread work here
                        Timber.v("entered onPostExecute()");
                        jobFinished(jobParameters, false);
                    }
                });
            }
        });
        return false;
    }

    @Override
    public boolean onStopJob(com.firebase.jobdispatcher.JobParameters job) {
        Timber.v("Entered onStopJob()");
        return false;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base, "en"));
    }
}
