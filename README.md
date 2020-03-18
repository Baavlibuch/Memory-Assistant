# README

### What is this repository for?
* **Quick summary** - Memory sports android app development. [Website's repository](https://github.com/sharadv245/Memory-Assistant)
* [Version](https://github.com/maniksejwal/Memory-Assistant/blob/master/app/build.gradle)

### I don't understand anything!
* Join the [Slack workspace](https://join.slack.com/t/memory-athlete-skynet/shared_invite/zt-5uq1mobq-i2UthsNyBXISWZHqkr2zZQ) and ask your questions.

### [Contribution guidelines](https://github.com/maniksejwal/Memory-Assistant/blob/master/CONTRIBUTING.md)

### Who do I talk to?
* Feel free to join the [Slack workspace](https://join.slack.com/t/memory-athlete-skynet/shared_invite/zt-5uq1mobq-i2UthsNyBXISWZHqkr2zZQ)
* eMail address – memoryassistantapp@gmail.com

### How do I get set up?
* **Summary of set up**
1. Install [Android Studio](https://developer.android.com/studio). 
2. [Download](https://github.com/maniksejwal/Memory-Assistant/archive/master.zip) and extract the repository or run the command `git clone https://github.com/maniksejwal/Memory-Assistant.git` on your computer.
3. In Android Studio, open the project as an existing android studio project after extracting the downloaded zip or import as a new project from git.
4. Create your own `final class Encryption.java` in `app/src/main/java/com.memory_athlete.memoryassistant/inAppBilling/`. Add `static` functions, `decrypt` and `addSomePepper` with return type `String`. You can return empty strings. 
5. Remove apk signing by removing `keystoreProperties` and `signingConfigs{...}` from app level `build.gradle`. To use your own signature, refer to the [documentation](https://developer.android.com/studio/publish/app-signing).
6. Add your own `google-services.json` to `app/src/`. Download it by linking your builds to Firebase. To run the app without it remove – all mentions to firebase from the app level `build.gradle`; `meta-data` tag from the `AndroidManifests.xml`. **Do NOT remove `firebase-jobdispatcher`** it is important for reminders.
7. Add your own `fabric.properties` to `app/src/`. Download it after connecting your builds to [Crashlytics](https://console.firebase.google.com/project/_/crashlytics) through Fabric. To run the app without it, run in debug or remove the crashlytics dependency and fabric repository and plugin from `build.gradle`; the `meta-data` tag from the `AndroidManifests.xml`; Crashlytics calls from all java files. 
8. Keep googling the errors that you face.

* **Dependencies**
1. Java 8
2. Kotlin
3. Android SDK
4. *Fabric* (Crashlytics)
5. *Firebase* (Indexing, AdMob, JobDispatcher) 
6. [Checkout](https://github.com/serso/android-checkout) 
7. [Picasso](https://github.com/square/picasso)
8. [ButterKnife](http://jakewharton.github.io/butterknife/)
9. [Timber](https://github.com/JakeWharton/timber)
10. MultiDex
11. AndroidX
12. JUnit
13. Espresso

14.  Refer to [`app/build.gradle`](https://github.com/maniksejwal/Memory-Assistant/blob/master/app/build.gradle) for details

### How to run tests
* Only some trivial Esspresso tests have been written, nothing serious. Pre Launch Report on Google Play Console is the only useful automated testing done. 
* For Running Espresso tests, go to the desired test in `D:\Projects\Skynet\MemoryAthlete\Android\app\src\androidTest\java\com\memory_athlete\memoryassistant`, right click and run the test on the device of your choice. USB debugging might be necessary.
* Write a more tests whenever you feel idle.

### Deployment instructions
1. On android studio `Build -> Build Bundles(s)/APKs -> Build Bundle/APK`
2. Upload it to [Google Play Console](https://play.google.com/apps/publish/)
