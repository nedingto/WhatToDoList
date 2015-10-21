package com.example.nick.whattodolist;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;



import com.example.nick.whattodolist.TaskDBContract.TaskDB;

public class MainToDo extends AppCompatActivity
implements simpleCreateTaskDialog.SimpleCreateTaskListener, advancedCreateTaskDialog.AdvancedCreateTaskListener,
createRepeatingDialog.RepeatingCreateTaskListener{

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

    //editors for the database
    TaskEditor taskEditor;
    RepeatingBasisEditor repeatingBasisEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        taskEditor = new TaskEditor(getApplicationContext());
        repeatingBasisEditor = new RepeatingBasisEditor(getApplicationContext());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main_to_do);

        // this is for the pagination, might need this later
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        //update the tasks when this starts
        tasksUpdated();

    }

    protected void onResume(){
        super.onResume();
        //update the tasks when it is restarted
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


    //this is for the page adapter that I may use later
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

    //method to populate main view
    public void tasksUpdated(){

        ArrayList<Bundle> tasks = taskEditor.getTasksDateAcceding();

        //removes the task list and then programmatically fills it will the newest tasks
        TableLayout tl = (TableLayout)findViewById(R.id.tableLayout1);
        tl.removeAllViews();

        //set a calendar to test these against, if there are past
        Calendar today = Calendar.getInstance();

        Calendar tmp = dateConverter.sqlToCalendar(dateConverter.firstDateSql);



        for(Bundle task:tasks){
            Calendar dueDate = dateConverter.sqlToCalendar(task.getString(taskEditor.BUNDLE_DUE_DATE));
            //act only if the task is due in the future or is past due
            if(!dueDate.before(today) || task.getInt(taskEditor.BUNDLE_CHECKED) == 0) {
                if(dueDate.after(tmp)){
                    //if there is a new due date create a divider and reset tmp
                    TableRow tr = new TableRow(getApplication());
                    //extra space
                    TextView line = new TextView(getApplicationContext());
                    tr.addView(line);
                    TextView dueText = new TextView(getApplicationContext());
                    dueText.setText(dateConverter.calendarToString(dueDate));
                    tr.addView(dueText);
                    tl.addView(tr);
                    tmp = dueDate;
                }

                int taskId = task.getInt(TaskEditor.BUNDLE_TASK_ID);
                TableRow tr = new TableRow(getApplication());
                CheckBox cb = new CheckBox(getApplication());
                cb.setTag(taskId);
                cb.setOnClickListener(new View.OnClickListener() {
                                          public void onClick(View v) {
                                              CheckBox thisView = (CheckBox) v;
                                              taskEditor.updateChecked((int) v.getTag(),
                                                      thisView.isChecked() ? 1 : 0);
                                          }
                                      }
                );
                TextView tv1 = new TextView(getApplication());
                tr.addView(cb);
                tr.addView(tv1);
                tv1.setText(task.getString(TaskEditor.BUNDLE_TASK_NAME));

                //text field for due date, will likely be displayed in a different way in final

                if (task.getInt(TaskEditor.BUNDLE_CHECKED) == 1) {
                    cb.setChecked(true);
                } else cb.setChecked(false);

                //switch to color the row based on priority
                //TODO cahnge colors
                int priority = task.getInt(TaskEditor.BUNDLE_PRIORITY);
                switch (priority) {
                    case 1:
                        tr.setBackgroundColor(Color.BLACK);
                        break;
                    case 2:
                        tr.setBackgroundColor(Color.DKGRAY);
                        break;
                    case 3:
                        tr.setBackgroundColor(Color.GREEN);
                        break;
                    case 4:
                        tr.setBackgroundColor(Color.YELLOW);
                        break;
                    case 5:
                        tr.setBackgroundColor(Color.RED);
                        break;
                    default:
                        break;
                }

                tl.addView(tr);

                tr.setTag(taskId);

                tr.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Intent myIntent = new Intent(MainToDo.this, EditTaskActivity.class);
                        myIntent.putExtra("taskId", String.valueOf(v.getTag()));
                        MainToDo.this.startActivity(myIntent);
                        return false;
                    }
                });
            }
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

        taskEditor.createTask(taskName, dateConverter.stringToSql(dueDate), 1, 0, -1);
        tasksUpdated();
    }


    @Override
    public long onAdvancedDialogPositiveClick(String taskName, String dueDate, int priority, int estimatedMins, ArrayList<String> categories, Bundle repeatingBundle){
        //task id to return, returns the first task if it is recurring
        long taskId = -1;

        ArrayList<String> dates = new ArrayList<>();

        //cheeck if there should be a repeating basis, if no the basis id will be -1
        float basisId = -1;
        if(repeatingBundle.containsKey(createRepeatingDialog.BUNDLE_ORDINAL_NUM)){
            basisId = repeatingBasisEditor.createBasis(
                    repeatingBundle.getInt(createRepeatingDialog.BUNDLE_PERIODICAL_NUM),
                    repeatingBundle.getInt(createRepeatingDialog.BUNDLE_PERIODICAL_PERIOD),
                    repeatingBundle.getInt(createRepeatingDialog.BUNDLE_ORDINAL_NUM),
                    repeatingBundle.getInt(createRepeatingDialog.BUNDLE_ORDINAL_PERIOD),
                    repeatingBundle.getIntArray(createRepeatingDialog.BUNDLE_DAY_OF_WEEK),
                    repeatingBundle.getString(createRepeatingDialog.BUNDLE_START_DATE),
                    repeatingBundle.getString(createRepeatingDialog.BUNDLE_END_DATE));
            dates = repeatingBasisEditor.getBasisDates(basisId);
        } else dates.add(dueDate);
        //for each date add a task
        for(int i = 0; i < dates.size();i++){
            int id = taskEditor.createTask(taskName, dates.get(i),
                    priority, estimatedMins, (int) basisId);

            //save the first id to return
            if(i == 0) taskId = id;

            taskEditor.updateCategories(categories, new ArrayList<String>(), id);
        }
        tasksUpdated();
        return taskId;
    }

    @Override
    public void onAdvancedDialogNeutralClick(String taskName, String dueDate, int priority,
                                             int estimatedMins, ArrayList<String> categories, Bundle repeatingBundle) {
        //same as positive but then open the edit with the first task id that was created
        long taskId;
        taskId = onAdvancedDialogPositiveClick(taskName, dueDate, priority, estimatedMins,
                categories, repeatingBundle);
        Intent myIntent = new Intent(MainToDo.this,EditTaskActivity.class);
        myIntent.putExtra("taskId", String.valueOf(taskId));
        MainToDo.this.startActivity(myIntent);
    }

    @Override
    public void onRepeatingDialogPositiveClick(Bundle repeatingBundle) {
        ArrayList<String> dates = new ArrayList<>();

        //create the basis and get its id
        float basisId = -1;
        basisId = repeatingBasisEditor.createBasis(
                repeatingBundle.getInt(createRepeatingDialog.BUNDLE_PERIODICAL_NUM),
                repeatingBundle.getInt(createRepeatingDialog.BUNDLE_PERIODICAL_PERIOD),
                repeatingBundle.getInt(createRepeatingDialog.BUNDLE_ORDINAL_NUM),
                repeatingBundle.getInt(createRepeatingDialog.BUNDLE_ORDINAL_PERIOD),
                repeatingBundle.getIntArray(createRepeatingDialog.BUNDLE_DAY_OF_WEEK),
                repeatingBundle.getString(createRepeatingDialog.BUNDLE_START_DATE),
                repeatingBundle.getString(createRepeatingDialog.BUNDLE_END_DATE));
        dates = repeatingBasisEditor.getBasisDates(basisId);

        //since this came from a simple dialog it will have a task name, save that
        String taskName = repeatingBundle.getString(simpleCreateTaskDialog.BUNDLE_TASK_NAME);

        //for each date create a task
        for(String date: dates) {
            taskEditor.createTask(taskName, date, 1, 0, (int)basisId);
        }
        tasksUpdated();
    }

    @Override
    public void onRepeatingDialogNegativeClick() {
        //user canceled repeating dialog, do nothing
    }


}
