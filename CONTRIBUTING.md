# Contributing
* [Introduction](#introduction)
* [Code of Conduct](https://github.com/maniksejwal/Memory-Assistant/blob/master/CODE_OF_CONDUCT.md)
* [Translation Guidelines](#translation-guidelines)
* [Issue Submission Guidelines](#issue-submission-guidelines)
* [Pull Request Submission Guidelines](#pull-request-submission-guidelines)
* [Writing tests](#writing-tests)
* [Questions, etc](#who-do-i-talk-to)

## Introduction
* The first step you need to take to be able to contribute to software is to believe in yourself; working on sowtware and coding are actually quite easy.
* The second step is to create an account on [Github](https://github.com/join?source=header). Github is the place where code, resources, backups, etc. are stored and can be played around with.
* Most of the unfamiliar work can be done by simply using the onscreen buttons on the page so you will barely have to learn anything.
* To edit a file on your browser, simply click on the edit button (pen) and edit the text. Once done, scroll to the bottom of the web page and fill in the message and description. Don't forget to press the commit button once you are done. Once all this is done, create a pull request.

## Translation Guidelines
* The translation instructions are for non tech savvy people who want to contribute to the project. They are written with the assumption that you just want to make a quick contribution using the web browser on your phone.
* **Theory** - There are three ways in which text resources are stored in Android.
  1. **Key-value pairs in [strings.xml](https://github.com/maniksejwal/Memory-Assistant/blob/master/app/src/main/res/values/strings.xml)** - These are small pieces of text used throughout the application for various tasks such as displaying messages, content labelling, etc.
  2. **[Raw text files](https://github.com/maniksejwal/Memory-Assistant/tree/master/app/src/main/res/raw)** - These are larger pieces of text which are not well suited to be put together into a single file. In this project, these files are formatted using [HTML](https://htmlcheatsheet.com/), which can take less than 10 minutes to be learnt from scratch.
  3. **[Assets directory](https://github.com/maniksejwal/Memory-Assistant/tree/master/app/src/main/assets/Implement)** - Android has an assets directory which can be used to store a variety of data in whichever format the developer likes. It is often used to store larger amounts of data and files. It is used in this project to make organise files better.

* **Locales**
The resources in the default language are stored in the `res/values/strings.xml`, `res/raw/` or `assets`. 
The resources in other languages are stored at different locations but have the same filename and key. 
  1. An English (default) value `<string name="practice">Practice</string>` is stored in [`app/src/main/res/values/strings.xml`](master/app/src/main/res/values/strings.xml) while the same is stored in [`app/src/main/res/values-fr/strings.xml`](master/app/src/main/res/values-fr/strings.xml) as `<string name="practice">Entraine</string>` for French. The only differences here are the actual value and the folders `values` and `values-fr`.
  2. An English (default) raw file is stored as [`app/src/main/res/raw/lesson_equations.txt`](master/app/src/main/res/raw/lesson_pao.txt) while the same is stored as [`app/src/main/res/raw-jp/lesson_equation.txt`](master/app/src/main/res/raw-jp/lesson_pao.txt) for Japanese. Differences - actual contents and the folders `raw` and `raw-jp`.
  3. An English (default) assets file is stored in [`app/src/main/assets/...`] while the same is stored as [`app/src/main/assets-ar/...`] for Arabian. Differences - actual contents within the files present deep inside the folders and the folders `assets` and `assets-ar`.
  
* **Formatting**
  1. Most of the resources are formatted using HTML and the app uses the tags to improve the UI further. They cannot be changed without changing the behaviour of the app.
  2. Automated translation tools often change the tags which has to be fixed manually. Prefer your own text editer instead of the one available here. Column selection mode can be quite useful.
  3. The tags cannot be edited but text between 2 tags can be edited. This is what has to be translated. In some cases, quotes ("", '') can also be used as tags. `<This is a tag>` `</This is also a tag>` `<...>Translate this<...>` `<Do NOT modify this>`
  4. Once a file has been translated, be sure to test it out. You con either compile the app or you can copy the file into your machine, change the extension from `.txt` to `.html` and double click on it to run it in your browser. Go through the new version atleast once to ensure that there are no typographical or semantic errors.
  
* **Translation Submission Guidelines** (using only the web browser)
  1. Create a fork of this repository. The button is in the top right corner of this web page.
  2. Navigate to the file that you want to translate and copy everything. Paths to resources - [`app/src/main/res`](master/app/src/main/assets/Implement/), path to assets - [`app/src/main/assets/Implement/`](master/app/src/main/assets/Implement/). Be sure to work on the `master` branch.
  3. Priority of translation: `res/values/strings.xml` > `res/raw/lesson_xxx.txt` > `res/raw/privacy_policy.txt` > `res/raw/xxx.txt` > `assets/Implement/xxx`. You can edit file names and folder names in `assets/Implement/`. Do **not** edit anything related to jq_math, it contains code.
  4. Go back to the parent folder and click the `Create new file` button. Give the same name to the file. Simply hitting backspace in the 'Name your file' box allows you to edit the path.
  5. Make sure that everything except `-xx` matches. You can find the value of -xx by searching for `(Language) locale` on your search engine.
  6. Paste the contents you copied in step 2 and begin the translation process.
  7. Click `Commit new file` to save the changes. Be sure to give a descriptive name to the commit. Do NOT commit directly to the master branch if you have the option.
  8. Test whether the file is doing what you want them to do
  9. Once you have are done, create a Pull Request to `maniksejwal/memory-assistant:master`.

## Issue Submission Guidelines
* Before you submit your issue search the archive, maybe your question was already answered. 
* If your issue appears to be a bug, and hasn't been reported, open a new issue. Help us to maximize the effort we can spend fixing issues and adding new features, by not reporting duplicate issues. 
* The "new issue" form contains a number of prompts that you should fill out to make it easier to understand and categorize the issue. 
* In general, providing the following information will increase the chances of your issue being dealt with quickly: 
* Overview of the Issue - if an error is being thrown a non-minified stack trace helps 
* Motivation for or Use Case - explain why this is a bug for you 
* Application Version(s) - is it a regression? 
* Reproduce the Error - provide an example (using a screen recorder) or an unambiguous set of steps. 
* Related Issues - has a similar issue been reported before? 
* Suggest a Fix - if you can't fix the bug yourself, perhaps you can point to what might be causing the problem (line of code or commit) 
* If you get help, help others. Collect good karma. 

## Pull Request Submission Guidelines
* Create the [development environment](README.md)
* Make your changes in a new git branch:
  `git checkout -b my-fix-branch master`
* Create your patch commit, including appropriate test cases.
* Follow Java's and Android's [Coding Rules](https://source.android.com/setup/contribute/code-style).
* Run the tests, and ensure that they pass. 
* Run eslint on Android Studio to check that you have followed the automatically enforced coding rules
* Commit your changes using a descriptive commit message. `git commit -a` 
Note: the optional commit -a command line option will automatically "add" and "rm" edited files.
* Before creating the Pull Request, run all tests for a last time.

**Push your branch to GitHub:**
`git push origin my-fix-branch`
* In GitHub, send a pull request to `maniksejwal/memory-assistant:master`.
* If we suggest changes, then:
  * Make the required updates.
  * Commit your changes to your branch (e.g. my-fix-branch).
  * Push the changes to your GitHub repository (this will update your Pull Request).
  You can also amend the initial commits and force push them to the branch.
  ```
  git rebase master -i
  git push origin my-fix-branch -f
  ```
  This is generally easier to follow, but seperate commits are useful if the Pull Request contains iterations that might be interesting to see side-by-side.
That's it! Thank you for your contribution!

**After your pull request is merged**
After your pull request is merged, you can safely delete your branch and pull the changes from the main (upstream) repository:
* Delete the remote branch on GitHub either through the GitHub web UI or your local shell as follows:
`git push origin --delete my-fix-branch`
* Check out the master branch:
`git checkout master -f`
* Delete the local branch:
`git branch -D my-fix-branch`
* Update your master with the latest upstream version:
`git pull --ff upstream master`

## Writing tests
* If you have no knowledge on how to write automated tests, you can learn [Espresso](https://developer.android.com/training/testing/espresso/) testing for android on [Udacity](https://eu.udacity.com/course/advanced-android-app-development--ud855).
* Be creative in writing functional tests.
* UI testing is the main goal, boundary value, and other forms of tests are less important.

## Who do I talk to?
* Feel free to join the [Slack workspace](https://join.slack.com/t/memory-athlete-skynet/shared_invite/zt-5uq1mobq-i2UthsNyBXISWZHqkr2zZQ) for easy and effecient communication
* eMail address – memoryassistantapp@gmail.com
