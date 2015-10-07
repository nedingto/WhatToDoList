package com.example.nick.whattodolist;

import java.security.AccessController;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.view.View.OnLongClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;



import com.example.nick.whattodolist.TaskDBContract.TaskDB;

public class MainToDo extends AppCompatActivity
implements simpleCreateTaskDialog.SimpleCreateTaskListener, advancedCreateTaskDialog.AdvancedCreateTaskListener{

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */


    TaskDBContract dbContract;
    TaskDbHelper mDbHelper;
    SQLiteDatabase dbR;
    SQLiteDatabase dbW;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dbContract = new TaskDBContract();
        mDbHelper = new TaskDbHelper(getApplicationContext());

        //set up reader and writer
        dbR = mDbHelper.getReadableDatabase();
        dbW = mDbHelper.getWritableDatabase();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main_to_do);


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        tasksUpdated();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_to_do, menu);
        return true;
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


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main_to_do, container, false);
            return rootView;

        }
        //override to populate main view as soon as possible
        @Override
        public void onActivityCreated(Bundle savedInstance){
            super.onActivityCreated(savedInstance);

        }

    }

    //method to populate main view, probably should use more accurate name
    public void tasksUpdated(){
        //query creation
        String[] projection = {
                TaskDB._ID,
                TaskDB.COLUMN_NAME_TASK_NAME,
                TaskDB.COLUMN_NAME_DUE_DATE,
                TaskDB.COLUMN_NAME_CHECKED,
                TaskDB.COLUMN_NAME_PRIORITY,
                TaskDB.COLUMN_NAME_ESTIMATED_MINS
        };
        String sortOrder =
                "date(" + TaskDB.COLUMN_NAME_DUE_DATE + ") DESC";

        Cursor c = dbR.query(
                TaskDB.TASK_TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        //this programmatically creates the views but it might be more efficient and
        //better practice to use a layout as a template and programmatically fill in text
        TableLayout tl = (TableLayout)findViewById(R.id.tableLayout1);
        tl.removeAllViews();
        c.moveToFirst();

        while(!c.isAfterLast()){

            TableRow tr = new TableRow(getApplication());
            CheckBox cb = new CheckBox(getApplication());
            TextView tv1 = new TextView(getApplication());
            TextView tv2 = new TextView(getApplication());
            tr.addView(cb);
            tr.addView(tv1);
            tr.addView(tv2);
            tv1.setText(c.getString(c.getColumnIndexOrThrow(TaskDB.COLUMN_NAME_TASK_NAME)));

            //text field for due date, will likely be displayed in a different way in final
            tv2.setText(c.getString(c.getColumnIndexOrThrow(TaskDB.COLUMN_NAME_DUE_DATE)));

            if(c.getInt(c.getColumnIndexOrThrow(TaskDB.COLUMN_NAME_CHECKED)) == 1){
                cb.setChecked(true);
            } else cb.setChecked(false);

            //switch to color the row based on priority
            //TODO cahnge colors and make default
            int priority = c.getInt(c.getColumnIndexOrThrow(TaskDB.COLUMN_NAME_PRIORITY));
            switch(priority) {
                case 1:
                    tr.setBackgroundColor(Color.rgb(0, 204, 255));
                    break;
                case 2:
                    tr.setBackgroundColor(Color.rgb(0, 255, 0));
                    break;
                case 3:
                    tr.setBackgroundColor(Color.rgb(255, 255, 0));
                    break;
                case 4:
                    tr.setBackgroundColor(Color.rgb(255, 153, 0));
                    break;
                case 5:
                    tr.setBackgroundColor(Color.rgb(255, 51, 0));
                    break;
                default:
                    break;
            }

            //field for the estimated time to complete, will likely not be displayed in final
            int estimatedMins = c.getInt(c.getColumnIndexOrThrow((TaskDB.COLUMN_NAME_ESTIMATED_MINS)));
            if(estimatedMins >0) {
                TextView tv3 = new TextView(getApplication());
                tv3.setText(" " + estimatedMins + "mins to complete");
                tr.addView(tv3);
            }
            tl.addView(tr);

            tr.setTag(c.getString(c.getColumnIndex(TaskDB._ID)));

            tr.setOnLongClickListener( new View.OnLongClickListener() {
                                          @Override
                                         public boolean onLongClick(View v){
                                              Intent myIntent = new Intent(MainToDo.this,EditTaskActivity.class);
                                              myIntent.putExtra("taskId", (String)v.getTag()); //Optional parameters
                                              MainToDo.this.startActivity(myIntent);
                                             return false;
                                          }
            });

            c.moveToNext();


        }
    }


    /*reactions to creating task button*/
    public void createTask(View v){
        DialogFragment newFragment = new simpleCreateTaskDialog();
        newFragment.show(getFragmentManager(), "create task");
    }

    //create task and save it when one is created
    @Override
    public void onSimpleDialogPositiveClick(String taskName, String dueDate) {
        ContentValues values = new ContentValues();
        values.put(TaskDB.COLUMN_NAME_TASK_NAME,taskName);
        values.put(TaskDB.COLUMN_NAME_DUE_DATE,dueDate);
        values.put(TaskDB.COLUMN_NAME_CHECKED, 0);

        //hook if row id is needed later
        //might be good to have function
        // that only updates the newly created task
        long newRowId;
        newRowId = dbW.insert(
                TaskDB.TASK_TABLE_NAME,
                null,
                values);


        tasksUpdated();
    }


    @Override
    public void onAdvancedDialogPositiveClick(String taskName, String dueDate, int priority, int estimatedMins, ArrayList<String> categories){
        //set up all value for advance task
        ContentValues values = new ContentValues();
        values.put(TaskDB.COLUMN_NAME_TASK_NAME,taskName);
        values.put(TaskDB.COLUMN_NAME_DUE_DATE,dueDate);
        values.put(TaskDB.COLUMN_NAME_CHECKED, 0);
        values.put(TaskDB.COLUMN_NAME_PRIORITY, priority);
        values.put(TaskDB.COLUMN_NAME_ESTIMATED_MINS, estimatedMins);

        //hook if row id is needed later
        //might be good to have function
        //that only updates the newly created task
        long newTaskRowId;
        newTaskRowId = dbW.insert(
                TaskDB.TASK_TABLE_NAME,
                null,
                values);



        //check if a category exists
        //if not create it
        //then add it to the task_category table with keys from both
        String[] projection = {
                TaskDB._ID,
                TaskDB.COLUMN_NAME_CATEGORY_NAME
        };

        //if the category exists, just add its id and the tasks id to the task_category table
        //otherwise create the category and then add their ids
        for(int i = 0; i < categories.size();i++){
            Cursor c = dbR.query(
                    TaskDB.CATEGORY_TABLE_NAME,  // The table to query
                    projection,                               // The columns to return
                    TaskDB.COLUMN_NAME_CATEGORY_NAME + "=?",                                // The columns for the WHERE clause
                    new String[] {categories.get(i)},                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    null                                 // The sort order
            );
            if(c.getCount() > 0){
                c.moveToFirst();
                int categoryRowId = c.getInt(c.getColumnIndexOrThrow(TaskDB._ID));
                values = new ContentValues();
                values.put(TaskDB.COLUMN_NAME_TASK_ID, newTaskRowId);
                values.put(TaskDB.COLUMN_NAME_CATEGORY_ID, categoryRowId);
                long newTaskCategoryRowId = dbW.insert(
                        TaskDB.TASK_CATEGORY_TABLE_NAME,
                        null,
                        values
                );

            }else{
                values = new ContentValues();
                values.put(TaskDB.COLUMN_NAME_CATEGORY_NAME, categories.get(i));
                long newCategoryRowId = dbW.insert(
                        TaskDB.CATEGORY_TABLE_NAME,
                        null,
                        values
                );
                values = new ContentValues();
                values.put(TaskDB.COLUMN_NAME_TASK_ID, newTaskRowId);
                values.put(TaskDB.COLUMN_NAME_CATEGORY_ID, newCategoryRowId);
                long newTaskCategoryRowId = dbW.insert(
                        TaskDB.TASK_CATEGORY_TABLE_NAME,
                        null,
                        values
                );
            }
        }
        tasksUpdated();
    }













}
