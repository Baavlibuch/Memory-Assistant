package com.memory_athlete.memoryassistant.main;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

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

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.memory_athlete.memoryassistant.TestHelper.waitForExecution;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4ClassRunner.class)
public class RecallSimpleInputInstrumentedUnitTests {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.READ_EXTERNAL_STORAGE",
                    "android.permission.WRITE_EXTERNAL_STORAGE");

    @Test
    public void recallInputInstrumentedUnitTests() {
        waitForExecution();

        DataInteraction cardView = onData(anything())
                .inAdapterView(allOf(withId(R.id.main_list),
                        childAtPosition(
                                withId(R.id.main_linear_layout),
                                0)))
                .atPosition(1);
        cardView.perform(click());

        waitForExecution();

        DataInteraction cardView2 = onData(anything())
                .inAdapterView(allOf(withId(R.id.main_list),
                        childAtPosition(
                                withId(R.id.practice),
                                0)))
                .atPosition(0);
        cardView2.perform(click());

        waitForExecution();

        ViewInteraction appCompatRadioButton = onView(
                allOf(withId(R.id.custom_radio), withText("Custom"),
                        childAtPosition(
                                allOf(withId(R.id.standard_custom_radio_group),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatRadioButton.perform(click());

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.start), withText("Start"),
                        childAtPosition(
                                allOf(withId(R.id.buttons),
                                        childAtPosition(
                                                withClassName(is("android.widget.HorizontalScrollView")),
                                                0)),
                                1)));
        appCompatButton.perform(scrollTo(), click());

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

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.response_input),
                        childAtPosition(
                                allOf(withId(R.id.text_response_scroll_view),
                                        childAtPosition(
                                                withId(R.id.response_layout),
                                                0)),
                                0)));
        appCompatEditText.perform(scrollTo(), replaceText("98 14\n56"), closeSoftKeyboard());

        ViewInteraction editText = onView(
                allOf(withId(R.id.response_input), withText("98 14\n56"),
                        childAtPosition(
                                allOf(withId(R.id.text_response_scroll_view),
                                        childAtPosition(
                                                withId(R.id.response_layout),
                                                0)),
                                0),
                        isDisplayed()));
        editText.check(matches(withText("98 14\n56")));

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.check), withText("Check"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.button_bar),
                                        0),
                                1)));
        appCompatButton3.perform(scrollTo(), click());

        ViewInteraction appCompatImageButton = onView(
                allOf(withContentDescription("Navigate up"),
                        childAtPosition(
                                allOf(withId(R.id.action_bar),
                                        childAtPosition(
                                                withId(R.id.action_bar_container),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatImageButton.perform(click());

        waitForExecution();

        ViewInteraction appCompatImageButton2 = onView(
                allOf(withContentDescription("Navigate up"),
                        childAtPosition(
                                allOf(withId(R.id.action_bar),
                                        childAtPosition(
                                                withId(R.id.action_bar_container),
                                                0)),
                                0),
                        isDisplayed()));
        appCompatImageButton2.perform(click());

        waitForExecution();

        DataInteraction cardView3 = onData(anything())
                .inAdapterView(allOf(withId(R.id.main_list),
                        childAtPosition(
                                withId(R.id.practice),
                                0)))
                .atPosition(1);
        cardView3.perform(click());

        waitForExecution();

        ViewInteraction appCompatButton4 = onView(
                allOf(withId(R.id.start), withText("Start"),
                        childAtPosition(
                                allOf(withId(R.id.buttons),
                                        childAtPosition(
                                                withClassName(is("android.widget.HorizontalScrollView")),
                                                0)),
                                1)));
        appCompatButton4.perform(scrollTo(), click());

        ViewInteraction appCompatButton5 = onView(
                allOf(withId(R.id.recall), withText("Recall"),
                        childAtPosition(
                                allOf(withId(R.id.buttons),
                                        childAtPosition(
                                                withClassName(is("android.widget.HorizontalScrollView")),
                                                0)),
                                6)));
        appCompatButton5.perform(scrollTo(), click());

        waitForExecution();

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.response_input),
                        childAtPosition(
                                allOf(withId(R.id.text_response_scroll_view),
                                        childAtPosition(
                                                withId(R.id.response_layout),
                                                0)),
                                0)));
        appCompatEditText2.perform(scrollTo(), replaceText("besiege\nought n't"), closeSoftKeyboard());

        ViewInteraction editText3 = onView(
                allOf(withId(R.id.response_input), withText("besiege\nought n't"),
                        childAtPosition(
                                allOf(withId(R.id.text_response_scroll_view),
                                        childAtPosition(
                                                withId(R.id.response_layout),
                                                0)),
                                0),
                        isDisplayed()));
        editText3.check(matches(withText("besiege\nought n't")));
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
