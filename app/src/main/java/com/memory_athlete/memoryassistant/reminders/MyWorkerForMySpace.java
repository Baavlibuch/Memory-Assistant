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

public class MyWorkerForMySpace extends Worker {

    public MyWorkerForMySpace(Context appContext, WorkerParameters params) {
        super(appContext, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Do your work here.
        Data input = getInputData();
        String path = input.getString("fpath");

//        Bundle bundle = new Bundle();
//        bundle.putString("fPath", path);

        // Return a ListenableWorker.Result
        Data outputData = new Data.Builder()
                .putString("fpath", path)
                .build();


        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(new Runnable() {
            @Override
            public void run() {
                //Background work here
                Timber.v("entered doInBackground()");

//                Bundle bundle = job.getExtras();
//                if (bundle != null) {
                //String fPath = bundle.getString("fPath");
                if(path!=null) {
                    NotificationUtils.createMySpaceNotification(getApplicationContext(), path);
                }
                //ReminderTask.executeTask(context, ReminderTask.);
                //}

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //UI Thread work here
                        Timber.v("entered onPostExecute()");
                        //jobFinished(job, false);
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



