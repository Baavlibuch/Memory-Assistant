package com.memory_athlete.memoryassistant;

import android.view.View;
import android.widget.Adapter;
import android.widget.ListView;

import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewAssertion;

import java.util.Objects;

import static com.memory_athlete.memoryassistant.TestHelper.waitForExecution;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

class ListViewItemCountAssertion implements ViewAssertion {
    private final int expectedCount;

    ListViewItemCountAssertion(int expectedCount) {
        this.expectedCount = expectedCount;
    }

    @Override
    public void check(View view, NoMatchingViewException noViewFoundException) {
        if (noViewFoundException != null)
            throw noViewFoundException;

        ListView listView = (ListView) view;
        Adapter adapter = listView.getAdapter();
        assertThat(Objects.requireNonNull(adapter).getCount(), is(expectedCount));
        waitForExecution();
    }
}

