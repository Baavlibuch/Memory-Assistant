package com.memory_athlete.memoryassistant.reminders;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.memory_athlete.memoryassistant.services.NotificationUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import timber.log.Timber;

public class MyWorkerForReminder extends Worker {

    public MyWorkerForReminder( Context appContext,  WorkerParameters params) {
        super(appContext, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Do your work here.
        Data input = getInputData();
        String r = input.getString("rem");

//        Bundle bundle = new Bundle();
//        bundle.putString("fPath", path);

        // Return a ListenableWorker.Result
        Data outputData = new Data.Builder()
                .putString("rem", r)
                .build();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(new Runnable() {
            @Override
            public void run() {
                //Background work here
                Timber.v("entered doInBackground()");
                if(r!=null) {
                    NotificationUtils.createNotification(getApplicationContext());
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //UI Thread work here
                        Timber.v("entered onPostExecute()");
                        //jobFinished(jobParameters, false);
                    }
                });
            }
        });

        return Result.success(outputData);
    }

    @Override
    public void onStopped() {
        // Cleanup because you are being stopped.

    }

}

