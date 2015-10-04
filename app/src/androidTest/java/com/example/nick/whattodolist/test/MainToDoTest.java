package com.example.nick.whattodolist.test;

import android.test.ActivityInstrumentationTestCase2;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.example.nick.whattodolist.MainToDo;
import com.example.nick.whattodolist.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by Nick on 10/3/2015.
 */
@RunWith(AndroidJUnit4.class)
public class MainToDoTest extends ActivityInstrumentationTestCase2<MainToDo> {
    MainToDo mActivity;
    public MainToDoTest() {
        super(MainToDo.class);
    }


    @Rule
    public ActivityTestRule<MainToDo> mActivityRule =
            new ActivityTestRule<>(MainToDo.class);
    @Test
    public void createTask(){
        onView(withId(R.id.createTaskButton)).perform(click());
        onView(withId(R.id.editText)).check(matches(isDisplayed()));
    }
}