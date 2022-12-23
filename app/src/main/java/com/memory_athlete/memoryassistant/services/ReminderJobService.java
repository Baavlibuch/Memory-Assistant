package com.memory_athlete.memoryassistant.services;

//import com.firebase.jobdispatcher.JobParameters;
//import com.firebase.jobdispatcher.JobService;

/**
 * Created by Manik on 26/08/17.
 */

//public class ReminderJobService extends JobService {
//
//    @Override
//    public boolean onStartJob(final JobParameters jobParameters) {
//        Timber.v("onStartJob() started");
//
//        //alternative to AsyncTask
//        ExecutorService executor = Executors.newSingleThreadExecutor();
//        Handler handler = new Handler(Looper.getMainLooper());
//
//        executor.execute(new Runnable() {
//            @Override
//            public void run() {
//                //Background work here
//                Timber.v("entered doInBackground()");
//                NotificationUtils.createNotification(ReminderJobService.this);
//
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        //UI Thread work here
//                        Timber.v("entered onPostExecute()");
//                        jobFinished(jobParameters, false);
//                    }
//                });
//            }
//        });
//        return false;
//    }
//
//    @Override
//    public boolean onStopJob(com.firebase.jobdispatcher.JobParameters job) {
//        Timber.v("Entered onStopJob()");
//        return false;
//    }
//
//}
