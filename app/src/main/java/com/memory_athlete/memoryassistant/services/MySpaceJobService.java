package com.memory_athlete.memoryassistant.services;

//import com.firebase.jobdispatcher.JobParameters;
//import com.firebase.jobdispatcher.JobService;

//public class MySpaceJobService extends JobService {
//    //private AsyncTask mBackgroundTask;
//
//    @Override
//    public boolean onStartJob(final JobParameters job) {
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
//
//                Bundle bundle = job.getExtras();
//                if (bundle != null) {
//                    String fPath = bundle.getString("fPath");
//                    NotificationUtils.createMySpaceNotification(MySpaceJobService.this, fPath);
//                    //ReminderTask.executeTask(context, ReminderTask.);
//                }
//
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        //UI Thread work here
//                        Timber.v("entered onPostExecute()");
//                        jobFinished(job, false);
//                    }
//                });
//            }
//        });
//
//        return false;
//    }
//
//    @Override
//    public boolean onStopJob(JobParameters job) {
//        return false;
//    }
//
//}
