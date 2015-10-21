package com.example.nick.whattodolist.test;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.ActivityInstrumentationTestCase2;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;

import com.example.nick.whattodolist.MainToDo;
import com.example.nick.whattodolist.TaskDBContract;
import com.example.nick.whattodolist.TaskDbHelper;
import com.example.nick.whattodolist.RepeatingBasisEditor;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


import java.lang.Override;


/**
 * Created by Nick on 10/3/2015.
 */
//need to test
public class TaskEditorTest extends ActivityInstrumentationTestCase2<MainToDo>{
        MainToDo activity;
        Context mContext = null;
        TaskDBContract dbContract;
        TaskDbHelper mDbHelper;
        SQLiteDatabase dbR;
        SQLiteDatabase dbW;

    public TaskEditorTest(){
        super(MainToDo.class);
    }


    @Before
@Override
public void setUp() throws Exception {
        super.setUp();
        getInstrumentation().waitForIdleSync();
        activity = getActivity();
        mContext = activity;
        }

    @Test
    public void testRepeatingRowCreated(){
        R
        int[] dayOfWeek = new int[7];
        dayOfWeek[0] = 0;
        dayOfWeek[1] = 0;
        dayOfWeek[2] = 0;
        dayOfWeek[3] = 0;
        dayOfWeek[4] = 0;
        dayOfWeek[5] = 0;
        dayOfWeek[6] = 0;


        long newRowId = repeatingBasisEditor.createBasis(1, 1, 1, 1, dayOfWeek, "2015-01-01", "2015-01-01");

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