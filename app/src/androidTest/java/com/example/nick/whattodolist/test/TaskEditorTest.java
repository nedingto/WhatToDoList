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
import com.example.nick.whattodolist.repeatingBasisEditor;
import com.example.nick.whattodolist.TaskEditor;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


import java.lang.Override;


/**
 * Created by Nick on 10/3/2015.
 */
//need to test with main activity to get context
public class TaskEditorTest extends ActivityInstrumentationTestCase2<MainToDo>{
        MainToDo activity;
        Context mContext;

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
        TaskEditor taskEditor = new TaskEditor(mContext);


    }
}