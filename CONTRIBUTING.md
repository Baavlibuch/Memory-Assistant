# Contributing
* [Code of Conduct](https://github.com/maniksejwal/Memory-Assistant/blob/master/CODE_OF_CONDUCT.md)
* [Writing tests](#writing-tests)
* [Issue Submission Guidelines](#issue-submission-guidelines)
* [Questions, etc](#who-do-i-talk-to)

## Issue Submission Guidelines
Before you submit your issue search the archive, maybe your question was already answered.
If your issue appears to be a bug, and hasn't been reported, open a new issue. Help us to maximize the effort we can spend fixing issues and adding new features, by not reporting duplicate issues.
The "new issue" form contains a number of prompts that you should fill out to make it easier to understand and categorize the issue.
In general, providing the following information will increase the chances of your issue being dealt with quickly:
Overview of the Issue - if an error is being thrown a non-minified stack trace helps
Motivation for or Use Case - explain why this is a bug for you
Application Version(s) - is it a regression?
Reproduce the Error - provide an example (using a screen recorder) or an unambiguous set of steps.
Related Issues - has a similar issue been reported before?
Suggest a Fix - if you can't fix the bug yourself, perhaps you can point to what might be causing the problem (line of code or commit)
If you get help, help others. Collect good karma.

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
[Espresso](https://developer.android.com/training/testing/espresso/) tests are preferred. If you prefer to learn from videos, Udacity 
has a very good [course](https://eu.udacity.com/course/advanced-android-app-development--ud855).

## Who do I talk to?
* Feel free to join the [Slack workspace](https://join.slack.com/t/memory-athlete-skynet/shared_invite/enQtNDU4Njk5MTQ4NzEwLTQ4YWRhMTRkMmY0ZjllMWJmOTJkYmI3MjY3M2Q1Y2M4MGNkNmU3OGM0ZWE0MTRiZWRlZGRlN2I0NDcxMGRmN2U)
* eMail address – memoryassistantapp@gmail.com
