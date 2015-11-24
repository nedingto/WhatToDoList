package com.example.nick.whattodolist;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import android.app.DialogFragment;
import android.content.Intent;

import android.gesture.GestureOverlayView;
import android.graphics.Color;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


public class MainToDo extends AppCompatActivity implements simpleCreateTaskDialog.SimpleCreateTaskListener, advancedCreateTaskDialog.AdvancedCreateTaskListener,
        createRepeatingDialog.RepeatingCreateTaskListener, ViewPager.OnPageChangeListener{

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;
    WhatToDoFragment[] mPages= new WhatToDoFragment[3];
    Calendar currentDate;
    /**
     * The {@link ViewPager} that will host the section contents.
     */



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_to_do);

        // this is for the pagination, might need this later
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager)findViewById(R.id.pager);
        mViewPager.addOnPageChangeListener(this);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        currentDate = Calendar.getInstance();
        getSupportActionBar().setTitle(dateConverter.calendarToString(currentDate));
    }

    protected void onResume(){
        super.onResume();
        //update the tasks when it is restarted
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
    public long onAdvancedDialogPositiveClick(String taskName, String dueDate, int priority, int estimatedMins, ArrayList<String> categories, Bundle repeatingBundle) {
        mPages[mViewPager.getCurrentItem()].onAdvancedDialogPositiveClick(taskName, dueDate, priority, estimatedMins, categories, repeatingBundle);
        return 0;
    }

    @Override
    public void onAdvancedDialogNeutralClick(String taskName, String dueDate, int priority, int estimatedMins, ArrayList<String> categories, Bundle repeatingBundle) {
        mPages[mViewPager.getCurrentItem()].onAdvancedDialogNeutralClick(taskName, dueDate, priority, estimatedMins, categories, repeatingBundle);
    }

    @Override
    public void onRepeatingDialogPositiveClick(Bundle args) {
        mPages[mViewPager.getCurrentItem()].onRepeatingDialogPositiveClick(args);
    }

    @Override
    public void onRepeatingDialogNegativeClick() {
        mPages[mViewPager.getCurrentItem()].onRepeatingDialogNegativeClick();
    }

    @Override
    public void onSimpleDialogPositiveClick(String taskName, String dueDate) {
        mPages[mViewPager.getCurrentItem()].onSimpleDialogPositiveClick(taskName, dueDate);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

        if(position !=1) {
            if(mPages[1].getFullListHidden() ^ mPages[position].getFullListHidden()){
                mPages[position].hideFullListClicked();
            }
            if(mPages[1].getAssignedHidden() ^ mPages[position].getAssignedHidden()){
                mPages[position].hideAssignedClicked();
            }
            mPages[position].tasksUpdated();
            for (WhatToDoFragment page : mPages) {
                page.changeDate(position - 1);
                //page.tasksUpdated();
            }
            currentDate.add(Calendar.DAY_OF_MONTH, position - 1);
            getSupportActionBar().setTitle(dateConverter.calendarToString(currentDate));
        }

    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if(state == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) mViewPager.setCurrentItem(1,false);
        for (WhatToDoFragment page : mPages) {
            if(page!=null) page.tasksUpdated();
        }
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

            //if this is the first three then they will be initialized, otherwise they will be handled accordingly
            if (mPages[position] == null){
                WhatToDoFragment fragment = WhatToDoFragment.newInstance(position-1);
                mPages[position] = fragment;
            }
            return mPages[position];
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

    public void hideAssignedClicked(View v) {
        mPages[mViewPager.getCurrentItem()].hideAssignedClicked();
    }

    public void hideFullListClicked(View v){
        mPages[mViewPager.getCurrentItem()].hideFullListClicked();
    }

    public void onCreateRepeatingCLicked(View v) {
        mPages[mViewPager.getCurrentItem()].onCreateRepeatingCLicked(v, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));
    }

    public void createTask(View v){
        mPages[mViewPager.getCurrentItem()].createTask(v, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));
    }

}
