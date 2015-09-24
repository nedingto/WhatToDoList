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
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TaskDBContract.TaskDB.TABLE_NAME + " (" +
                    TaskDBContract.TaskDB._ID + " INTEGER PRIMARY KEY," +
                    TaskDBContract.TaskDB.COLUMN_NAME_ENTRY_ID + TEXT_TYPE + COMMA_SEP +
                    TaskDBContract.TaskDB.COLUMN_NAME_DUE_DATE + TEXT_TYPE + COMMA_SEP +
                    TaskDBContract.TaskDB.COLUMN_NAME_CHECKED + INTEGER_TYPE +
                    ")";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TaskDBContract.TaskDB.TABLE_NAME;
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 4;
    public static final String DATABASE_NAME = "Task.db";

    public TaskDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}
