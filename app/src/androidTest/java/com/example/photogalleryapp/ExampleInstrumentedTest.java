package com.example.photogalleryapp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Context;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule
            = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.photogalleryapp", appContext.getPackageName());
    }

    // Find a photo by a specific date range & keyword
    @Test
    public void searchByDate() {
        onView(withId(R.id.buttonLeft)).perform(click());
        onView(withId(R.id.startDate)).perform(typeText(""), closeSoftKeyboard());
        onView(withId(R.id.endDate)).perform(typeText(""), closeSoftKeyboard());
        onView(withId(R.id.editKeywordSearch)).perform(typeText("caption"), closeSoftKeyboard());
        onView(withId(R.id.button5)).perform(click());
        onView(withId(R.id.editTextCaption)).check(matches(withText("caption")));
    }

    // Move to next picture (move right) and go back to previous picture (left)
    @Test
    public void galleryNavigation() {
        onView(withId(R.id.buttonLeft)).perform(click());
        onView(withId(R.id.buttonRight)).perform(click());
    }
}
