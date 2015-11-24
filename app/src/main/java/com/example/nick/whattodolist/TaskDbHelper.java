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
                    TaskDBContract.TaskDB.COLUMN_NAME_ESTIMATED_MINS + INTEGER_TYPE + COMMA_SEP +
                    TaskDBContract.TaskDB.COLUMN_NAME_REPEATING_BASIS + INTEGER_TYPE + COMMA_SEP +
                    TaskDBContract.TaskDB.COLUMN_NAME_ASSIGNED_DATE + TEXT_TYPE + COMMA_SEP +
                    TaskDBContract.TaskDB.COLUMN_NAME_INCOMPLETE_SUB + INTEGER_TYPE +
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

    private static final String SQL_CREATE_REPEATING_TABLE =
            "CREATE TABLE " + TaskDBContract.TaskDB.REPEATING_TABLE_NAME + " (" +
                    TaskDBContract.TaskDB._ID + " INTEGER PRIMARY KEY," +
                    TaskDBContract.TaskDB.COLUMN_NAME_PERIODICAL_NUM + INTEGER_TYPE + COMMA_SEP +
                    TaskDBContract.TaskDB.COLUMN_NAME_PERIODICAL_PERIOD + TEXT_TYPE + COMMA_SEP +
                    TaskDBContract.TaskDB.COLUMN_NAME_ORDINAL_NUM + INTEGER_TYPE + COMMA_SEP +
                    TaskDBContract.TaskDB.COLUMN_NAME_ORDINAL_PERIOD + TEXT_TYPE + COMMA_SEP +
                    TaskDBContract.TaskDB.COLUMN_NAME_SUNDAY + INTEGER_TYPE + COMMA_SEP +
                    TaskDBContract.TaskDB.COLUMN_NAME_MONDAY + INTEGER_TYPE + COMMA_SEP +
                    TaskDBContract.TaskDB.COLUMN_NAME_TUESDAY + INTEGER_TYPE + COMMA_SEP +
                    TaskDBContract.TaskDB.COLUMN_NAME_WEDNESDAY + INTEGER_TYPE + COMMA_SEP +
                    TaskDBContract.TaskDB.COLUMN_NAME_THURSDAY + INTEGER_TYPE + COMMA_SEP +
                    TaskDBContract.TaskDB.COLUMN_NAME_FRIDAY + INTEGER_TYPE + COMMA_SEP +
                    TaskDBContract.TaskDB.COLUMN_NAME_SATURDAY + INTEGER_TYPE + COMMA_SEP +
                    TaskDBContract.TaskDB.COLUMN_NAME_START_DATE + TEXT_TYPE + COMMA_SEP +
                    TaskDBContract.TaskDB.COLUMN_NAME_END_DATE + TEXT_TYPE + ")";


       private static final String SQL_CREATE_TASK_RELATION_TABLE =
               "CREATE TABLE " + TaskDBContract.TaskDB.TASK_RELATION_TABLE_NAME + " (" +
                       TaskDBContract.TaskDB._ID + " INTEGER PRIMARY KEY," +
                    TaskDBContract.TaskDB.COLUMN_NAME_PARENT_TASK + INTEGER_TYPE + COMMA_SEP +
                    TaskDBContract.TaskDB.COLUMN_NAME_SUB_TASK + INTEGER_TYPE + ")";


    private static final String SQL_DELETE_TASK_TABLE =
            "DROP TABLE IF EXISTS " + TaskDBContract.TaskDB.TASK_TABLE_NAME;

    private static final String SQL_DELETE_CATEGORY_TABLE =
            "DROP TABLE IF EXISTS " + TaskDBContract.TaskDB.CATEGORY_TABLE_NAME;

    private static final String SQL_DELETE_TASK_CATEGORY_TABLE =
            "DROP TABLE IF EXISTS " + TaskDBContract.TaskDB.TASK_CATEGORY_TABLE_NAME;

    private static final String SQL_DELETE_REPEATING_TABLE =
            "DROP TABLE IF EXISTS " + TaskDBContract.TaskDB.REPEATING_TABLE_NAME;

    private static final String SQL_DELETE_TASK_RELATION_TABLE =
            "DROP TABLE IF EXISTS " + TaskDBContract.TaskDB.TASK_RELATION_TABLE_NAME;


    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 40;

    public static final String DATABASE_NAME = "Task.db";

    public TaskDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TASK_TABLE);
        db.execSQL(SQL_CREATE_CATEGORY_TABLE);
        db.execSQL(SQL_CREATE_TASK_CATEGORY_TABLE);
        db.execSQL(SQL_CREATE_REPEATING_TABLE);
        db.execSQL((SQL_CREATE_TASK_RELATION_TABLE));
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_TASK_TABLE);
        db.execSQL(SQL_DELETE_CATEGORY_TABLE);
        db.execSQL(SQL_DELETE_TASK_CATEGORY_TABLE);
        db.execSQL(SQL_DELETE_REPEATING_TABLE);
        db.execSQL(SQL_DELETE_TASK_RELATION_TABLE);

        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}
