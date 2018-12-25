# README #

This README would normally document whatever steps are necessary to get your application up and running.

### What is this repository for? ###

* Quick summary-
Memory sports android app
* Version
* [Learn Markdown](https://bitbucket.org/tutorials/markdowndemo)

### How do I get set up? ###

* **Summary of set up** 
1. Use android studio. 
2. Import new project from git or open the project as an existing android studio project. 
3. Create your own `final class Encryption.java` in `app/src/main/java/com.memory_athlete.memoryassistant/inAppBilling/`. Add `static` functions, `decrypt` and `addSomePepper` with return type `String`. You can return empty strings. 
4. Remove apk signing by removing `keystoreProperties` and `signingConfigs{...}` from app level `build.gradle`. To use your own signarure, refer to the [documentation](https://developer.android.com/studio/publish/app-signing).
5. Add your own `google-services.json` to `app/src/`. Download it by linking your builds to Firebase. To run the app without it, remove all mentions to firebase from the app level `build.gradle`. Remove the code for ads from `Mainactivity.firststart()`. Remove the `meta-data` tag from the `AndroidManifests.xml`. **Do NOT remove `firebase-jobdispatcher`**!
6. Add your own `fabric.properties` to `app/src/`. Download it after connecting your builds to Crashlytics through Fabric. To run the app without it, remove the crashlytics dependency and fabric repository and plugin from `build.gradle`; the `meta-data` tag from the `AndroidManifests.xml`; Crashlytics calls from all java files.   

* **Configuration**
* **Dependencies** 
1. Android SDK
2. *Fabric* (Crashlytics)
3. *Firebase* (Indexing, AdMob, JobDispatcher) 
4. [Checkout](https://github.com/serso/android-checkout) 
5. [Picasso](https://github.com/square/picasso)
6. [ButterKnife](http://jakewharton.github.io/butterknife/)
7. [Timber](https://github.com/JakeWharton/timber)
8. Refer to [`app/build.gradle`](https://github.com/maniksejwal/Memory-Assistant/blob/master/app/build.gradle) for details

* **Database configuration**
* **How to run tests** – No functioning Esspresso tests are written yet. Pre Launch Report on Google Play Console is the only automated test used.
* Deployment instructions – Leave it up to me.

### Contribution guidelines ###

* **Writing tests** – Recommended Espresso tests. Overrite the ones already present.
* **Code review**
* **Other guidelines**
### Who do I talk to? ###

* Feel free to join the [Slack workspace](https://join.slack.com/t/memory-athlete-skynet/shared_invite/enQtNDU4Njk5MTQ4NzEwLTQ4YWRhMTRkMmY0ZjllMWJmOTJkYmI3MjY3M2Q1Y2M4MGNkNmU3OGM0ZWE0MTRiZWRlZGRlN2I0NDcxMGRmN2U)
* eMail address – memoryassistantapp@gmail.com
