package com.memory_athlete.memoryassistant;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4ClassRunner.class)
public class SimpleCompareInstrumentedUnitTest {
    // LETTERS  -   0: small (97)  |  1: capital (65)  |  2: mixed (65 + r + c * 32)   c == 0 or 1

    // TODO: THIS DOES NOT WORK

    //@Rule
    //public ActivityTestRule<RecallSimple> mActivityTestRule
    //        = new ActivityTestRule<>(RecallSimple.class);

    @Test
    public void comparisionIsCorrect() {
//        onView(withId(R.id.response_input)).perform(typeText(""));  // TODO fill in the tests
//        // single letter
//        runTest("a", "a", ResponseFormat.CHARACTER_RESPONSE_FORMAT, "Letters",
//                CompareFormat.SIMPLE_COMPARE_FORMAT, 1, 0, 0, 0, -1,
//                Helper.LOWER_CASE, false);
//        runTest("ABCDEFGHIJK", "ABCDEGHIJKL", ResponseFormat.CHARACTER_RESPONSE_FORMAT, "Letters",
//                CompareFormat.SIMPLE_COMPARE_FORMAT, 10, 0, 1, 1, -1,
//                Helper.UPPER_CASE, false);
//        runTest("a", "A", ResponseFormat.CHARACTER_RESPONSE_FORMAT, "Letters",
//                CompareFormat.SIMPLE_COMPARE_FORMAT, 1, 0, 0, 0, -1,
//                Helper.UPPER_CASE, false);
//
//        // numbers
//        runTest("123456", "123456", ResponseFormat.SIMPLE_RESPONSE_FORMAT, "Numbers",
//                CompareFormat.SIMPLE_COMPARE_FORMAT, 6, 0, 0, 0, 1,
//                -1, false);
//
//        // words
//        runTest("abcdef\ndefghi\nghijkl", "def\ndeghi\nghijkl", ResponseFormat.SIMPLE_RESPONSE_FORMAT, "Numbers",
//                CompareFormat.WORD_COMPARE_FORMAT, 6, 1, 0, 0, 1,
//                -1, true);
//
//        compare(false);
//
//        waitForExecution();
    }

//    void runTest(String response, String answerString, ResponseFormat responseFormat,
//                 String discipline, CompareFormat compareFormat, int noCorrect, int noWrong,
//                 int noMissed, int noExtra, int noOfSpelling, int letterCase, boolean words){
//        // TODO use files
//
//        mDiscipline = discipline;
//        this.compareFormat = compareFormat;
//        this.responseFormat = responseFormat;
//
//        ((EditText) findViewById(R.id.response_input)).setText(response);
//        getResponse();
//        answersAsList(answerString);
//
//        if (letterCase == Helper.MIXED_CASE) compareMixed();
//        else compare(words);
//
//        assertEquals(noCorrect, this.correctCount);
//        assertEquals(noWrong, this.wrongCount);
//        assertEquals(noMissed, this.missedCount);
//        assertEquals(noOfSpelling, this.spellingCount);
//        assertEquals(noExtra, this.extraCount);
//
//        reset();                                                        // Recall___.reset()
//        // TODO move to the next test, create new activity if needed
//    }
//
//    void answersAsList(String answerString){
//        Scanner scanner = new Scanner(answerString).useDelimiter("\t|\n|\n\n");
//        String string;
//        while (scanner.hasNext()) {
//            string = scanner.next();
//            if (mDiscipline.equals(getString(R.string.numbers)) || mDiscipline.equals(getString(R.string.cards)))
//                this.answers.add(string.trim());
//                //else if (mDiscipline == getString(e))
//            else if (mDiscipline.equalsIgnoreCase(getString(R.string.letters))
//                    || mDiscipline.equalsIgnoreCase(getString(R.string.binary))
//                    || mDiscipline.equalsIgnoreCase(getString(R.string.digits))) {
//                for (char c : string.toCharArray())
//                    if (c != ' ') this.answers.add("" + c);
//            } else this.answers.add(string);
//        }
//        scanner.close();
//    }
}
