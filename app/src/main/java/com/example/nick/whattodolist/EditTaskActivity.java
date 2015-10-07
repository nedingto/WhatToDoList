package com.example.nick.whattodolist;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import junit.framework.Assert;

import java.util.ArrayList;

public class EditTaskActivity extends AppCompatActivity {


    TaskDBContract dbContract;
    TaskDbHelper mDbHelper;
    SQLiteDatabase dbR;
    SQLiteDatabase dbW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);
        dbContract = new TaskDBContract();
        mDbHelper = new TaskDbHelper(getApplicationContext());

        //set up reader and writer
        dbR = mDbHelper.getReadableDatabase();
        dbW = mDbHelper.getWritableDatabase();
        Intent intent = getIntent();
        String value = intent.getStringExtra("taskId");

        //fill all the fields
        populateEdit(value);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_task, menu);
        return true;
    }

    public void populateEdit(String id) {
        //query creation
        String[] projection = {
                TaskDBContract.TaskDB._ID,
                TaskDBContract.TaskDB.COLUMN_NAME_TASK_NAME,
                TaskDBContract.TaskDB.COLUMN_NAME_DUE_DATE,
                TaskDBContract.TaskDB.COLUMN_NAME_CHECKED,
                TaskDBContract.TaskDB.COLUMN_NAME_PRIORITY,
                TaskDBContract.TaskDB.COLUMN_NAME_ESTIMATED_MINS
        };

        String[] ID = {id};
        Cursor c = dbR.query(
                TaskDBContract.TaskDB.TASK_TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                TaskDBContract.TaskDB._ID + "=?",                                // The columns for the WHERE clause
                ID,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                // The sort order
        );
        c.moveToFirst();
        //should assert that c is not after last, also there should only be one

        //set title
        EditText title = ((EditText) findViewById(R.id.editText));
        title.setText(c.getString(c.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_TASK_NAME)));

        //set due date
        TextView dueDate = ((TextView) findViewById(R.id.textView21));
        dueDate.setText(c.getString(c.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_DUE_DATE)));

        //set priority
        RatingBar priority = ((RatingBar) findViewById(R.id.ratingBar));
        priority.setRating(c.getInt(c.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_PRIORITY)));

        //set estimated mins
        EditText estimatedMin = ((EditText) findViewById(R.id.editText2));
        estimatedMin.setText(c.getString(c.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_ESTIMATED_MINS)));


        String table = TaskDBContract.TaskDB.CATEGORY_TABLE_NAME +
                " INNER JOIN " + TaskDBContract.TaskDB.TASK_CATEGORY_TABLE_NAME +
                " ON " +  TaskDBContract.TaskDB.COLUMN_NAME_CATEGORY_ID + " = " +
                TaskDBContract.TaskDB.CATEGORY_TABLE_NAME + "."
                + TaskDBContract.TaskDB._ID;


        projection = new String[]{
                TaskDBContract.TaskDB.COLUMN_NAME_TASK_ID,
                TaskDBContract.TaskDB.COLUMN_NAME_CATEGORY_NAME
        };

        ID = new String[]{id};
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

        final spinnerPopulater fragment = (spinnerPopulater) getFragmentManager().findFragmentById(R.id.spinner_fragment);
        fragment.setSelected(list);

        //TODO check if there is a need to close the cursor
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}

