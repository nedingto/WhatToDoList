package com.example.nick.whattodolist.test;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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
import java.util.ArrayList;


/**
 * Created by Nick on 10/3/2015.
 */
//need to test with main activity to get context
public class TaskEditorTest extends ActivityInstrumentationTestCase2<MainToDo>{
        MainToDo activity;
        Context mContext;
        TaskEditor taskEditor;
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
        taskEditor = new TaskEditor(mContext);
        TaskDbHelper mDbHelper = new TaskDbHelper(mContext);

        //set up reader and writer
        dbR = mDbHelper.getReadableDatabase();
        dbW = mDbHelper.getWritableDatabase();
        dbR.delete(TaskDBContract.TaskDB.TASK_TABLE_NAME,null,null);
        dbR.delete(TaskDBContract.TaskDB.REPEATING_TABLE_NAME,null,null);
        dbR.delete(TaskDBContract.TaskDB.CATEGORY_TABLE_NAME,null,null);
        dbR.delete(TaskDBContract.TaskDB.TASK_RELATION_TABLE_NAME,null,null);
        dbR.delete(TaskDBContract.TaskDB.TASK_CATEGORY_TABLE_NAME,null,null);
        }

    @Test
    public void testGetCategories9_1(){
        String categoryName = "category";
        ContentValues values = new ContentValues();
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_TASK_NAME, "Task");
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_DUE_DATE, "2000-01-01");
        long id = dbW.insert(TaskDBContract.TaskDB.TASK_TABLE_NAME,null, values);
        values.clear();
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_CATEGORY_NAME, categoryName);
        long catId = dbW.insert(TaskDBContract.TaskDB.CATEGORY_TABLE_NAME, null, values);
        values.clear();
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_TASK_ID, id);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_CATEGORY_ID, catId);
        dbW.insert(TaskDBContract.TaskDB.TASK_CATEGORY_TABLE_NAME, null, values);
        ArrayList<String> list = taskEditor.getCategories(String.valueOf(id));
        assertEquals(list.get(0), categoryName);
    }

    @Test
    public void testUpdateFields10_1(){
        String originalTitle = "task";
        String originalDate = "2000-01-01";
        int originalPriority = 1;
        int originalEstimation = 1;
        int originalChecked = 1;
        ContentValues values = new ContentValues();
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_TASK_NAME, originalTitle);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_DUE_DATE, originalDate);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_PRIORITY, originalPriority);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_ESTIMATED_MINS, originalEstimation);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_CHECKED, originalChecked);
        long id = dbW.insert(TaskDBContract.TaskDB.TASK_TABLE_NAME, null, values);
        taskEditor.updateFields(null, null, -1, -1, -1, (int) id);
        String[] projection = {
                TaskDBContract.TaskDB._ID,
                TaskDBContract.TaskDB.COLUMN_NAME_TASK_NAME,
                TaskDBContract.TaskDB.COLUMN_NAME_DUE_DATE,
                TaskDBContract.TaskDB.COLUMN_NAME_CHECKED,
                TaskDBContract.TaskDB.COLUMN_NAME_PRIORITY,
                TaskDBContract.TaskDB.COLUMN_NAME_ESTIMATED_MINS
        };
        String[] ID = {String.valueOf(id)};
        Cursor c = dbR.query(
                TaskDBContract.TaskDB.TASK_TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                TaskDBContract.TaskDB._ID + "=?",   // The columns for the WHERE clause
                ID,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                // The sort order
        );
        c.moveToFirst();
        assertEquals(originalTitle, c.getString(c.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_TASK_NAME)));
        assertEquals(originalDate, c.getString(c.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_DUE_DATE)));
        assertEquals(originalPriority, c.getInt(c.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_PRIORITY)));
        assertEquals(originalEstimation, c.getInt(c.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_ESTIMATED_MINS)));
        assertEquals(originalChecked, c.getInt(c.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_CHECKED)));
    }
    @Test
    public void testUpdateFields10_2(){
        String originalTitle = "task";
        String originalDate = "2000-01-01";
        int originalPriority = 1;
        int originalEstimation = 1;
        int originalChecked = 1;
        ContentValues values = new ContentValues();
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_TASK_NAME, originalTitle);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_DUE_DATE, originalDate);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_PRIORITY, originalPriority);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_ESTIMATED_MINS, originalEstimation);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_CHECKED, originalChecked);
        long id = dbW.insert(TaskDBContract.TaskDB.TASK_TABLE_NAME,null,values);
        String newTitle = "task1";
        String newDate = "2000-02-02";
        int newPriority = 2;
        int newEstimation = 2;
        int newChecked = 0;
        taskEditor.updateFields(newTitle, newDate, newPriority, newEstimation, newChecked, (int) id);
        String[] projection = {
                TaskDBContract.TaskDB._ID,
                TaskDBContract.TaskDB.COLUMN_NAME_TASK_NAME,
                TaskDBContract.TaskDB.COLUMN_NAME_DUE_DATE,
                TaskDBContract.TaskDB.COLUMN_NAME_CHECKED,
                TaskDBContract.TaskDB.COLUMN_NAME_PRIORITY,
                TaskDBContract.TaskDB.COLUMN_NAME_ESTIMATED_MINS
        };
        String[] ID = {String.valueOf(id)};
        Cursor c = dbR.query(
                TaskDBContract.TaskDB.TASK_TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                TaskDBContract.TaskDB._ID + "=?",   // The columns for the WHERE clause
                ID,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                // The sort order
        );
        c.moveToFirst();
        assertEquals(newTitle, c.getString(c.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_TASK_NAME)));
        assertEquals(newDate, c.getString(c.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_DUE_DATE)));
        assertEquals(newPriority, c.getInt(c.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_PRIORITY)));
        assertEquals(newEstimation, c.getInt(c.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_ESTIMATED_MINS)));
        assertEquals(newChecked, c.getInt(c.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_CHECKED)));
    }

    @Test
    public void testUpdateCategories11_1(){
        String originalTitle = "task";
        String originalDate = "2000-01-01";
        int originalPriority = 1;
        int originalEstimation = 1;
        int originalChecked = 1;
        ContentValues values = new ContentValues();
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_TASK_NAME, originalTitle);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_DUE_DATE, originalDate);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_PRIORITY, originalPriority);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_ESTIMATED_MINS, originalEstimation);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_CHECKED, originalChecked);
        long id = dbW.insert(TaskDBContract.TaskDB.TASK_TABLE_NAME,null,values);
        values.clear();
        String catName1 = "cat1";
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_CATEGORY_NAME, catName1);
        long catId = dbW.insert(TaskDBContract.TaskDB.CATEGORY_TABLE_NAME, null, values);
        values.clear();
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_TASK_ID, id);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_CATEGORY_ID, catId);
        dbW.insert(TaskDBContract.TaskDB.TASK_CATEGORY_TABLE_NAME, null, values);
        values.clear();
        String catName2 = "cat2";
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_CATEGORY_NAME, catName2);
        long catId2 = dbW.insert(TaskDBContract.TaskDB.CATEGORY_TABLE_NAME, null, values);
        values.clear();
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_TASK_ID, id);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_CATEGORY_ID, catId);
        dbW.insert(TaskDBContract.TaskDB.TASK_CATEGORY_TABLE_NAME, null, values);
        ArrayList<String> catNames = new ArrayList<>();
        catNames.add(catName1);
        catNames.add(catName2);
        taskEditor.updateCategories(new ArrayList<String>(), catNames, (int)id);
        String[] projection = {
                TaskDBContract.TaskDB._ID,
                TaskDBContract.TaskDB.COLUMN_NAME_TASK_ID,
                TaskDBContract.TaskDB.COLUMN_NAME_CATEGORY_ID
        };
        String[] ID = {String.valueOf(id)};
        Cursor c = dbR.query(
                TaskDBContract.TaskDB.TASK_CATEGORY_TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                TaskDBContract.TaskDB.COLUMN_NAME_TASK_ID + "=?",
                ID,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                // The sort order
        );
        c.moveToFirst();
        //should be empty
        assertTrue(c.isAfterLast());
    }

    @Test
    public void testUpdateCategories11_2() {
        String originalTitle = "task";
        String originalDate = "2000-01-01";
        int originalPriority = 1;
        int originalEstimation = 1;
        int originalChecked = 1;
        ContentValues values = new ContentValues();
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_TASK_NAME, originalTitle);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_DUE_DATE, originalDate);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_PRIORITY, originalPriority);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_ESTIMATED_MINS, originalEstimation);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_CHECKED, originalChecked);
        long id = dbW.insert(TaskDBContract.TaskDB.TASK_TABLE_NAME,null,values);
        String catName1 = "Cat1";
        ArrayList<String> names = new ArrayList<>();
        names.add(catName1);
        taskEditor.updateCategories(names, new ArrayList<String>(), (int) id);
        String tableName = TaskDBContract.TaskDB.CATEGORY_TABLE_NAME +
                " INNER JOIN " + TaskDBContract.TaskDB.TASK_CATEGORY_TABLE_NAME +
                " ON " +  TaskDBContract.TaskDB.COLUMN_NAME_CATEGORY_ID + " = " +
                TaskDBContract.TaskDB.CATEGORY_TABLE_NAME + "."
                + TaskDBContract.TaskDB._ID;
        String[] projection = {
                TaskDBContract.TaskDB.COLUMN_NAME_CATEGORY_NAME
        };
        String[] ID = {String.valueOf(id)};
        Cursor c = dbR.query(
                tableName,  // The table to query
                projection,                               // The columns to return
                TaskDBContract.TaskDB.COLUMN_NAME_TASK_ID + "=?",
                ID,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                // The sort order
        );
        c.moveToFirst();
        assertEquals(catName1, c.getString(c.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_CATEGORY_NAME)));
    }

    @Test
    public void testCreateTask12_1(){
        String taskName = "name";
        String dueDate = "2000-01-01";
        int priority = 1;
        int estimatedMins = 0;
        int basisId = -1;
        long id = taskEditor.createTask(taskName, dueDate, priority, estimatedMins, basisId);
        String[] projection = {
                TaskDBContract.TaskDB._ID,
                TaskDBContract.TaskDB.COLUMN_NAME_TASK_NAME,
                TaskDBContract.TaskDB.COLUMN_NAME_DUE_DATE,
                TaskDBContract.TaskDB.COLUMN_NAME_CHECKED,
                TaskDBContract.TaskDB.COLUMN_NAME_PRIORITY,
                TaskDBContract.TaskDB.COLUMN_NAME_ESTIMATED_MINS,
                TaskDBContract.TaskDB.COLUMN_NAME_REPEATING_BASIS
        };
        String[] ID = {String.valueOf(id)};
        Cursor c = dbR.query(
                TaskDBContract.TaskDB.TASK_TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                null,   // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                // The sort order
        );
        c.moveToFirst();
        assertEquals(taskName, c.getString(c.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_TASK_NAME)));
        assertEquals(dueDate, c.getString(c.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_DUE_DATE)));
        assertEquals(priority, c.getInt(c.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_PRIORITY)));
        assertEquals(estimatedMins, c.getInt(c.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_ESTIMATED_MINS)));
        assertEquals(0, c.getInt(c.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_CHECKED)));
        assertEquals(basisId, c.getInt(c.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_REPEATING_BASIS)));
        assertEquals((int)id, c.getInt(c.getColumnIndex(TaskDBContract.TaskDB._ID)));
    }

    @Test
    public void testGetTask13_1(){
        String originalTitle = "task";
        String originalDate = "2000-01-01";
        int originalPriority = 1;
        int originalEstimation = 1;
        int originalChecked = 1;
        int originalRepeating = -1;
        int originalIncompleteSub = 0;
        ContentValues values = new ContentValues();
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_TASK_NAME, originalTitle);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_DUE_DATE, originalDate);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_PRIORITY, originalPriority);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_ESTIMATED_MINS, originalEstimation);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_CHECKED, originalChecked);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_REPEATING_BASIS, originalRepeating);
        long id = dbW.insert(TaskDBContract.TaskDB.TASK_TABLE_NAME,null,values);
        Bundle taskBundle = taskEditor.getTask(String.valueOf(id));
        assertEquals(originalTitle, taskBundle.getString(TaskEditor.BUNDLE_TASK_NAME));
        assertEquals(originalDate,taskBundle.getString(TaskEditor.BUNDLE_DUE_DATE));
        assertEquals(originalPriority, taskBundle.getInt(TaskEditor.BUNDLE_PRIORITY));
        assertEquals(originalEstimation, taskBundle.getInt(TaskEditor.BUNDLE_ESTIMATION));
        assertEquals(originalChecked, taskBundle.getInt(TaskEditor.BUNDLE_CHECKED));
        assertEquals(originalRepeating, taskBundle.getInt(TaskEditor.BUNDLE_BASIS_ID));
        assertEquals(originalIncompleteSub, taskBundle.getInt(TaskEditor.BUNDLE_INCOMPLETE_SUB));
    }
    @Test
    public void testGetTask13_2(){
        Bundle taskBundle = taskEditor.getTask(String.valueOf(1));
        assertTrue(taskBundle.isEmpty());
    }

    @Test
    public void testGetSimpleTasks14_1(){
        String originalTitle = "task";
        String originalDate = "2000-01-01";
        int originalPriority = 1;
        int originalEstimation = 1;
        int originalChecked = 1;
        int originalRepeating = -1;
        int originalIncompleteSub = 0;
        ContentValues values = new ContentValues();
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_TASK_NAME, originalTitle);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_DUE_DATE, originalDate);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_PRIORITY, originalPriority);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_ESTIMATED_MINS, originalEstimation);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_CHECKED, originalChecked);
        long id = dbW.insert(TaskDBContract.TaskDB.TASK_TABLE_NAME, null, values);
        ArrayList<Bundle> tasks = taskEditor.getSimpleTasks(TaskDBContract.TaskDB._ID + "= ?", new String[]{String.valueOf(1)}, null);
        Bundle taskBundle = tasks.get(0);
        assertEquals(originalTitle, taskBundle.getString(TaskEditor.BUNDLE_TASK_NAME));
        assertEquals(originalDate,taskBundle.getString(TaskEditor.BUNDLE_DUE_DATE));
        assertEquals(originalPriority, taskBundle.getInt(TaskEditor.BUNDLE_PRIORITY));
        assertEquals(originalChecked, taskBundle.getInt(TaskEditor.BUNDLE_CHECKED));
        assertEquals(originalIncompleteSub, taskBundle.getInt(TaskEditor.BUNDLE_INCOMPLETE_SUB));
    }

    @Test
    public void testUpdateChecked21_1(){

        String originalTitle = "task";
        String originalDate = "2000-01-01";
        int originalPriority = 1;
        int originalEstimation = 1;
        int originalChecked = 1;
        int originalRepeating = -1;
        int originalIncompleteSub = 0;
        ContentValues values = new ContentValues();
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_TASK_NAME, originalTitle);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_DUE_DATE, originalDate);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_PRIORITY, originalPriority);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_ESTIMATED_MINS, originalEstimation);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_CHECKED, originalChecked);
        long id = dbW.insert(TaskDBContract.TaskDB.TASK_TABLE_NAME, null, values);

        taskEditor.updateChecked(2, 1);

        String[] projection = {
                TaskDBContract.TaskDB._ID,
                TaskDBContract.TaskDB.COLUMN_NAME_CHECKED
        };
        String[] ID = {String.valueOf(id)};
        Cursor c = dbR.query(
                TaskDBContract.TaskDB.TASK_TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                TaskDBContract.TaskDB._ID + "=?",   // The columns for the WHERE clause
                ID,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                // The sort order
        );
        c.moveToFirst();
        assertEquals(originalChecked, c.getInt(c.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_CHECKED)));
    }

    @Test
    public void testUpdateChecked21_2(){
        int newCheckedValue = 1;
        String originalTitle = "task";
        String originalDate = "2000-01-01";
        int originalPriority = 1;
        int originalEstimation = 1;
        int originalChecked = 0;
        int originalRepeating = -1;
        int originalIncompleteSub = 1;
        int newIncompleteSub = originalIncompleteSub-1;
        ContentValues values = new ContentValues();
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_TASK_NAME, originalTitle);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_DUE_DATE, originalDate);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_PRIORITY, originalPriority);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_ESTIMATED_MINS, originalEstimation);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_CHECKED, originalChecked);
        long id2 = dbW.insert(TaskDBContract.TaskDB.TASK_TABLE_NAME, null, values);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_INCOMPLETE_SUB, originalIncompleteSub);
        long id = dbW.insert(TaskDBContract.TaskDB.TASK_TABLE_NAME, null, values);
        values.clear();
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_PARENT_TASK, id);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_SUB_TASK, id2);
        dbW.insert(TaskDBContract.TaskDB.TASK_RELATION_TABLE_NAME, null, values);
        taskEditor.updateChecked((int)id2,newCheckedValue);

        String[] projection = {
                TaskDBContract.TaskDB._ID,
                TaskDBContract.TaskDB.COLUMN_NAME_CHECKED
        };
        String[] ID = {String.valueOf(id2)};
        Cursor c = dbR.query(
                TaskDBContract.TaskDB.TASK_TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                TaskDBContract.TaskDB._ID + "=?",   // The columns for the WHERE clause
                ID,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                // The sort order
        );
        c.moveToFirst();
        assertEquals(newCheckedValue, c.getInt(c.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_CHECKED)));

        projection = new String[]{
                TaskDBContract.TaskDB._ID,
                TaskDBContract.TaskDB.COLUMN_NAME_INCOMPLETE_SUB
        };
        ID = new String[]{String.valueOf(id)};
        c = dbR.query(
                TaskDBContract.TaskDB.TASK_TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                TaskDBContract.TaskDB._ID + "=?",   // The columns for the WHERE clause
                ID,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                // The sort order
        );
        c.moveToFirst();
        assertEquals(newIncompleteSub, c.getInt(c.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_INCOMPLETE_SUB)));
    }

    @Test
    public void testUpdateRelation15_1(){
        int newCheckedValue = 1;
        String originalTitle = "task";
        String originalDate = "2000-01-01";
        int originalPriority = 1;
        int originalEstimation = 1;
        int originalChecked = 1;
        int originalRepeating = -1;
        int originalIncompleteSub = 2;

        ContentValues values = new ContentValues();
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_TASK_NAME, originalTitle);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_DUE_DATE, originalDate);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_PRIORITY, originalPriority);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_ESTIMATED_MINS, originalEstimation);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_CHECKED, originalChecked);
        long id2 = dbW.insert(TaskDBContract.TaskDB.TASK_TABLE_NAME, null, values);
        long id3 = dbW.insert(TaskDBContract.TaskDB.TASK_TABLE_NAME, null, values);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_INCOMPLETE_SUB, originalIncompleteSub);
        long id = dbW.insert(TaskDBContract.TaskDB.TASK_TABLE_NAME, null, values);
        values.clear();
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_PARENT_TASK, id);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_SUB_TASK, id2);
        dbW.insert(TaskDBContract.TaskDB.TASK_RELATION_TABLE_NAME, null, values);
        values.clear();
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_PARENT_TASK, id);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_SUB_TASK, id3);
        dbW.insert(TaskDBContract.TaskDB.TASK_RELATION_TABLE_NAME, null, values);
        ArrayList<Integer> list = new ArrayList<>();
        list.add((int) id2);
        list.add((int) id3);
        int newIncompleteSub = 0;
        taskEditor.updateRelations(new ArrayList<Integer>(), list, (int) id, newIncompleteSub);
        String[] projection = new String[]{
                TaskDBContract.TaskDB._ID,
                TaskDBContract.TaskDB.COLUMN_NAME_INCOMPLETE_SUB
        };
        String[] ID = new String[]{String.valueOf(id)};
        Cursor c = dbR.query(
                TaskDBContract.TaskDB.TASK_TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                TaskDBContract.TaskDB._ID + "=?",   // The columns for the WHERE clause
                ID,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                // The sort order
        );
        c.moveToFirst();
        assertEquals(newIncompleteSub, c.getInt(c.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_INCOMPLETE_SUB)));

        projection = new String[]{
                TaskDBContract.TaskDB._ID,
                TaskDBContract.TaskDB.COLUMN_NAME_PARENT_TASK,
                TaskDBContract.TaskDB.COLUMN_NAME_SUB_TASK
        };
        ID = new String[]{String.valueOf(id)};
        c = dbR.query(
                TaskDBContract.TaskDB.TASK_RELATION_TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                TaskDBContract.TaskDB.COLUMN_NAME_PARENT_TASK + "=?",   // The columns for the WHERE clause
                ID,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                // The sort order
        );
        c.moveToFirst();
        assertTrue(c.isAfterLast());
    }

    @Test
    public void testUpdateRelation15_2() {
        int newCheckedValue = 1;
        String originalTitle = "task";
        String originalDate = "2000-01-01";
        int originalPriority = 1;
        int originalEstimation = 1;
        int originalChecked = 1;
        int originalRepeating = -1;
        int originalIncompleteSub = 0;

        ContentValues values = new ContentValues();
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_TASK_NAME, originalTitle);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_DUE_DATE, originalDate);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_PRIORITY, originalPriority);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_ESTIMATED_MINS, originalEstimation);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_CHECKED, originalChecked);
        long id2 = dbW.insert(TaskDBContract.TaskDB.TASK_TABLE_NAME, null, values);
        long id3 = dbW.insert(TaskDBContract.TaskDB.TASK_TABLE_NAME, null, values);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_INCOMPLETE_SUB, originalIncompleteSub);
        long id = dbW.insert(TaskDBContract.TaskDB.TASK_TABLE_NAME, null, values);
        ArrayList<Integer> list = new ArrayList<>();
        list.add((int) id2);
        list.add((int) id3);
        int newIncompleteSub = originalIncompleteSub +2;
        taskEditor.updateRelations(list, new ArrayList<Integer>(), (int) id, newIncompleteSub);
        String[] projection = new String[]{
                TaskDBContract.TaskDB._ID,
                TaskDBContract.TaskDB.COLUMN_NAME_INCOMPLETE_SUB
        };
        String[] ID = new String[]{String.valueOf(id)};
        Cursor c = dbR.query(
                TaskDBContract.TaskDB.TASK_TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                TaskDBContract.TaskDB._ID + "=?",   // The columns for the WHERE clause
                ID,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                // The sort order
        );
        c.moveToFirst();
        assertEquals(newIncompleteSub, c.getInt(c.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_INCOMPLETE_SUB)));
        projection = new String[]{
                TaskDBContract.TaskDB._ID,
                TaskDBContract.TaskDB.COLUMN_NAME_PARENT_TASK,
                TaskDBContract.TaskDB.COLUMN_NAME_SUB_TASK
        };
        ID = new String[]{String.valueOf(id)};
        c = dbR.query(
                TaskDBContract.TaskDB.TASK_RELATION_TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                TaskDBContract.TaskDB.COLUMN_NAME_PARENT_TASK + "=?",   // The columns for the WHERE clause
                ID,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                // The sort order
        );
        c.moveToFirst();
        ArrayList<Integer> result = new ArrayList<>();
        result.add(c.getInt(c.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_SUB_TASK)));
        c.moveToNext();
        result.add(c.getInt(c.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_SUB_TASK)));
        assertTrue(result.contains((int)id2));
        assertTrue(result.contains((int)id3));
    }

    @Test
    public void testRemoveAllRelation16_1(){
        int newCheckedValue = 1;
        String originalTitle = "task";
        String originalDate = "2000-01-01";
        int originalPriority = 1;
        int originalEstimation = 1;
        int originalChecked = 1;
        int originalRepeating = -1;
        int originalIncompleteSub = 2;
        int newIncompleteSub = originalIncompleteSub-1;
        ContentValues values = new ContentValues();
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_TASK_NAME, originalTitle);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_DUE_DATE, originalDate);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_PRIORITY, originalPriority);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_ESTIMATED_MINS, originalEstimation);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_CHECKED, originalChecked);
        long id2 = dbW.insert(TaskDBContract.TaskDB.TASK_TABLE_NAME, null, values);
        long id3 = dbW.insert(TaskDBContract.TaskDB.TASK_TABLE_NAME, null, values);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_INCOMPLETE_SUB, originalIncompleteSub);
        long id = dbW.insert(TaskDBContract.TaskDB.TASK_TABLE_NAME, null, values);
        values.clear();
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_PARENT_TASK, id);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_SUB_TASK, id2);
        dbW.insert(TaskDBContract.TaskDB.TASK_RELATION_TABLE_NAME, null, values);
        values.clear();
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_PARENT_TASK, id);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_SUB_TASK, id3);
        dbW.insert(TaskDBContract.TaskDB.TASK_RELATION_TABLE_NAME, null, values);

        taskEditor.removeAllRelations((int) id3);

        String[] projection = new String[]{
                TaskDBContract.TaskDB._ID,
                TaskDBContract.TaskDB.COLUMN_NAME_INCOMPLETE_SUB
        };
        String[] ID = new String[]{String.valueOf(id)};
        Cursor c = dbR.query(
                TaskDBContract.TaskDB.TASK_TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                TaskDBContract.TaskDB._ID + "=?",   // The columns for the WHERE clause
                ID,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                // The sort order
        );
        c.moveToFirst();
        assertEquals(newIncompleteSub, c.getInt(c.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_INCOMPLETE_SUB)));

        projection = new String[]{
                TaskDBContract.TaskDB._ID,
                TaskDBContract.TaskDB.COLUMN_NAME_PARENT_TASK,
                TaskDBContract.TaskDB.COLUMN_NAME_SUB_TASK
        };
        ID = new String[]{String.valueOf(id)};
        c = dbR.query(
                TaskDBContract.TaskDB.TASK_RELATION_TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                TaskDBContract.TaskDB.COLUMN_NAME_PARENT_TASK + "=?",   // The columns for the WHERE clause
                ID,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                // The sort order
        );
        c.moveToFirst();
        c.moveToNext();
        assertTrue(c.isAfterLast());
    }

    @Test
    public void testUpdateIncompleteSub17_1(){
        int newCheckedValue = 1;
        String originalTitle = "task";
        String originalDate = "2000-01-01";
        int originalPriority = 1;
        int originalEstimation = 1;
        int originalChecked = 1;
        int originalRepeating = -1;
        int originalIncompleteSub = 1;
        int newIncompleteSub = originalIncompleteSub-1;
        ContentValues values = new ContentValues();
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_TASK_NAME, originalTitle);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_DUE_DATE, originalDate);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_PRIORITY, originalPriority);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_ESTIMATED_MINS, originalEstimation);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_CHECKED, originalChecked);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_INCOMPLETE_SUB, originalIncompleteSub);
        long id = dbW.insert(TaskDBContract.TaskDB.TASK_TABLE_NAME, null, values);

        taskEditor.updateIncompleteSub(newIncompleteSub, (int)id);

        String[] projection = new String[]{
                TaskDBContract.TaskDB._ID,
                TaskDBContract.TaskDB.COLUMN_NAME_INCOMPLETE_SUB
        };
        String[] ID = new String[]{String.valueOf(id)};
        Cursor c = dbR.query(
                TaskDBContract.TaskDB.TASK_TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                TaskDBContract.TaskDB._ID + "=?",   // The columns for the WHERE clause
                ID,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                // The sort order
        );
        c.moveToFirst();
        assertEquals(newIncompleteSub, c.getInt(c.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_INCOMPLETE_SUB)));
    }

    @Test
    public void testGetIncomleteSub18_1(){
        int newCheckedValue = 1;
        String originalTitle = "task";
        String originalDate = "2000-01-01";
        int originalPriority = 1;
        int originalEstimation = 1;
        int originalChecked = 1;
        int originalRepeating = -1;
        int originalIncompleteSub = 1;
        ContentValues values = new ContentValues();
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_TASK_NAME, originalTitle);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_DUE_DATE, originalDate);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_PRIORITY, originalPriority);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_ESTIMATED_MINS, originalEstimation);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_CHECKED, originalChecked);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_INCOMPLETE_SUB, originalIncompleteSub);
        long id = dbW.insert(TaskDBContract.TaskDB.TASK_TABLE_NAME, null, values);

        assertEquals(originalIncompleteSub,taskEditor.getIncompleteSub((int) id));
    }

    @Test
    public void testGetIncomleteSub18_2(){
        int newCheckedValue = 1;
        String originalTitle = "task";
        String originalDate = "2000-01-01";
        int originalPriority = 1;
        int originalEstimation = 1;
        int originalChecked = 1;
        int originalRepeating = -1;
        int originalIncompleteSub = 1;
        ContentValues values = new ContentValues();
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_TASK_NAME, originalTitle);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_DUE_DATE, originalDate);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_PRIORITY, originalPriority);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_ESTIMATED_MINS, originalEstimation);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_CHECKED, originalChecked);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_INCOMPLETE_SUB, originalIncompleteSub);
        long id = dbW.insert(TaskDBContract.TaskDB.TASK_TABLE_NAME, null, values);

        assertEquals(0,taskEditor.getIncompleteSub(2));
    }

    @Test
    public void testGetParentIds19_1(){
        int newCheckedValue = 1;
        String originalTitle = "task";
        String originalDate = "2000-01-01";
        int originalPriority = 1;
        int originalEstimation = 1;
        int originalChecked = 1;
        int originalRepeating = -1;
        int originalIncompleteSub = 1;
        int newIncompleteSub = originalIncompleteSub-1;
        ContentValues values = new ContentValues();
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_TASK_NAME, originalTitle);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_DUE_DATE, originalDate);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_PRIORITY, originalPriority);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_ESTIMATED_MINS, originalEstimation);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_CHECKED, originalChecked);
        long id2 = dbW.insert(TaskDBContract.TaskDB.TASK_TABLE_NAME, null, values);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_INCOMPLETE_SUB, originalIncompleteSub);
        long id = dbW.insert(TaskDBContract.TaskDB.TASK_TABLE_NAME, null, values);
        values.clear();
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_PARENT_TASK, id);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_SUB_TASK, id2);
        dbW.insert(TaskDBContract.TaskDB.TASK_RELATION_TABLE_NAME, null, values);

        assertEquals((int)id, (int)taskEditor.getParentIds((int)id2).get(0));
    }

    @Test
    public void testGetSubIds20_1(){
        int newCheckedValue = 1;
        String originalTitle = "task";
        String originalDate = "2000-01-01";
        int originalPriority = 1;
        int originalEstimation = 1;
        int originalChecked = 1;
        int originalRepeating = -1;
        int originalIncompleteSub = 1;
        int newIncompleteSub = originalIncompleteSub-1;
        ContentValues values = new ContentValues();
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_TASK_NAME, originalTitle);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_DUE_DATE, originalDate);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_PRIORITY, originalPriority);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_ESTIMATED_MINS, originalEstimation);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_CHECKED, originalChecked);
        long id2 = dbW.insert(TaskDBContract.TaskDB.TASK_TABLE_NAME, null, values);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_INCOMPLETE_SUB, originalIncompleteSub);
        long id = dbW.insert(TaskDBContract.TaskDB.TASK_TABLE_NAME, null, values);
        values.clear();
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_PARENT_TASK, id);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_SUB_TASK, id2);
        dbW.insert(TaskDBContract.TaskDB.TASK_RELATION_TABLE_NAME, null, values);

        assertEquals((int)id2, (int)taskEditor.getSubIds((int) id).get(0));
    }

    @Test
    public void testGetSubTasks22_1(){
        int newCheckedValue = 1;
        String originalTitle = "task";
        String originalDate = "2000-01-01";
        int originalPriority = 1;
        int originalEstimation = 1;
        int originalChecked = 1;
        int originalRepeating = -1;
        int originalIncompleteSub = 1;
        int newIncompleteSub = originalIncompleteSub-1;
        ContentValues values = new ContentValues();
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_TASK_NAME, originalTitle);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_DUE_DATE, originalDate);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_PRIORITY, originalPriority);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_ESTIMATED_MINS, originalEstimation);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_CHECKED, originalChecked);
        long id2 = dbW.insert(TaskDBContract.TaskDB.TASK_TABLE_NAME, null, values);
        long id3 = dbW.insert(TaskDBContract.TaskDB.TASK_TABLE_NAME, null, values);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_INCOMPLETE_SUB, originalIncompleteSub);
        long id = dbW.insert(TaskDBContract.TaskDB.TASK_TABLE_NAME, null, values);
        values.clear();
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_PARENT_TASK, id);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_SUB_TASK, id2);
        dbW.insert(TaskDBContract.TaskDB.TASK_RELATION_TABLE_NAME, null, values);
        values.clear();
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_PARENT_TASK, id);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_SUB_TASK, id3);
        dbW.insert(TaskDBContract.TaskDB.TASK_RELATION_TABLE_NAME, null, values);

        ArrayList<Bundle> taskBundles = taskEditor.getSubTasks((int)id);

        ArrayList<Integer> subIds = new ArrayList<>();
        subIds.add(taskBundles.get(0).getInt(TaskEditor.BUNDLE_TASK_ID));
        subIds.add(taskBundles.get(1).getInt(TaskEditor.BUNDLE_TASK_ID));
        assertTrue(subIds.contains((int) id2));
        assertTrue(subIds.contains((int) id3));
    }

    @Test
    public void testGetSubTasks22_2() {
        int newCheckedValue = 1;
        String originalTitle = "task";
        String originalDate = "2000-01-01";
        int originalPriority = 1;
        int originalEstimation = 1;
        int originalChecked = 1;
        int originalRepeating = -1;
        int originalIncompleteSub = 1;
        int newIncompleteSub = originalIncompleteSub - 1;
        ContentValues values = new ContentValues();
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_TASK_NAME, originalTitle);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_DUE_DATE, originalDate);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_PRIORITY, originalPriority);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_ESTIMATED_MINS, originalEstimation);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_CHECKED, originalChecked);
        long id2 = dbW.insert(TaskDBContract.TaskDB.TASK_TABLE_NAME, null, values);
        long id3 = dbW.insert(TaskDBContract.TaskDB.TASK_TABLE_NAME, null, values);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_INCOMPLETE_SUB, originalIncompleteSub);
        long id = dbW.insert(TaskDBContract.TaskDB.TASK_TABLE_NAME, null, values);
        values.clear();
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_PARENT_TASK, id);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_SUB_TASK, id2);
        dbW.insert(TaskDBContract.TaskDB.TASK_RELATION_TABLE_NAME, null, values);
        values.clear();
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_PARENT_TASK, id);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_SUB_TASK, id3);
        dbW.insert(TaskDBContract.TaskDB.TASK_RELATION_TABLE_NAME, null, values);

        ArrayList<Bundle> taskBundles = taskEditor.getSubTasks(70);
        assertEquals(taskBundles.size(), 0);
    }

    @Test
    public void testDeleteTask23_1(){
        String categoryName = "category";
        int newCheckedValue = 1;
        String originalTitle = "task";
        String originalDate = "2000-01-01";
        int originalPriority = 1;
        int originalEstimation = 1;
        int originalChecked = 0;
        int originalRepeating = -1;
        int originalIncompleteSub = 1;
        int newIncompleteSub = originalIncompleteSub-1;
        ContentValues values = new ContentValues();
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_TASK_NAME, originalTitle);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_DUE_DATE, originalDate);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_PRIORITY, originalPriority);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_ESTIMATED_MINS, originalEstimation);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_CHECKED, originalChecked);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_REPEATING_BASIS, originalRepeating);
        long id2 = dbW.insert(TaskDBContract.TaskDB.TASK_TABLE_NAME, null, values);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_INCOMPLETE_SUB, originalIncompleteSub);
        long id = dbW.insert(TaskDBContract.TaskDB.TASK_TABLE_NAME, null, values);
        values.clear();
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_PARENT_TASK, id);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_SUB_TASK, id2);
        dbW.insert(TaskDBContract.TaskDB.TASK_RELATION_TABLE_NAME, null, values);
        values.clear();
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_CATEGORY_NAME, categoryName);
        long catId = dbW.insert(TaskDBContract.TaskDB.CATEGORY_TABLE_NAME, null, values);
        values.clear();
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_TASK_ID, id2);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_CATEGORY_ID, catId);
        dbW.insert(TaskDBContract.TaskDB.TASK_CATEGORY_TABLE_NAME, null, values);

        taskEditor.deleteTask((int)id2);

        String[] projection = new String[]{
                TaskDBContract.TaskDB._ID,
                TaskDBContract.TaskDB.COLUMN_NAME_INCOMPLETE_SUB
        };
        String[] ID = new String[]{String.valueOf(id)};
        Cursor c = dbR.query(
                TaskDBContract.TaskDB.TASK_TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                TaskDBContract.TaskDB._ID + "=?",   // The columns for the WHERE clause
                ID,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                // The sort order
        );
        c.moveToFirst();
        assertEquals(newIncompleteSub, c.getInt(c.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_INCOMPLETE_SUB)));

        projection = new String[]{
                TaskDBContract.TaskDB._ID,
                TaskDBContract.TaskDB.COLUMN_NAME_INCOMPLETE_SUB
        };
         ID = new String[]{String.valueOf(id2)};
        c = dbR.query(
                TaskDBContract.TaskDB.TASK_TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                TaskDBContract.TaskDB._ID + "=?",   // The columns for the WHERE clause
                ID,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                // The sort order
        );
        c.moveToFirst();
        assertTrue(c.isAfterLast());

        projection = new String[]{
                TaskDBContract.TaskDB._ID,
                TaskDBContract.TaskDB.COLUMN_NAME_PARENT_TASK,
                TaskDBContract.TaskDB.COLUMN_NAME_SUB_TASK
        };
        ID = new String[]{String.valueOf(id)};
        c = dbR.query(
                TaskDBContract.TaskDB.TASK_RELATION_TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                TaskDBContract.TaskDB.COLUMN_NAME_PARENT_TASK + "=?",   // The columns for the WHERE clause
                ID,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                // The sort order
        );
        c.moveToFirst();
        assertTrue(c.isAfterLast());

        projection = new String[]{
                TaskDBContract.TaskDB._ID,
                TaskDBContract.TaskDB.COLUMN_NAME_TASK_ID,
                TaskDBContract.TaskDB.COLUMN_NAME_CATEGORY_ID
        };
        ID = new String[]{String.valueOf(id2)};
        c = dbR.query(
                TaskDBContract.TaskDB.TASK_CATEGORY_TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                TaskDBContract.TaskDB.COLUMN_NAME_TASK_ID + "=?",
                ID,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                // The sort order
        );
        c.moveToFirst();
        //should be empty
        assertTrue(c.isAfterLast());
    }

    @Test
    public void testGetTasksDateAscending(){
        String originalTitle = "task";
        String originalDate1 = "2015-01-01";
        String originalDate2 = "2015-01-02";
        int originalPriority = 1;
        int originalEstimation = 1;
        int originalChecked = 1;
        ContentValues values = new ContentValues();
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_TASK_NAME, originalTitle);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_DUE_DATE, originalDate1);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_PRIORITY, originalPriority);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_ESTIMATED_MINS, originalEstimation);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_CHECKED, originalChecked);
        long id = dbW.insert(TaskDBContract.TaskDB.TASK_TABLE_NAME,null,values);
        values.remove(TaskDBContract.TaskDB.COLUMN_NAME_DUE_DATE);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_DUE_DATE, originalDate2);
        long id2 = dbW.insert(TaskDBContract.TaskDB.TASK_TABLE_NAME,null,values);

        ArrayList<Bundle> list = taskEditor.getTasksDateAcceding();

        assertEquals(originalDate1, list.get(0).getString(TaskEditor.BUNDLE_DUE_DATE));
        assertEquals(originalDate2, list.get(1).getString(TaskEditor.BUNDLE_DUE_DATE));
    }

}