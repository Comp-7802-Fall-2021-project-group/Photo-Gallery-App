package com.example.photogalleryapp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.widget.DatePicker;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.contrib.PickerActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.hamcrest.Matchers;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void test11_useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.photogalleryapp", appContext.getPackageName());
    }

    // Move to next picture (move right) and go back to previous picture (left)
    @Test
    public void test21_galleryNavigation() {
        navigateRightLeft();
    }

    // Move to next picture (move right) and go back to previous picture (left)
    @Test
    public void test22_galleryUpdateCaptionNavigation() {
        // set caption for the last picture
        navigateAllRight();
        // enter text string
        onView(withId(R.id.editTextCaption)).perform(replaceText(""), closeSoftKeyboard());
        onView(withId(R.id.editTextCaption)).perform(typeText("test caption from espresso"), closeSoftKeyboard());
        // press update and check match
        onView(withId(R.id.btnUpdateCaption)).perform(click());
        onView(withId(R.id.editTextCaption)).check(matches(withText("test caption from espresso")));

        // move away 2 pics, return and check match
        navigateLeftRight();
        onView(withId(R.id.editTextCaption)).check(matches(withText("test caption from espresso")));
    }

    // Move to next picture (move right) and go back to previous picture (left)
    @Test
    public void test23_galleryUpdateCaptionNavigation() {
        navigateAllRight();
        onView(withId(R.id.editTextCaption)).check(matches(withText("test caption from espresso")));

        // enter new text string
        onView(withId(R.id.editTextCaption)).perform(replaceText(""), closeSoftKeyboard());
        onView(withId(R.id.editTextCaption)).perform(typeText("another caption from espresso"), closeSoftKeyboard());
        // press update and check match
        onView(withId(R.id.btnUpdateCaption)).perform(click());
        onView(withId(R.id.editTextCaption)).check(matches(withText("another caption from espresso")));
    }

    @Test
    public void test24_galleryUpdateCaptionNavigation() {
        navigateAllRight();
        onView(withId(R.id.editTextCaption)).check(matches(withText("another caption from espresso")));
    }

    // Find a photo by a specific date range & keyword
    @Test
    public void test31_searchByDateCancel() {
        // pick start day
        onView(withId(R.id.buttonSearch)).perform(click());
        onView(withId(R.id.startDate)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(2021, 10, 1));
        onView(withText("OK")).perform(click());
        onView(withId(R.id.startDate)).check(matches(withText("20211001")));

        // pick end day
        onView(withId(R.id.endDate)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(2021, 10, 5));
        onView(withText("OK")).perform(click());
        onView(withId(R.id.endDate)).check(matches(withText("20211005")));

        // click cancel
        onView(withId(R.id.buttonCancel)).perform(click());
    }

    // Find a photo by a specific date range & keyword
    @Test
    public void test32_searchByOutOfRangeDateSubmit() {
        // pick start day
        onView(withId(R.id.buttonSearch)).perform(click());
        onView(withId(R.id.startDate)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(2021, 10, 1));
        onView(withText("OK")).perform(click());
        onView(withId(R.id.startDate)).check(matches(withText("20211001")));

        // pick end day
        onView(withId(R.id.endDate)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(2021, 10, 2));
        onView(withText("OK")).perform(click());
        onView(withId(R.id.endDate)).check(matches(withText("20211002")));

        // click cancel
        onView(withId(R.id.buttonSubmit)).perform(click());

        // try to go left and right
        navigateRightLeft();

    }

    // Find a photo by a specific date range & keyword
    @Test
    public void test33_searchByInRangeDateSubmit() {
        // pick start day
        onView(withId(R.id.buttonSearch)).perform(click());
        onView(withId(R.id.startDate)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(2021, 10, 11));
        onView(withText("OK")).perform(click());
        onView(withId(R.id.startDate)).check(matches(withText("20211011")));

        // pick end day
        onView(withId(R.id.endDate)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(2021, 10, 12));
        onView(withText("OK")).perform(click());
        onView(withId(R.id.endDate)).check(matches(withText("20211012")));

        // click submit
        onView(withId(R.id.buttonSubmit)).perform(click());

        // try to go left and right
        navigateRightLeft();

    }

    // Find a photo by a specific date range & keyword
    @Test
    public void test34_searchByNonExistKeyword() {
        searchByKeyword("nonsense");
    }

    @Test
    public void test35_searchByExistKeyword() {
        searchByKeyword("test1");
        searchByKeyword("test2");
        searchByKeyword("test3");
        searchByKeyword("test4");
        searchByKeyword("test5");
        searchByKeyword("test1 test3");
        searchByKeyword("test2 test4");
        searchByKeyword("test1 test5");
        searchByKeyword("espresso");
    }

    @Test
    public void test36_searchByEmptyLatLong() {
        searchByLatLong(null, null);
        searchByLatLong("0", null);
        searchByLatLong(null, "0");
    }

    @Test
    public void test36_searchByValidLatLong() {
        searchByLatLong("0", "0");
        searchByLatLong("49.2414", "-123.0584");
        searchByLatLong("49.4951", "-123.1767");
        searchByLatLong("37.4219", "-122.0841");
        searchByLatLong("48.9631", "-122.3763");
    }


    // Navigation methods
    public void navigateAllLeft() {
        int count = MainActivity.getPhotoCount();
        for (int i = 0; i < count; i++) {
            onView(withId(R.id.buttonLeft)).perform(click());
        }
    }

    public void navigateAllRight() {
        int count = MainActivity.getPhotoCount();
        for (int i = 0; i < count; i++) {
            onView(withId(R.id.buttonRight)).perform(click());
        }
    }

    public void navigateRightLeft() {
        // try to go left and right
        navigateAllRight();
        navigateAllLeft();
    }

    public void navigateLeftRight() {
        // try to go left and right
        navigateAllLeft();
        navigateAllRight();
    }


    // Find a photo by a specific date range & keyword
    public void searchByKeyword(String keyword) {
        // pick start day
        onView(withId(R.id.buttonSearch)).perform(click());

        //enter keyword
        onView(withId(R.id.editKeywordSearch)).perform(replaceText(""), closeSoftKeyboard());
        onView(withId(R.id.editKeywordSearch)).perform(typeText(keyword), closeSoftKeyboard());

        // click submit
        onView(withId(R.id.buttonSubmit)).perform(click());

        // navigate right left
        navigateRightLeft();
    }

    // Find a photo by a specific date range & keyword
    public void searchByLatLong(String latitude, String longitude) {
        // pick start day
        onView(withId(R.id.buttonSearch)).perform(click());

        //enter keyword
        if (latitude != null) {
            onView(withId(R.id.latitude)).perform(replaceText(""), closeSoftKeyboard());
            onView(withId(R.id.latitude)).perform(typeText(latitude), closeSoftKeyboard());
        }

        if (longitude != null) {
            onView(withId(R.id.longitude)).perform(replaceText(""), closeSoftKeyboard());
            onView(withId(R.id.longitude)).perform(typeText(longitude), closeSoftKeyboard());
        }

        // click submit
        onView(withId(R.id.buttonSubmit)).perform(click());

        // navigate right left
        navigateRightLeft();
    }


    public void restartActivity() {
        ActivityScenario<MainActivity> scenario = activityScenarioRule.getScenario();
        scenario.moveToState(Lifecycle.State.RESUMED);
        scenario.recreate();
    }

}
