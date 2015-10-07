package com.example.nick.whattodolist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Nick on 9/21/2015.
 */
public class TaskDbHelper extends SQLiteOpenHelper {
    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_TASK_TABLE =
            "CREATE TABLE " + TaskDBContract.TaskDB.TASK_TABLE_NAME + " (" +
                    TaskDBContract.TaskDB._ID + " INTEGER PRIMARY KEY," +
                    TaskDBContract.TaskDB.COLUMN_NAME_TASK_NAME + TEXT_TYPE + COMMA_SEP +
                    TaskDBContract.TaskDB.COLUMN_NAME_DUE_DATE + TEXT_TYPE + COMMA_SEP +
                    TaskDBContract.TaskDB.COLUMN_NAME_CHECKED + INTEGER_TYPE + COMMA_SEP +
                    TaskDBContract.TaskDB.COLUMN_NAME_PRIORITY + INTEGER_TYPE + COMMA_SEP +
                    TaskDBContract.TaskDB.COLUMN_NAME_ESTIMATED_MINS + INTEGER_TYPE +
                    ")";
    private static final String SQL_CREATE_CATEGORY_TABLE =
            "CREATE TABLE " + TaskDBContract.TaskDB.CATEGORY_TABLE_NAME + " (" +
                    TaskDBContract.TaskDB._ID + " INTEGER PRIMARY KEY," +
                    TaskDBContract.TaskDB.COLUMN_NAME_CATEGORY_NAME + TEXT_TYPE + ")";
    private static final String SQL_CREATE_TASK_CATEGORY_TABLE =
            "CREATE TABLE " + TaskDBContract.TaskDB.TASK_CATEGORY_TABLE_NAME + " (" +
                    TaskDBContract.TaskDB._ID + " INTEGER PRIMARY KEY," +
                    TaskDBContract.TaskDB.COLUMN_NAME_TASK_ID + INTEGER_TYPE + COMMA_SEP +
                    TaskDBContract.TaskDB.COLUMN_NAME_CATEGORY_ID + INTEGER_TYPE + ")";

    private static final String SQL_DELETE_TASK_TABLE =
            "DROP TABLE IF EXISTS " + TaskDBContract.TaskDB.TASK_TABLE_NAME;

    private static final String SQL_DELETE_CATEGORY_TABLE =
            "DROP TABLE IF EXISTS " + TaskDBContract.TaskDB.CATEGORY_TABLE_NAME;

    private static final String SQL_DELETE_TASK_CATEGORY_TABLE =
            "DROP TABLE IF EXISTS " + TaskDBContract.TaskDB.TASK_CATEGORY_TABLE_NAME;

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 13;
    public static final String DATABASE_NAME = "Task.db";

    public TaskDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TASK_TABLE);
        db.execSQL(SQL_CREATE_CATEGORY_TABLE);
        db.execSQL(SQL_CREATE_TASK_CATEGORY_TABLE);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_TASK_TABLE);
        db.execSQL(SQL_DELETE_CATEGORY_TABLE);
        db.execSQL(SQL_DELETE_TASK_CATEGORY_TABLE);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}
