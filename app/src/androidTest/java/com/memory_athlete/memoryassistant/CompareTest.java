package com.memory_athlete.memoryassistant;

import androidx.test.rule.ActivityTestRule;

import com.memory_athlete.memoryassistant.recall.RecallSimple;

import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;

public class CompareTest extends RecallSimple {
    @Rule
    public ActivityTestRule<RecallSimple> mActivityTestRule
            = new ActivityTestRule<>(RecallSimple.class);

    @Test
    public void comparisionIsCorrect() throws Exception {
        onView(withId(R.id.response_input)).perform(typeText(""));  // TODO fill in the tests

        compare(false);
        assertEquals(4, 2 + 2);
    }

    void runTest(String response, String answers, ResponseFormat responseFormat, String filePath,
                 String discipline, CompareFormat compareFormat, int noCorrect, int noWrong,
                 int noMissed, int noExtra){
        reset();
    }
}
