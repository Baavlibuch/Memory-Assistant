package com.memory_athlete.memoryassistant.main;


import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.memory_athlete.memoryassistant.TestHelper.waitForExecution;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import com.memory_athlete.memoryassistant.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

// Using BVA
@LargeTest
@RunWith(AndroidJUnit4ClassRunner.class)
public class RecallDisciplineNameInstrumentedIntegratedTests {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.READ_EXTERNAL_STORAGE",
                    "android.permission.WRITE_EXTERNAL_STORAGE");

    @Test
    public void recallDigitsInstrumentedIntegrationTests() {
        executeTest(0, "Digits");
    }

    @Test
    public void recallWordsInstrumentedIntegrationTests() {
        executeTest(1, "Words");
    }

    @Test
    public void recallBinaryInstrumentedIntegrationTests() {
        executeTest(6, "Binary Digits");
    }

    @Test
    public void recallLettersInstrumentedIntegrationTests() {
        executeTest(7, "Letters");
    }

    private void executeTest(int disciplinePosition, String expectedValue){
        waitForExecution();

        // Select Practice
        DataInteraction cardView = onData(anything())
                .inAdapterView(allOf(withId(R.id.main_list),
                        childAtPosition(
                                withId(R.id.main_linear_layout),
                                0)))
                .atPosition(1);
        cardView.perform(click());

        waitForExecution();

        // Select Discipline
        DataInteraction cardView2 = onData(anything())
                .inAdapterView(allOf(withId(R.id.main_list),
                        childAtPosition(
                                withId(R.id.practice),
                                0)))
                .atPosition(disciplinePosition);
        cardView2.perform(click());

        waitForExecution();

        // TODO Accidental double tap. tap a different position to test
//        DataInteraction cardView3 = onData(anything())
//                .inAdapterView(allOf(withId(R.id.main_list),
//                        childAtPosition(
//                                withId(R.id.practice),
//                                0)))
//                .atPosition(0);
//        cardView3.perform(click());

//        waitForExecution();

        // Start
        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.start), withText("Start"),
                        childAtPosition(
                                allOf(withId(R.id.buttons),
                                        childAtPosition(
                                                withClassName(is("android.widget.HorizontalScrollView")),
                                                0)),
                                1)));
        appCompatButton.perform(scrollTo(), click());

        // Recall
        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.recall), withText("Recall"),
                        childAtPosition(
                                allOf(withId(R.id.buttons),
                                        childAtPosition(
                                                withClassName(is("android.widget.HorizontalScrollView")),
                                                0)),
                                6)));
        appCompatButton2.perform(scrollTo(), click());

        waitForExecution();

        // Check title
        onView(allOf(isAssignableFrom(TextView.class), withParent(isAssignableFrom(Toolbar.class))))
                .check(matches(withText(expectedValue)));
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
