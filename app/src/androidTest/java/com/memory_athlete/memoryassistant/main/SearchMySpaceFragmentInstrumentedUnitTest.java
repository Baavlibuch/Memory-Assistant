package com.memory_athlete.memoryassistant.main;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.NoMatchingViewException;
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
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.memory_athlete.memoryassistant.TestHelper.waitForExecution;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;

@LargeTest
@RunWith(AndroidJUnit4ClassRunner.class)
public class SearchMySpaceFragmentInstrumentedUnitTest {

    @Rule
    public ActivityTestRule<Practice> mActivityTestRule = new ActivityTestRule<>(Practice.class);

    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.READ_EXTERNAL_STORAGE",
                    "android.permission.WRITE_EXTERNAL_STORAGE");

    @Test
    public void searchTestMySpaceFragment() {
        waitForExecution();

        // select numbers discipline
        DataInteraction cardView2 = onData(anything())
                .inAdapterView(allOf(withId(R.id.main_list),
                        childAtPosition(
                                withId(R.id.practice),
                                0)))
                .atPosition(0);
        cardView2.perform(click());

        waitForExecution();

        // go to MySpaceFragment
        ViewInteraction tabView = onView(
                allOf(withContentDescription("My Space"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.sliding_tabs),
                                        0),
                                1),
                        isDisplayed()));
        tabView.perform(click());

        // select the folder
        DataInteraction cardView3 = onData(anything())
                .inAdapterView(childAtPosition(
                        allOf(withId(R.id.my_space_relative_layout), withContentDescription("My Space file picker")),
                        2))
                .atPosition(3);
        cardView3.perform(click());

        // add a file
        ViewInteraction floatingActionButton = onView(
                allOf(withId(R.id.add_search), withContentDescription("New"),
                        childAtPosition(
                                allOf(withId(R.id.my_space_relative_layout), withContentDescription("My Space file picker"),
                                        withParent(withId(R.id.viewpager))),
                                0),
                        isDisplayed()));
        floatingActionButton.perform(click());

        // enter file name
        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.f_name),
                        childAtPosition(
                                allOf(withId(R.id.edit_mySpaceFragment_layout),
                                        childAtPosition(
                                                allOf(withId(R.id.my_space_relative_layout), withContentDescription("My Space file picker")),
                                                1)),
                                0),
                        isDisplayed()));
        appCompatEditText.perform(replaceText("w"), closeSoftKeyboard());

        // enter text
        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.my_space_editText), withText(""),
                        childAtPosition(
                                allOf(withId(R.id.my_space_scroll_view),
                                        childAtPosition(
                                                withId(R.id.edit_mySpaceFragment_layout),
                                                1)),
                                0)));
        appCompatEditText4.perform(scrollTo(), replaceText("1.acgt\nasdf\nsdfgjq\ntix\n\nzcvxcvbcbvn,\nqaz\nwsxd\ny\nop\n2.aacgt\npl,\nokm\ni\nytfrd\nq3\n3.acgtt\nzxcv\nxvcb\nb\n\ndg\n\nt\n\n4.aacgtt\n"));

        // Close keypad
