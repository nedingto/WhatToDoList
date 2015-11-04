package com.example.nick.whattodolist.test;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.content.Context;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.example.nick.whattodolist.MainToDo;
import com.example.nick.whattodolist.TaskDBContract;
import com.example.nick.whattodolist.TaskDbHelper;
import com.example.nick.whattodolist.RepeatingBasisEditor;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Created by Nick on 10/3/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class MainToDoTest{

    MainToDo mActivity;
    TaskDBContract dbContract;
    TaskDbHelper mDbHelper;
    SQLiteDatabase dbR;
    SQLiteDatabase dbW;

    @Mock
    Context mMockContext;


    /*public MainToDoTest() {
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
    } */
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
        dbContract = new TaskDBContract();
        mDbHelper = new TaskDbHelper(mMockContext.getApplicationContext());

        //set up reader and writer
        dbR = mDbHelper.getReadableDatabase();
        dbW = mDbHelper.getWritableDatabase();
        int[] dayOfWeek = new int[7];
        dayOfWeek[0] = 0;
        dayOfWeek[1] = 0;
        dayOfWeek[2] = 0;
        dayOfWeek[3] = 0;
        dayOfWeek[4] = 0;
        dayOfWeek[5] = 0;
        dayOfWeek[6] = 0;


        long newRowId = RepeatingBasisEditor.createBasis(dbW, 1, 1, 1, 1, dayOfWeek, "2015-01-01", "2015-01-01");

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