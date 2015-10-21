package com.example.nick.whattodolist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by Nick on 10/18/2015.
 */
public class TaskEditor {
    TaskDBContract dbContract;
    TaskDbHelper mDbHelper;
    SQLiteDatabase dbR;
    SQLiteDatabase dbW;

    public TaskEditor(Context context){
        dbContract = new TaskDBContract();
        mDbHelper = new TaskDbHelper(context);

        //set up reader and writer
        dbR = mDbHelper.getReadableDatabase();
        dbW = mDbHelper.getWritableDatabase();
    }

    public static void updateCategories(ArrayList<String> added, ArrayList<String> deleted,
                                        int taskId, SQLiteDatabase dbR, SQLiteDatabase dbW){
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
}
