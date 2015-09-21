package com.example.nick.whattodolist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by Nick on 9/20/2015.
 */
public class TaskDBContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public TaskDBContract() {}

    /* Inner class that defines the table contents */
    public static abstract class TaskDB implements BaseColumns {
        public static final String TABLE_NAME = "tasks";
        public static final String COLUMN_NAME_ENTRY_ID = "taskid";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_DUE_DATE = "due date";
        public static final String COLUMN_NAME_CHECKED = "checked";
        private static final String TEXT_TYPE = " TEXT";
        private static final String INTEGER_TYPE = " INTEGER";
        private static final String COMMA_SEP = ",";
        private static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + TaskDB.TABLE_NAME + " (" +
                        TaskDB._ID + " INTEGER PRIMARY KEY," +
                        TaskDB.COLUMN_NAME_ENTRY_ID + TEXT_TYPE + COMMA_SEP +
                        TaskDB.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                        TaskDB.COLUMN_NAME_DUE_DATE + TEXT_TYPE + COMMA_SEP +
                        TaskDB.COLUMN_NAME_CHECKED + INTEGER_TYPE + COMMA_SEP +
                ")";

        private static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + TaskDB.TABLE_NAME;

        public class TaskDBHelper extends SQLiteOpenHelper {
            // If you change the database schema, you must increment the database version.
            public static final int DATABASE_VERSION = 1;
            public static final String DATABASE_NAME = "FeedReader.db";

            public TaskDBHelper(Context context) {
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
    }

}
