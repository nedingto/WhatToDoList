package com.example.nick.whattodolist;

import java.security.AccessController;
import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import android.app.DialogFragment;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.nick.whattodolist.TaskDBContract.TaskDB;

public class MainToDo extends AppCompatActivity
implements simpleCreateTaskDialog.SimpleCreateTaskListener{

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
    ViewPager mViewPager;

    TaskDBContract dbContract;
    TaskDbHelper mDbHelper;
    SQLiteDatabase dbR;
    SQLiteDatabase dbW;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dbContract = new TaskDBContract();
        mDbHelper = new TaskDbHelper(getApplicationContext());
        dbR = mDbHelper.getReadableDatabase();
        dbW = mDbHelper.getWritableDatabase();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_to_do);


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

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

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        EditText  et = (EditText)dialog.getDialog().findViewById(R.id.editText);
        String taskName = et.getText().toString();
        DatePicker dp = (DatePicker) dialog.getDialog().findViewById(R.id.datePicker);
        String dueDate = dp.getYear() + "-" + dp.getMonth() + "-" + dp.getDayOfMonth() + " 00:00:00";
        ContentValues values = new ContentValues();
        values.put(TaskDB.COLUMN_NAME_ENTRY_ID,taskName);
        values.put(TaskDB.COLUMN_NAME_DUE_DATE,dueDate);
        values.put(TaskDB.COLUMN_NAME_CHECKED, 0);

        long newRowId;
        newRowId = dbW.insert(
                TaskDB.TABLE_NAME,
                null,
                values);
        tasksUpdated();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

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
        @Override
        public void onActivityCreated(Bundle savedInstance){
            super.onActivityCreated(savedInstance);
            ((MainToDo)getActivity()).tasksUpdated();
        }

    }
    public void createTask(View v){
        DialogFragment newFragment = new simpleCreateTaskDialog();
        newFragment.show(getFragmentManager(), "create task");

    }

    public void tasksUpdated(){
        String[] projection = {
                TaskDB.COLUMN_NAME_ENTRY_ID,
                TaskDB.COLUMN_NAME_DUE_DATE,
                TaskDB.COLUMN_NAME_CHECKED
        };
        String sortOrder =
                "date(" + TaskDB.COLUMN_NAME_DUE_DATE + ") DESC";

        Cursor c = dbR.query(
                TaskDB.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );
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
            tv1.setText(c.getString(c.getColumnIndexOrThrow(TaskDB.COLUMN_NAME_ENTRY_ID)));
            tv2.setText(c.getString(c.getColumnIndexOrThrow(TaskDB.COLUMN_NAME_DUE_DATE)));
            if(c.getInt(c.getColumnIndexOrThrow(TaskDB.COLUMN_NAME_CHECKED)) == 1){
                cb.setChecked(true);
            } else cb.setChecked(false);
            tl.addView(tr);

            c.moveToNext();


        }
    }

}
