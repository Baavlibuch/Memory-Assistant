# Memory Assistant

## BaseActivity.java
This activity is created once at the time of app installation. The `onCreate` method creates a Spinner with different languages and the selected language is applied throughout the App. It also asks user for signing in through their **Google Account**. `MainActivity` is created after language selection.

## MainActivity.java
The `onCreateOptionsMenu` method creates a Menu bar and the `onOptionsItemSelected` method selects the option from the menu bar, if **Privacy Policy** is selected, it creates  `PrivacyPolicy` activity which renders the WebView for displaying the policy information.
`onRequestPermissionsResult()`  asks permission from user for accessing storage.
`onResume()` resumes the activity.
`onBackPressed()` closes the activity.
The `onCreate` method calls following methods -
`setAdapter()` - It sets the list of features the app has to provide.
`firstStart()` - It checks if the app is opened for the first time.
`verifyInstallerId()` - It checks where the app is installed from.
[Google AdMob](https://developers.google.com/admob) is used for displaying the Banner Ad.

## Learn.java
The `onCreate` method calls  `setAdapter()` which displays the list of all the learning techniques used for improving the memory.

## Practice.java
The `onCreate` method calls  `setAdapter()` which lists the different disciplines for practicing.

## MySpace.java
The `onStart`method calls `setAdapter` which lists the different sections for creating our own practice files.
The `add()` method creates `WriteFile` activity for creating a new file

## WriteFile.java
The `onCreateOptionsMenu` method creates a Menu bar and the `onOptionsItemSelected` method selects the option from the menu bar, if 'Delete' is selected, it creates  deletes the selected file and 'Exit without Saving' discards the file changes.
The `save()` method creates a new file and saves on the device and if signed in, stores on the **Firebase**. The saved file can also be edited.
The `search()` method searches the keyword inside the file. 
The `deleteFromFirebase()` method deletes the file stored on Firebase.


## Preferences.java
The `onCcreatePreferences()` method calls -
`bindPreferenceSummaryToValue()`  and `bindPreferenceToast()`- for showing the current value of the preference.
The `onPreferenceChange()` method is called when a preference has been changed by the user. This is called before the state of the Preference is about to be updated and before the state is persisted. Depending on the preference, the settings can be changed by the user.
The `onDisplayPreferenceDialog()` method creates a time picker dialog for setting the time.
The `myAlarm()` method is used for notifying the user everyday at selected time.
The `onStop()` method is called when the preference is changed and asks user to restart the app so that the changes are reflected.

## Contribute.java
The `onCreate` method calls  `setAdapter()` which lists the different ways of contributing to the app.

## ReminderUtils.java
It uses WorkManager to schedule and execute deferrable background work.
The `scheduleReminder()` method creates a one time WorkRequest with certain constraints and builds `MyWorkerForReminder` class which is the entry point of executing the work for reminder notifications.
The `mySpaceReminder()` method creates a one time WorkRequest with certain constraints and builds `MyWorkerForSpace` class which is the entry point of executing the work for myspace notifications.

## MyWorkerForReminder.java
It calls`NotificationUtils.createNotification()` which notifies the user when certain conditions are met.

## MyWorkerForSpace.java
It calls `NotificationUtils.createMySpaceNotification()` which notifies the user when a practice file is edited.

## NotificationUtils.java
The `createNotification()` method calls `text()` which provides the text to be displayed for notification.
The `createMySpaceNotification()` method calls `mySpcaetext()` which provides the text to be displayed for myspace files notification.
