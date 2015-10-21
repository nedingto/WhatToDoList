package com.example.nick.whattodolist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import java.util.ArrayList;

/**
 * Created by Nick on 10/18/2015.
 */
public class TaskEditor {
    TaskDBContract dbContract;
    TaskDbHelper mDbHelper;
    SQLiteDatabase dbR;
    SQLiteDatabase dbW;

    public final static String BUNDLE_TASK_ID = "task_id";
    public final static String BUNDLE_TASK_NAME = "task_name";
    public final static String BUNDLE_DUE_DATE = "due_date";
    public final static String BUNDLE_CHECKED = "checked";
    public final static String BUNDLE_PRIORITY = "priority";
    public final static String BUNDLE_ESTIMATION = "estimation";
    public final static String BUNDLE_BASIS_ID = "basis_id";


    public TaskEditor(Context context){
        dbContract = new TaskDBContract();
        mDbHelper = new TaskDbHelper(context);

        //set up reader and writer
        dbR = mDbHelper.getReadableDatabase();
        dbW = mDbHelper.getWritableDatabase();
    }

    public ArrayList<String>  getCategories(String taskId){

        //gets the categories associated with the given task id
        String table = TaskDBContract.TaskDB.CATEGORY_TABLE_NAME +
                " INNER JOIN " + TaskDBContract.TaskDB.TASK_CATEGORY_TABLE_NAME +
                " ON " +  TaskDBContract.TaskDB.COLUMN_NAME_CATEGORY_ID + " = " +
                TaskDBContract.TaskDB.CATEGORY_TABLE_NAME + "."
                + TaskDBContract.TaskDB._ID;

        String[]projection = new String[]{
                TaskDBContract.TaskDB.COLUMN_NAME_TASK_ID,
                TaskDBContract.TaskDB.COLUMN_NAME_CATEGORY_NAME
        };

        String[] ID = new String[]{taskId};
        Cursor c2 = dbR.query(
                table,  // The table to query
                projection,                               // The columns to return
                TaskDBContract.TaskDB.COLUMN_NAME_TASK_ID + "=?",                                // The columns for the WHERE clause
                ID,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                // The sort order
        );
        c2.moveToFirst();

        ArrayList<String> list = new ArrayList<>();
        while(!c2.isAfterLast()){
            list.add(c2.getString(c2.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_CATEGORY_NAME)));
            c2.moveToNext();
        }
        return list;
    }

    public void updateCategories(ArrayList<String> added, ArrayList<String> deleted, int taskId){
        //removes the deleted categories and adds the added categories to te give task
        String selection;
        String[] selectionArgs = { String.valueOf(taskId) };

        if (!deleted.isEmpty()) {
            //set up the field

            String table = TaskDBContract.TaskDB.CATEGORY_TABLE_NAME +
                    " INNER JOIN " + TaskDBContract.TaskDB.TASK_CATEGORY_TABLE_NAME +
                    " ON (" + TaskDBContract.TaskDB.COLUMN_NAME_CATEGORY_ID + " = " +
                    TaskDBContract.TaskDB.CATEGORY_TABLE_NAME + "."
                    + TaskDBContract.TaskDB._ID + ")";

            selection = TaskDBContract.TaskDB.COLUMN_NAME_TASK_ID + " = " + taskId + " AND (";
            for (int i = 0; i < deleted.size(); i++) {
                selection += TaskDBContract.TaskDB.COLUMN_NAME_CATEGORY_NAME + " = '";
                selection += deleted.get(i);
                if (i < deleted.size() - 1) selection += "' OR ";
            }
            selection += "')";

            String fullSelection = TaskDBContract.TaskDB._ID + " IN (SELECT " +
                    TaskDBContract.TaskDB.TASK_CATEGORY_TABLE_NAME + "." +
                    TaskDBContract.TaskDB._ID + " FROM " + table + " WHERE " + selection + ")";

            dbR.delete(TaskDBContract.TaskDB.TASK_CATEGORY_TABLE_NAME, fullSelection, null);


        }

        if (!added.isEmpty()) {
            String[] projection = {
                    TaskDBContract.TaskDB._ID,
                    TaskDBContract.TaskDB.COLUMN_NAME_CATEGORY_NAME
            };
            //if the category exists, just add its id and the tasks id to the task_category table
            //otherwise create the category and then add their ids
            for (int i = 0; i < added.size(); i++) {
                Cursor c = dbR.query(
                        TaskDBContract.TaskDB.CATEGORY_TABLE_NAME,  // The table to query
                        projection,                               // The columns to return
                        TaskDBContract.TaskDB.COLUMN_NAME_CATEGORY_NAME + "=?",                                // The columns for the WHERE clause
                        new String[]{added.get(i)},                            // The values for the WHERE clause
                        null,                                     // don't group the rows
                        null,                                     // don't filter by row groups
                        null                                 // The sort order
                );
                if (c.getCount() > 0) {
                    c.moveToFirst();
                    int categoryRowId = c.getInt(c.getColumnIndexOrThrow(TaskDBContract.TaskDB._ID));
                    ContentValues values = new ContentValues();
                    values.put(TaskDBContract.TaskDB.COLUMN_NAME_TASK_ID, taskId);
                    values.put(TaskDBContract.TaskDB.COLUMN_NAME_CATEGORY_ID, categoryRowId);
                    long newTaskCategoryRowId = dbW.insert(
                            TaskDBContract.TaskDB.TASK_CATEGORY_TABLE_NAME,
                            null,
                            values
                    );

                } else {
                    ContentValues values = new ContentValues();
                    values.put(TaskDBContract.TaskDB.COLUMN_NAME_CATEGORY_NAME, added.get(i));
                    long newCategoryRowId = dbW.insert(
                            TaskDBContract.TaskDB.CATEGORY_TABLE_NAME,
                            null,
                            values
                    );
                    values = new ContentValues();
                    values.put(TaskDBContract.TaskDB.COLUMN_NAME_TASK_ID, taskId);
                    values.put(TaskDBContract.TaskDB.COLUMN_NAME_CATEGORY_ID, newCategoryRowId);
                    long newTaskCategoryRowId = dbW.insert(
                            TaskDBContract.TaskDB.TASK_CATEGORY_TABLE_NAME,
                            null,
                            values
                    );
                }
            }

        }
    }

    public void updateFields(String titleString, String dueDateString, int priorityInt,
                             int estimationInt,int checkedInt, int taskId){
        //this will update the fields of a give task
        ContentValues values = new ContentValues();
        //possible to leave some fields the same by leaving it null or -1 in the case of int
        if (titleString != null)values.put(TaskDBContract.TaskDB.COLUMN_NAME_TASK_NAME, titleString);
        if (dueDateString != null)values.put(TaskDBContract.TaskDB.COLUMN_NAME_DUE_DATE, dueDateString);
        if (priorityInt != -1)values.put(TaskDBContract.TaskDB.COLUMN_NAME_PRIORITY, priorityInt);
        if (estimationInt != -1)values.put(TaskDBContract.TaskDB.COLUMN_NAME_ESTIMATED_MINS, estimationInt);
        if (checkedInt != -1)values.put(TaskDBContract.TaskDB.COLUMN_NAME_CHECKED, checkedInt);

        String selection = TaskDBContract.TaskDB._ID+ " LIKE ?";
        String[] selectionArgs = { String.valueOf(taskId) };

        int cout = dbR.update(
                TaskDBContract.TaskDB.TASK_TABLE_NAME,
                values,
                selection,
                selectionArgs
        );
    }

    public void deleteTask (int taskId){
        //will delete task based on the id given
        String table = TaskDBContract.TaskDB.TASK_TABLE_NAME +
                " INNER JOIN " + TaskDBContract.TaskDB.TASK_CATEGORY_TABLE_NAME +
                " ON (" + TaskDBContract.TaskDB.COLUMN_NAME_TASK_ID + " = " +
                TaskDBContract.TaskDB.TASK_TABLE_NAME + "."
                + TaskDBContract.TaskDB._ID + ")";

        String selection = TaskDBContract.TaskDB.TASK_TABLE_NAME + "." +
                TaskDBContract.TaskDB._ID + " = " + taskId;

        String fullSelection = TaskDBContract.TaskDB._ID + " IN (SELECT " +
                TaskDBContract.TaskDB.TASK_CATEGORY_TABLE_NAME + "." +
                TaskDBContract.TaskDB._ID + " FROM " + table + " WHERE " + selection + ")";

        dbR.delete(TaskDBContract.TaskDB.TASK_CATEGORY_TABLE_NAME, fullSelection, null);


        selection = TaskDBContract.TaskDB._ID + " = " + taskId;
        dbR.delete(TaskDBContract.TaskDB.TASK_TABLE_NAME, selection, null);
    }

    public int createTask(String taskName, String dueDate, int priority, int estimation, int basisId){
        //creates a task and returns the id of it
        ContentValues values = new ContentValues();
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_TASK_NAME,taskName);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_DUE_DATE,dueDate);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_CHECKED, 0);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_PRIORITY, priority);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_ESTIMATED_MINS, estimation);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_REPEATING_BASIS, basisId);
        long newTaskRowId;
        newTaskRowId = dbW.insert(
                TaskDBContract.TaskDB.TASK_TABLE_NAME,
                null,
                values);
        return (int) newTaskRowId;
    }

    public Bundle getTask(String taskId){
        Bundle taskBundle = new Bundle();

        //query creation
        String[] projection = {
                TaskDBContract.TaskDB._ID,
                TaskDBContract.TaskDB.COLUMN_NAME_TASK_NAME,
                TaskDBContract.TaskDB.COLUMN_NAME_DUE_DATE,
                TaskDBContract.TaskDB.COLUMN_NAME_CHECKED,
                TaskDBContract.TaskDB.COLUMN_NAME_PRIORITY,
                TaskDBContract.TaskDB.COLUMN_NAME_ESTIMATED_MINS,
                TaskDBContract.TaskDB.COLUMN_NAME_REPEATING_BASIS
        };

        String[] ID = {taskId};
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
        if(c.isAfterLast()){
            //nothing will be returned
        } else{
            String taskName = c.getString(c.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_TASK_NAME));
            taskBundle.putString(BUNDLE_TASK_NAME, taskName);

            String dueDate = c.getString(c.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_DUE_DATE));
            taskBundle.putString(BUNDLE_DUE_DATE, dueDate);

            int priority = c.getInt(c.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_PRIORITY));
            taskBundle.putInt(BUNDLE_PRIORITY, priority);

            int estimation = c.getInt(c.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_ESTIMATED_MINS));
            taskBundle.putInt(BUNDLE_ESTIMATION, estimation);

            int checked = c.getInt(c.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_CHECKED));
            taskBundle.putInt(BUNDLE_CHECKED, checked);

            int basisId = c.getInt(c.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_REPEATING_BASIS));
            taskBundle.putInt(BUNDLE_BASIS_ID, basisId);
        }
        return taskBundle;
    }

    public ArrayList<Bundle> getTasksDateAcceding(){
        ArrayList<Bundle> tasks = new ArrayList<>();
        //query creation
        String[] projection = {
                TaskDBContract.TaskDB._ID,
                TaskDBContract.TaskDB.COLUMN_NAME_TASK_NAME,
                TaskDBContract.TaskDB.COLUMN_NAME_DUE_DATE,
                TaskDBContract.TaskDB.COLUMN_NAME_CHECKED,
                TaskDBContract.TaskDB.COLUMN_NAME_PRIORITY,
        };
        String sortOrder =
                "date(" + TaskDBContract.TaskDB.COLUMN_NAME_DUE_DATE + ") ASC";

        Cursor c = dbR.query(
                TaskDBContract.TaskDB.TASK_TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );
        return getSimpleTasks(c);
    }
    private ArrayList<Bundle> getSimpleTasks(Cursor c){
        ArrayList<Bundle> tasks = new ArrayList<>();
        c.moveToFirst();
        while(!c.isAfterLast()){
            Bundle simpleBundle = new Bundle();

            int taskId = c.getInt(c.getColumnIndex(TaskDBContract.TaskDB._ID));
            simpleBundle.putInt(BUNDLE_TASK_ID, taskId);

            String taskName = c.getString(c.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_TASK_NAME));
            simpleBundle.putString(BUNDLE_TASK_NAME, taskName);

            String dueDate = c.getString(c.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_DUE_DATE));
            simpleBundle.putString(BUNDLE_DUE_DATE, dueDate);

            int priority = c.getInt(c.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_PRIORITY));
            simpleBundle.putInt(BUNDLE_PRIORITY, priority);

            int checked = c.getInt(c.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_CHECKED));
            simpleBundle.putInt(BUNDLE_CHECKED, checked);

            tasks.add(simpleBundle);

            c.moveToNext();
        }
        return tasks;
    }

    public void updateChecked(int taskId, int value){
        ContentValues values = new ContentValues();
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_CHECKED, value);
        String selection = TaskDBContract.TaskDB._ID + " LIKE ?";
        String[] selectionArgs = {String.valueOf(taskId)};
        int cout = dbR.update(
                TaskDBContract.TaskDB.TASK_TABLE_NAME,
                values,
                selection,
                selectionArgs
        );
    }
}
