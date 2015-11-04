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
        public static final String COLUMN_NAME_REPEATING_BASIS = "repeating_basis";
        public static final String COLUMN_NAME_INCOMPLETE_SUB = "incomplete_sub";

        public static final String CATEGORY_TABLE_NAME = "category_name";
        public static final String COLUMN_NAME_CATEGORY_NAME = "category";

        public static final String TASK_CATEGORY_TABLE_NAME = "task_category";
        public static final String COLUMN_NAME_TASK_ID = "task_id";
        public static final String COLUMN_NAME_CATEGORY_ID = "category_id";

        public static final String REPEATING_TABLE_NAME ="repeating";
        public static final String COLUMN_NAME_PERIODICAL_NUM = "periodical_num";
        public static final String COLUMN_NAME_PERIODICAL_PERIOD = "periodical_period";
        public static final String COLUMN_NAME_ORDINAL_NUM = "ordinal_num";
        public static final String COLUMN_NAME_ORDINAL_PERIOD = "ordinal_period";
        public static final String COLUMN_NAME_SUNDAY = "sunday";
        public static final String COLUMN_NAME_MONDAY = "monday";
        public static final String COLUMN_NAME_TUESDAY = "tuesday";
        public static final String COLUMN_NAME_WEDNESDAY = "wednesday";
        public static final String COLUMN_NAME_THURSDAY = "thursday";
        public static final String COLUMN_NAME_FRIDAY = "friday";
        public static final String COLUMN_NAME_SATURDAY = "saturday";
        public static final String COLUMN_NAME_START_DATE = "start_date";
        public static final String COLUMN_NAME_END_DATE = "end_date";

        public static final String TASK_RELATION_TABLE_NAME = "task_relation";
        public static final String COLUMN_NAME_PARENT_TASK = "parent_task";
        public static final String COLUMN_NAME_SUB_TASK = "sub_task";

    }


}
