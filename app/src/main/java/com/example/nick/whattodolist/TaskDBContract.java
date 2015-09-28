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
        public static final String COLUMN_NAME_DUE_DATE = "duedate";
        public static final String COLUMN_NAME_CHECKED = "checked";
        public static final String COLUMN_NAME_PRIORITY = "priority";
        public static final String COLUMN_NAME_ESTIMATED_MINS = "estimatedMins";


    }


}