//        ViewInteraction appCompatEditText5 = onView(
//                allOf(withId(R.id.my_space_editText), withText("1.acgt\nasdf\nsdfgjq\ntix\n\nzcvxcvbcbvn,\nqaz\nwsxd\ny\nop\n2.aacgt\npl,\nokm\ni\nytfrd\nq3\n3.acgtt\nzxcv\nxvcb\nb\n\ndg\n\nt\n\n4.aacgtt\n"),
//                        childAtPosition(
//                                allOf(withId(R.id.my_space_scroll_view),
//                                        childAtPosition(
//                                                withId(R.id.edit_mySpaceFragment_layout),
//                                                1)),
//                                0),
//                        isDisplayed()));
//        appCompatEditText5.perform(closeSoftKeyboard());

        // tap search FAB
        ViewInteraction floatingActionButton2 = onView(
                allOf(withId(R.id.add_search), withContentDescription("Search"),
                        childAtPosition(
                                allOf(withId(R.id.my_space_relative_layout), withContentDescription(
                                        mActivityTestRule.getActivity().getString(R.string.my_space_file_picker)),
                                        withParent(withId(R.id.viewpager))),
                                0),
                        isDisplayed()));
        floatingActionButton2.perform(click());

        // enter search string
        ViewInteraction appCompatEditText6 = onView(
                allOf(withId(R.id.search_edit_text_mySpaceFragment),
                        childAtPosition(
                                allOf(withId(R.id.edit_mySpaceFragment_layout),
                                        childAtPosition(
                                                allOf(withId(R.id.my_space_relative_layout), withContentDescription(
                                                        mActivityTestRule.getActivity().getString(R.string.my_space_file_picker))),
                                                1)),
                                2),
                        isDisplayed()));
        appCompatEditText6.perform(replaceText("acgt"), closeSoftKeyboard());

        // wait 5s for old toast messages to disappear
        for (int i = 0; i < 5; i++) waitForExecution();

        // press done to search
        ViewInteraction appCompatEditText7 = onView(
                allOf(withId(R.id.search_edit_text_mySpaceFragment), withText("acgt"),
                        childAtPosition(
                                allOf(withId(R.id.edit_mySpaceFragment_layout),
                                        childAtPosition(
                                                allOf(withId(R.id.my_space_relative_layout), withContentDescription(
                                                        mActivityTestRule.getActivity().getString(R.string.my_space_file_picker))),
                                                1)),
                                2),
                        isDisplayed()));
        appCompatEditText7.perform(pressImeActionButton());
        // searchIndex == 1
        try {
            // the Toast should not be found and an exception should be thrown. If it is found, fail the test
            onView(withText(R.string.search_from_start)).inRoot(withDecorView(not(is(
                    mActivityTestRule.getActivity().getWindow().getDecorView())))).check(matches(not(isDisplayed())));
        } catch (NoMatchingViewException e) {
            // ignore
        }

        // tap search FAB
        floatingActionButton2.perform(click());
        // searchIndex == 2
        try {
            // the Toast should not be found and an exception should be thrown. If it is found, fail the test
            onView(withText(R.string.search_from_start)).inRoot(withDecorView(not(is(
                    mActivityTestRule.getActivity().getWindow().getDecorView())))).check(matches(not(isDisplayed())));
        } catch (NoMatchingViewException e) {
            // ignore
        }

        // tap search FAB
        floatingActionButton2.perform(click());
        // searchIndex == 3
        try {
            // the Toast should not be found and an exception should be thrown. If it is found, fail the test
            onView(withText(R.string.search_from_start)).inRoot(withDecorView(not(is(
                    mActivityTestRule.getActivity().getWindow().getDecorView())))).check(matches(not(isDisplayed())));
        } catch (NoMatchingViewException e) {
            // ignore
        }

        // tap search FAB
        floatingActionButton2.perform(click());
        // searchIndex == 4
        try {
            // the Toast should not be found and an exception should be thrown. If it is found, fail the test
            onView(withText(R.string.search_from_start)).inRoot(withDecorView(not(is(
                    mActivityTestRule.getActivity().getWindow().getDecorView())))).check(matches(not(isDisplayed())));
        } catch (NoMatchingViewException e) {
            // ignore
        }

        // tap search FAB
        floatingActionButton2.perform(click());
        // searchIndex == -1. Reached the bottom
        onView(withText(R.string.search_from_start)).inRoot(withDecorView(not(is(
                mActivityTestRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));

        // wait for toast to disappear (2s)
        for (int i = 0; i < 2; i++) waitForExecution();

        // tap search FAB
        floatingActionButton2.perform(click());
        // searchIndex == 1
        try {
            // the Toast should not be found and an exception should be thrown. If it is found, fail the test
            onView(withText(R.string.search_from_start)).inRoot(withDecorView(not(is(
                    mActivityTestRule.getActivity().getWindow().getDecorView())))).check(matches(not(isDisplayed())));
        } catch (NoMatchingViewException e) {
            // ignore
        }

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
