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
        public static final String TASK_TABLE_NAME = "tasks";
        public static final String COLUMN_NAME_TASK_NAME = "task_name";
        public static final String COLUMN_NAME_DUE_DATE = "due_date";
        public static final String COLUMN_NAME_CHECKED = "checked";
        public static final String COLUMN_NAME_PRIORITY = "priority";
        public static final String COLUMN_NAME_ESTIMATED_MINS = "estimated_mins";

        public static final String CATEGORY_TABLE_NAME = "category_name";
        public static final String COLUMN_NAME_CATEGORY_NAME = "category";

        public static final String TASK_CATEGORY_TABLE_NAME = "task_category";
        public static final String COLUMN_NAME_TASK_ID = "task_id";
        public static final String COLUMN_NAME_CATEGORY_ID = "category_id";



    }


}
