package com.memory_athlete.memoryassistant;

import androidx.test.rule.ActivityTestRule;

import com.memory_athlete.memoryassistant.recall.RecallSimple;

import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.nio.file.Paths;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;

public class CompareInstrumentedUnitTest extends RecallSimple {
    @Rule
    public ActivityTestRule<RecallSimple> mActivityTestRule
            = new ActivityTestRule<>(RecallSimple.class);

    @Test
    public void comparisionIsCorrect() {
        onView(withId(R.id.response_input)).perform(typeText(""));  // TODO fill in the tests
        // single letter
        runTest("a", "a", ResponseFormat.SIMPLE_RESPONSE_FORMAT, "Letters",
                CompareFormat.SIMPLE_COMPARE_FORMAT, 1, 0, 0, 0);

        compare(false);
        assertEquals(4, 2 + 2);
    }

    void runTest(String response, String answers, ResponseFormat responseFormat,
                 String discipline, CompareFormat compareFormat, int noCorrect, int noWrong,
                 int noMissed, int noExtra){
        String filePath = Paths.get(Helper.APP_FOLDER,  discipline,  "test.txt").toAbsolutePath().toString();
        // TODO remove this assertion because it is only for testing
        assertEquals(filePath,
                Helper.APP_FOLDER + File.separator +  discipline + File.separator + "test.txt");

        // TODO delete file
        // TODO set values
        // TODO click check
        reset();
        // TODO move to the next test, create new activity if needed
    }
}
