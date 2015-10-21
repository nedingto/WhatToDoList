package com.example.nick.whattodolist.test;

import android.app.LauncherActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;

import android.content.Context;

import android.test.ActivityUnitTestCase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.example.nick.whattodolist.MainToDo;
import com.example.nick.whattodolist.R;
import com.example.nick.whattodolist.TaskDBContract;
import com.example.nick.whattodolist.TaskDbHelper;
import com.example.nick.whattodolist.repeatingBasisCreator;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.test.AndroidTestCase;
import android.test.InstrumentationTestCase;
import 	android.test.ProviderTestCase2;
import android.test.mock.MockContext;

import org.junit.Test;


/**
 * Created by Nick on 10/3/2015.
 */
@RunWith(AndroidJUnit4.class)
public class MainToDoTest extends AndroidTestCase {

    MainToDo mActivity;
    TaskDBContract dbContract;
    TaskDbHelper mDbHelper;
    SQLiteDatabase dbR;
    SQLiteDatabase dbW;



    public MainToDoTest() {
        super();
    }

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        //mActivity = getActivity();
        dbContract = new TaskDBContract();
        mDbHelper = new TaskDbHelper(getContext());

        //set up reader and writer
        dbR = mDbHelper.getReadableDatabase();
    }
    //@Rule
    //public ActivityTestRule<MainToDo> mActivityRule =
    //        new ActivityTestRule<>(MainToDo.class);

    /*@Override
    protected void setUp() throws Exception {
        super.setUp();
        Intent mLaunchIntent = new Intent(getInstrumentation()
                .getTargetContext(), LauncherActivity.class);
        startActivity(mLaunchIntent, null, null);

        dbContract = new TaskDBContract();
        mDbHelper = new TaskDbHelper(mActivity.getApplicationContext());

        //set up reader and writer
        dbR = mDbHelper.getReadableDatabase();
        dbW = mDbHelper.getWritableDatabase();

    }

    @Test
    public void createTask(){
        onView(withId(R.id.createTaskButton)).perform(click());
        onView(withId(R.id.editText6)).check(matches(isDisplayed()));


    }
*/
    @Test
    public void repeatingRowCreated(){

        Bundle testBundle = new Bundle();
        testBundle.putInt(repeatingBasisCreator.BUNDLE_PERIODICAL_PERIOD, 1);
        testBundle.putIntArray(repeatingBasisCreator.BUNDLE_DAY_OF_WEEK, new int[]{0, 0, 0, 0, 0, 0, 0});
        long newRowId = repeatingBasisCreator.createBasis(dbW, testBundle);

        String[] projection = {
                TaskDBContract.TaskDB._ID,
        };

        Cursor cursor = dbR.query(
                TaskDBContract.TaskDB.REPEATING_TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                TaskDBContract.TaskDB.REPEATING_TABLE_NAME + "." + TaskDBContract.TaskDB._ID + "=?",         // The columns for the WHERE clause
                new String[] {String.valueOf(newRowId)},                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );
        assertTrue(cursor.getCount() > 0);
    }
}