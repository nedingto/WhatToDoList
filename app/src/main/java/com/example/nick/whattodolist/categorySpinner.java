package com.example.nick.whattodolist;

/**
 * Created by Nick on 10/6/2015.
 */
public class categorySpinner extends spinnerPopulater {
    //creates an instance of the custom spinner fragment for categories
    @Override
    public String giveTableName() {
        return TaskDBContract.TaskDB.CATEGORY_TABLE_NAME;
    }

    @Override
    public String giveColumnName() {
        return TaskDBContract.TaskDB.COLUMN_NAME_CATEGORY_NAME;
    }

    @Override
    public String giveIdName() {
        return TaskDBContract.TaskDB._ID;
    }
}
