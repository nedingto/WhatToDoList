package com.example.nick.whattodolist.test;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.test.ActivityInstrumentationTestCase2;

import com.example.nick.whattodolist.MainToDo;
import com.example.nick.whattodolist.TaskDBContract;
import com.example.nick.whattodolist.TaskDbHelper;
import com.example.nick.whattodolist.TaskEditor;
import com.example.nick.whattodolist.createRepeatingDialog;
import com.example.nick.whattodolist.repeatingBasisEditor;

import junit.framework.TestResult;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Nicole on 11/12/2015.
 */
public class RepeatingBasisEditorTest extends ActivityInstrumentationTestCase2<MainToDo> {
    MainToDo activity;
    Context mContext;
    repeatingBasisEditor repeatingBasisEditor;
    SQLiteDatabase dbR;
    SQLiteDatabase dbW;

    public RepeatingBasisEditorTest() {
        super(MainToDo.class);
    }


    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        getInstrumentation().waitForIdleSync();
        activity = getActivity();
        mContext = activity;
        repeatingBasisEditor = new repeatingBasisEditor(mContext);
        TaskDbHelper mDbHelper = new TaskDbHelper(mContext);

        //set up reader and writer
        dbR = mDbHelper.getReadableDatabase();
        dbW = mDbHelper.getWritableDatabase();
        dbR.delete(TaskDBContract.TaskDB.TASK_TABLE_NAME, null, null);
        dbR.delete(TaskDBContract.TaskDB.REPEATING_TABLE_NAME, null, null);
        dbR.delete(TaskDBContract.TaskDB.CATEGORY_TABLE_NAME, null, null);
        dbR.delete(TaskDBContract.TaskDB.TASK_RELATION_TABLE_NAME, null, null);
        dbR.delete(TaskDBContract.TaskDB.TASK_CATEGORY_TABLE_NAME, null, null);
    }

    @Test
    public void testCreatBasis1_1(){
        int originalPeriodicalNumber = 1;
        int originalperiodicalPeriod = 1;
        int originalOrdinalNumber = 1;
        int originalOrdinalPeriod = 1;
        int[] originalDayOfWeek = {0,0,0,0,0,0,0};
        String originalStartDate = "2000-01-01";
        String originalEndDate = "2000-01-01";
        repeatingBasisEditor.createBasis(originalPeriodicalNumber, originalperiodicalPeriod,
                originalOrdinalNumber, originalOrdinalPeriod, originalDayOfWeek, originalStartDate,
                originalEndDate);
        String[] projection = {
                TaskDBContract.TaskDB._ID,
                TaskDBContract.TaskDB.COLUMN_NAME_PERIODICAL_NUM,
                TaskDBContract.TaskDB.COLUMN_NAME_PERIODICAL_PERIOD,
                TaskDBContract.TaskDB.COLUMN_NAME_ORDINAL_NUM,
                TaskDBContract.TaskDB.COLUMN_NAME_ORDINAL_PERIOD,
                TaskDBContract.TaskDB.COLUMN_NAME_SUNDAY,
                TaskDBContract.TaskDB.COLUMN_NAME_MONDAY,
                TaskDBContract.TaskDB.COLUMN_NAME_TUESDAY,
                TaskDBContract.TaskDB.COLUMN_NAME_WEDNESDAY,
                TaskDBContract.TaskDB.COLUMN_NAME_THURSDAY,
                TaskDBContract.TaskDB.COLUMN_NAME_FRIDAY,
                TaskDBContract.TaskDB.COLUMN_NAME_SATURDAY,
                TaskDBContract.TaskDB.COLUMN_NAME_START_DATE,
                TaskDBContract.TaskDB.COLUMN_NAME_END_DATE
        };

        Cursor cursor = dbR.query(
                TaskDBContract.TaskDB.REPEATING_TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                null,         // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );
        cursor.moveToFirst();
        assertEquals(originalPeriodicalNumber, cursor.getInt(cursor.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_PERIODICAL_NUM)));
        assertEquals(originalperiodicalPeriod, cursor.getInt(cursor.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_PERIODICAL_PERIOD)));
        assertEquals(originalOrdinalNumber, cursor.getInt(cursor.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_ORDINAL_NUM)));
        assertEquals(originalOrdinalPeriod, cursor.getInt(cursor.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_ORDINAL_PERIOD)));
        assertEquals(originalDayOfWeek[0], cursor.getInt(cursor.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_SUNDAY)));
        assertEquals(originalDayOfWeek[1], cursor.getInt(cursor.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_MONDAY)));
        assertEquals(originalDayOfWeek[2], cursor.getInt(cursor.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_THURSDAY)));
        assertEquals(originalDayOfWeek[3], cursor.getInt(cursor.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_WEDNESDAY)));
        assertEquals(originalDayOfWeek[4], cursor.getInt(cursor.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_THURSDAY)));
        assertEquals(originalDayOfWeek[5], cursor.getInt(cursor.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_FRIDAY)));
        assertEquals(originalDayOfWeek[6], cursor.getInt(cursor.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_SATURDAY)));
        assertEquals(originalStartDate, cursor.getString(cursor.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_START_DATE)));
        assertEquals(originalEndDate, cursor.getString(cursor.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_END_DATE)));
    }

    @Test
    public void testGetBasisDates2_1(){
        int originalPeriodicalNumber = 1;
        int originalperiodicalPeriod = 0;
        int originalOrdinalNumber = 1;
        int originalOrdinalPeriod = 1;
        int[] originalDayOfWeek = {0,0,0,0,0,0,0};
        String originalStartDate = "2000-01-01";
        String originalEndDate = "2000-01-02";
        ContentValues values = new ContentValues();
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_PERIODICAL_NUM, originalPeriodicalNumber);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_PERIODICAL_PERIOD, originalperiodicalPeriod);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_ORDINAL_NUM, originalOrdinalNumber);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_ORDINAL_PERIOD, originalOrdinalPeriod);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_SUNDAY, originalDayOfWeek[0]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_MONDAY, originalDayOfWeek[1]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_TUESDAY, originalDayOfWeek[2]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_WEDNESDAY, originalDayOfWeek[3]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_THURSDAY, originalDayOfWeek[4]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_FRIDAY, originalDayOfWeek[5]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_SATURDAY, originalDayOfWeek[6]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_START_DATE, originalStartDate);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_END_DATE, originalEndDate);
        long id = dbW.insert(TaskDBContract.TaskDB.REPEATING_TABLE_NAME, null, values);
        ArrayList<String> result = repeatingBasisEditor.getBasisDates(id);
        assertEquals("2000-01-01", result.get(0));
        assertEquals("2000-01-02",result.get(1));
    }
    @Test
    public void testGetBasisDates2_2(){
        int originalPeriodicalNumber = 1;
        int originalperiodicalPeriod = 1;
        int originalOrdinalNumber = 0;
        int originalOrdinalPeriod = 0;
        int[] originalDayOfWeek = {1,0,0,0,0,0,0};
        String originalStartDate = "2015-11-08";
        String originalEndDate = "2015-11-21";
        ContentValues values = new ContentValues();
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_PERIODICAL_NUM, originalPeriodicalNumber);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_PERIODICAL_PERIOD, originalperiodicalPeriod);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_ORDINAL_NUM,originalOrdinalNumber);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_ORDINAL_PERIOD,originalOrdinalPeriod);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_SUNDAY,originalDayOfWeek[0]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_MONDAY,originalDayOfWeek[1]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_TUESDAY,originalDayOfWeek[2]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_WEDNESDAY,originalDayOfWeek[3]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_THURSDAY,originalDayOfWeek[4]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_FRIDAY,originalDayOfWeek[5]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_SATURDAY,originalDayOfWeek[6]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_START_DATE,originalStartDate);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_END_DATE, originalEndDate);
        long id = dbW.insert(TaskDBContract.TaskDB.REPEATING_TABLE_NAME, null, values);
        ArrayList<String> result = repeatingBasisEditor.getBasisDates(id);
        assertEquals("2015-11-08", result.get(0));
        assertEquals("2015-11-15",result.get(1));
    }
    @Test
    public void testGetBasisDates2_3(){
        int originalPeriodicalNumber = 1;
        int originalperiodicalPeriod = 2;
        int originalOrdinalNumber = 1;
        int originalOrdinalPeriod = 1;
        int[] originalDayOfWeek = {0,0,0,0,0,0,0};
        String originalStartDate = "2015-11-01";
        String originalEndDate = "2015-12-31";
        ContentValues values = new ContentValues();
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_PERIODICAL_NUM, originalPeriodicalNumber);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_PERIODICAL_PERIOD, originalperiodicalPeriod);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_ORDINAL_NUM,originalOrdinalNumber);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_ORDINAL_PERIOD,originalOrdinalPeriod);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_SUNDAY,originalDayOfWeek[0]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_MONDAY,originalDayOfWeek[1]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_TUESDAY,originalDayOfWeek[2]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_WEDNESDAY,originalDayOfWeek[3]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_THURSDAY,originalDayOfWeek[4]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_FRIDAY,originalDayOfWeek[5]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_SATURDAY,originalDayOfWeek[6]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_START_DATE,originalStartDate);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_END_DATE, originalEndDate);
        long id = dbW.insert(TaskDBContract.TaskDB.REPEATING_TABLE_NAME, null, values);
        ArrayList<String> result = repeatingBasisEditor.getBasisDates(id);
        assertEquals("2015-11-01", result.get(0));
        assertEquals("2015-12-06",result.get(1));
    }
    @Test
    public void testGetBasisDates2_4(){
        int originalPeriodicalNumber = 1;
        int originalperiodicalPeriod = 2;
        int originalOrdinalNumber = 0;
        int originalOrdinalPeriod = 1;
        int[] originalDayOfWeek = {0,0,0,0,0,0,0};
        String originalStartDate = "2015-11-01";
        String originalEndDate = "2015-12-31";
        ContentValues values = new ContentValues();
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_PERIODICAL_NUM, originalPeriodicalNumber);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_PERIODICAL_PERIOD, originalperiodicalPeriod);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_ORDINAL_NUM,originalOrdinalNumber);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_ORDINAL_PERIOD,originalOrdinalPeriod);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_SUNDAY,originalDayOfWeek[0]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_MONDAY,originalDayOfWeek[1]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_TUESDAY,originalDayOfWeek[2]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_WEDNESDAY,originalDayOfWeek[3]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_THURSDAY,originalDayOfWeek[4]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_FRIDAY,originalDayOfWeek[5]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_SATURDAY,originalDayOfWeek[6]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_START_DATE,originalStartDate);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_END_DATE, originalEndDate);
        long id = dbW.insert(TaskDBContract.TaskDB.REPEATING_TABLE_NAME, null, values);
        ArrayList<String> result = repeatingBasisEditor.getBasisDates(id);
        assertEquals("2015-11-29", result.get(0));
        assertEquals("2015-12-27",result.get(1));
    }
    @Test
    public void testGetBasisDates2_5(){
        int originalPeriodicalNumber = 1;
        int originalperiodicalPeriod = 2;
        int originalOrdinalNumber = 1;
        int originalOrdinalPeriod = 0;
        int[] originalDayOfWeek = {0,0,0,0,0,0,0};
        String originalStartDate = "2015-11-01";
        String originalEndDate = "2015-12-31";
        ContentValues values = new ContentValues();
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_PERIODICAL_NUM, originalPeriodicalNumber);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_PERIODICAL_PERIOD, originalperiodicalPeriod);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_ORDINAL_NUM,originalOrdinalNumber);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_ORDINAL_PERIOD,originalOrdinalPeriod);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_SUNDAY,originalDayOfWeek[0]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_MONDAY,originalDayOfWeek[1]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_TUESDAY,originalDayOfWeek[2]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_WEDNESDAY,originalDayOfWeek[3]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_THURSDAY,originalDayOfWeek[4]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_FRIDAY,originalDayOfWeek[5]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_SATURDAY,originalDayOfWeek[6]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_START_DATE,originalStartDate);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_END_DATE, originalEndDate);
        long id = dbW.insert(TaskDBContract.TaskDB.REPEATING_TABLE_NAME, null, values);
        ArrayList<String> result = repeatingBasisEditor.getBasisDates(id);
        assertEquals("2015-11-01", result.get(0));
        assertEquals("2015-12-01",result.get(1));
    }
    @Test
    public void testGetBasisDates2_6(){
        int originalPeriodicalNumber = 1;
        int originalperiodicalPeriod = 2;
        int originalOrdinalNumber = 0;
        int originalOrdinalPeriod = 0;
        int[] originalDayOfWeek = {0,0,0,0,0,0,0};
        String originalStartDate = "2015-11-01";
        String originalEndDate = "2015-12-31";
        ContentValues values = new ContentValues();
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_PERIODICAL_NUM, originalPeriodicalNumber);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_PERIODICAL_PERIOD, originalperiodicalPeriod);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_ORDINAL_NUM,originalOrdinalNumber);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_ORDINAL_PERIOD,originalOrdinalPeriod);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_SUNDAY,originalDayOfWeek[0]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_MONDAY,originalDayOfWeek[1]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_TUESDAY,originalDayOfWeek[2]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_WEDNESDAY,originalDayOfWeek[3]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_THURSDAY,originalDayOfWeek[4]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_FRIDAY,originalDayOfWeek[5]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_SATURDAY,originalDayOfWeek[6]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_START_DATE,originalStartDate);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_END_DATE, originalEndDate);
        long id = dbW.insert(TaskDBContract.TaskDB.REPEATING_TABLE_NAME, null, values);
        ArrayList<String> result = repeatingBasisEditor.getBasisDates(id);
        assertEquals("2015-11-30", result.get(0));
        assertEquals("2015-12-31",result.get(1));
    }
    @Test
    public void testGetBasisDates2_7(){
        int originalPeriodicalNumber = 1;
        int originalperiodicalPeriod = 3;
        int originalOrdinalNumber = 0;
        int originalOrdinalPeriod = 0;
        int[] originalDayOfWeek = {0,0,0,0,0,0,0};
        String originalStartDate = "2015-01-01";
        String originalEndDate = "2016-12-31";
        ContentValues values = new ContentValues();
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_PERIODICAL_NUM, originalPeriodicalNumber);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_PERIODICAL_PERIOD, originalperiodicalPeriod);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_ORDINAL_NUM,originalOrdinalNumber);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_ORDINAL_PERIOD,originalOrdinalPeriod);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_SUNDAY,originalDayOfWeek[0]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_MONDAY,originalDayOfWeek[1]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_TUESDAY,originalDayOfWeek[2]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_WEDNESDAY,originalDayOfWeek[3]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_THURSDAY,originalDayOfWeek[4]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_FRIDAY,originalDayOfWeek[5]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_SATURDAY,originalDayOfWeek[6]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_START_DATE,originalStartDate);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_END_DATE, originalEndDate);
        long id = dbW.insert(TaskDBContract.TaskDB.REPEATING_TABLE_NAME, null, values);
        ArrayList<String> result = repeatingBasisEditor.getBasisDates(id);
        assertEquals("2015-01-01", result.get(0));
        assertEquals("2016-01-01",result.get(1));
    }
    @Test
    public void testGetBasisDates2_8(){
        int originalPeriodicalNumber = 1;
        int originalperiodicalPeriod = 3;
        int originalOrdinalNumber = 0;
        int originalOrdinalPeriod = 0;
        int[] originalDayOfWeek = {0,0,0,0,0,0,0};
        String originalStartDate = "2015-01-01";
        String originalEndDate = "2016-12-31";
        ContentValues values = new ContentValues();
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_PERIODICAL_NUM, originalPeriodicalNumber);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_PERIODICAL_PERIOD, originalperiodicalPeriod);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_ORDINAL_NUM,originalOrdinalNumber);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_ORDINAL_PERIOD,originalOrdinalPeriod);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_SUNDAY,originalDayOfWeek[0]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_MONDAY,originalDayOfWeek[1]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_TUESDAY,originalDayOfWeek[2]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_WEDNESDAY,originalDayOfWeek[3]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_THURSDAY,originalDayOfWeek[4]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_FRIDAY,originalDayOfWeek[5]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_SATURDAY,originalDayOfWeek[6]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_START_DATE,originalStartDate);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_END_DATE, originalEndDate);
        long id = dbW.insert(TaskDBContract.TaskDB.REPEATING_TABLE_NAME, null, values);
        ArrayList<String> result = repeatingBasisEditor.getBasisDates((int)id + 1);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetBasisTaskIds3_1(){
        String originalTitle = "task";
        String originalDate = "2015-11-14";
        int originalPriority = 1;
        int originalEstimation = 1;
        int originalChecked = 1;
        int originalRepeating = 1;
        ContentValues values = new ContentValues();
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_TASK_NAME, originalTitle);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_DUE_DATE, originalDate);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_PRIORITY, originalPriority);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_ESTIMATED_MINS, originalEstimation);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_CHECKED, originalChecked);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_REPEATING_BASIS,originalRepeating);
        long id = dbW.insert(TaskDBContract.TaskDB.TASK_TABLE_NAME, null, values);
        long id2 = dbW.insert(TaskDBContract.TaskDB.TASK_TABLE_NAME, null, values);
        ArrayList<Integer> results = repeatingBasisEditor.getBasisTaskIds(1, "2015-11-14");
        assertTrue(results.contains((int)id));
        assertTrue(results.contains((int) id2));
    }
    @Test
    public void testGetBasis4_1(){
        int originalPeriodicalNumber = 1;
        int originalPeriodicalPeriod = 1;
        int originalOrdinalNumber = 1;
        int originalOrdinalPeriod = 1;
        int[] originalDayOfWeek = {1,1,1,1,1,1,1};
        String originalStartDate = "2000-01-01";
        String originalEndDate = "2000-01-02";
        ContentValues values = new ContentValues();
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_PERIODICAL_NUM, originalPeriodicalNumber);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_PERIODICAL_PERIOD, originalPeriodicalPeriod);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_ORDINAL_NUM, originalOrdinalNumber);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_ORDINAL_PERIOD, originalOrdinalPeriod);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_SUNDAY, originalDayOfWeek[0]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_MONDAY, originalDayOfWeek[1]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_TUESDAY, originalDayOfWeek[2]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_WEDNESDAY, originalDayOfWeek[3]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_THURSDAY, originalDayOfWeek[4]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_FRIDAY, originalDayOfWeek[5]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_SATURDAY, originalDayOfWeek[6]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_START_DATE, originalStartDate);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_END_DATE, originalEndDate);
        long id = dbW.insert(TaskDBContract.TaskDB.REPEATING_TABLE_NAME, null, values);
        Bundle basis = repeatingBasisEditor.getBasis(-1);
        assertEquals(0,basis.getInt(createRepeatingDialog.BUNDLE_PERIODICAL_PERIOD));
        assertEquals(0,basis.getInt(createRepeatingDialog.BUNDLE_PERIODICAL_NUM));
        assertEquals(0,basis.getInt(createRepeatingDialog.BUNDLE_ORDINAL_PERIOD));
        assertEquals(0,basis.getInt(createRepeatingDialog.BUNDLE_ORDINAL_NUM));
        assertEquals(0,basis.getIntArray(createRepeatingDialog.BUNDLE_DAY_OF_WEEK)[0]);
        assertEquals(0, basis.getIntArray(createRepeatingDialog.BUNDLE_DAY_OF_WEEK)[1]);
        assertEquals(0,basis.getIntArray(createRepeatingDialog.BUNDLE_DAY_OF_WEEK)[2]);
        assertEquals(0,basis.getIntArray(createRepeatingDialog.BUNDLE_DAY_OF_WEEK)[3]);
        assertEquals(0,basis.getIntArray(createRepeatingDialog.BUNDLE_DAY_OF_WEEK)[4]);
        assertEquals(0,basis.getIntArray(createRepeatingDialog.BUNDLE_DAY_OF_WEEK)[5]);
        assertEquals(0,basis.getIntArray(createRepeatingDialog.BUNDLE_DAY_OF_WEEK)[6]);

        String today = "";
        Calendar cal = Calendar.getInstance();
        today += cal.get(Calendar.YEAR) + "-";
        if(cal.get(Calendar.MONTH)<9)today += "0";
        today += (cal.get(Calendar.MONTH)+1) + "-";
        if (cal.get(Calendar.DAY_OF_MONTH)<10)today += "0";
        today += cal.get(Calendar.DAY_OF_MONTH);
        assertEquals(today, basis.getString(createRepeatingDialog.BUNDLE_START_DATE));
        assertEquals(today, basis.getString(createRepeatingDialog.BUNDLE_END_DATE));
    }
    @Test
    public void testGetBasis4_2(){
        int originalPeriodicalNumber = 0;
        int originalPeriodicalPeriod = 0;
        int originalOrdinalNumber = 1;
        int originalOrdinalPeriod = 1;
        int[] originalDayOfWeek = {0,0,0,0,0,0,0};
        String originalStartDate = "2000-01-01";
        String originalEndDate = "2000-01-02";
        ContentValues values = new ContentValues();
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_PERIODICAL_NUM, originalPeriodicalNumber);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_PERIODICAL_PERIOD, originalPeriodicalPeriod);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_ORDINAL_NUM, originalOrdinalNumber);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_ORDINAL_PERIOD, originalOrdinalPeriod);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_SUNDAY, originalDayOfWeek[0]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_MONDAY, originalDayOfWeek[1]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_TUESDAY, originalDayOfWeek[2]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_WEDNESDAY, originalDayOfWeek[3]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_THURSDAY, originalDayOfWeek[4]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_FRIDAY, originalDayOfWeek[5]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_SATURDAY, originalDayOfWeek[6]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_START_DATE, originalStartDate);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_END_DATE, originalEndDate);
        long id = dbW.insert(TaskDBContract.TaskDB.REPEATING_TABLE_NAME, null, values);
        Bundle basis = repeatingBasisEditor.getBasis((int)id);
        assertEquals(originalPeriodicalNumber, basis.getInt(createRepeatingDialog.BUNDLE_PERIODICAL_NUM));
        assertEquals(originalPeriodicalPeriod, basis.getInt(createRepeatingDialog.BUNDLE_PERIODICAL_PERIOD));
        assertEquals(originalOrdinalNumber, basis.getInt(createRepeatingDialog.BUNDLE_ORDINAL_NUM));
        assertEquals(originalOrdinalPeriod, basis.getInt(createRepeatingDialog.BUNDLE_ORDINAL_PERIOD));
        assertEquals(originalDayOfWeek[0], basis.getIntArray(createRepeatingDialog.BUNDLE_DAY_OF_WEEK)[0]);
        assertEquals(originalDayOfWeek[1], basis.getIntArray(createRepeatingDialog.BUNDLE_DAY_OF_WEEK)[1]);
        assertEquals(originalDayOfWeek[2], basis.getIntArray(createRepeatingDialog.BUNDLE_DAY_OF_WEEK)[2]);
        assertEquals(originalDayOfWeek[3], basis.getIntArray(createRepeatingDialog.BUNDLE_DAY_OF_WEEK)[3]);
        assertEquals(originalDayOfWeek[4], basis.getIntArray(createRepeatingDialog.BUNDLE_DAY_OF_WEEK)[4]);
        assertEquals(originalDayOfWeek[5], basis.getIntArray(createRepeatingDialog.BUNDLE_DAY_OF_WEEK)[5]);
        assertEquals(originalDayOfWeek[6], basis.getIntArray(createRepeatingDialog.BUNDLE_DAY_OF_WEEK)[6]);
        assertEquals(originalStartDate, basis.getString(createRepeatingDialog.BUNDLE_START_DATE));
        assertEquals(originalEndDate, basis.getString(createRepeatingDialog.BUNDLE_END_DATE));
    }

    @Test
    public void testDeleteBasis5_1(){
        int originalPeriodicalNumber = 0;
        int originalPeriodicalPeriod = 0;
        int originalOrdinalNumber = 1;
        int originalOrdinalPeriod = 1;
        int[] originalDayOfWeek = {0,0,0,0,0,0,0};
        String originalStartDate = "2000-01-01";
        String originalEndDate = "2000-01-02";
        ContentValues values = new ContentValues();
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_PERIODICAL_NUM, originalPeriodicalNumber);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_PERIODICAL_PERIOD, originalPeriodicalPeriod);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_ORDINAL_NUM, originalOrdinalNumber);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_ORDINAL_PERIOD, originalOrdinalPeriod);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_SUNDAY, originalDayOfWeek[0]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_MONDAY, originalDayOfWeek[1]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_TUESDAY, originalDayOfWeek[2]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_WEDNESDAY, originalDayOfWeek[3]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_THURSDAY, originalDayOfWeek[4]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_FRIDAY, originalDayOfWeek[5]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_SATURDAY, originalDayOfWeek[6]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_START_DATE, originalStartDate);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_END_DATE, originalEndDate);
        long id = dbW.insert(TaskDBContract.TaskDB.REPEATING_TABLE_NAME, null, values);
        repeatingBasisEditor.deleteBasis((int) id);
        String[] projection = {
                TaskDBContract.TaskDB._ID,
                TaskDBContract.TaskDB.COLUMN_NAME_PERIODICAL_NUM,
                TaskDBContract.TaskDB.COLUMN_NAME_PERIODICAL_PERIOD,
                TaskDBContract.TaskDB.COLUMN_NAME_ORDINAL_NUM,
                TaskDBContract.TaskDB.COLUMN_NAME_ORDINAL_PERIOD,
                TaskDBContract.TaskDB.COLUMN_NAME_SUNDAY,
                TaskDBContract.TaskDB.COLUMN_NAME_MONDAY,
                TaskDBContract.TaskDB.COLUMN_NAME_TUESDAY,
                TaskDBContract.TaskDB.COLUMN_NAME_WEDNESDAY,
                TaskDBContract.TaskDB.COLUMN_NAME_THURSDAY,
                TaskDBContract.TaskDB.COLUMN_NAME_FRIDAY,
                TaskDBContract.TaskDB.COLUMN_NAME_SATURDAY,
                TaskDBContract.TaskDB.COLUMN_NAME_START_DATE,
                TaskDBContract.TaskDB.COLUMN_NAME_END_DATE
        };

        Cursor cursor = dbR.query(
                TaskDBContract.TaskDB.REPEATING_TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                TaskDBContract.TaskDB._ID + "=?",         // The columns for the WHERE clause
                new String[]{String.valueOf(id)},                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );
        cursor.moveToFirst();
        assertTrue(cursor.isAfterLast());
    }

    @Test
    public void testDeleteBasis5_2(){
        int originalPeriodicalNumber = 0;
        int originalPeriodicalPeriod = 0;
        int originalOrdinalNumber = 1;
        int originalOrdinalPeriod = 1;
        int[] originalDayOfWeek = {0,0,0,0,0,0,0};
        String originalStartDate = "2000-01-01";
        String originalEndDate = "2000-01-02";
        ContentValues values = new ContentValues();
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_PERIODICAL_NUM, originalPeriodicalNumber);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_PERIODICAL_PERIOD, originalPeriodicalPeriod);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_ORDINAL_NUM, originalOrdinalNumber);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_ORDINAL_PERIOD, originalOrdinalPeriod);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_SUNDAY, originalDayOfWeek[0]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_MONDAY, originalDayOfWeek[1]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_TUESDAY, originalDayOfWeek[2]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_WEDNESDAY, originalDayOfWeek[3]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_THURSDAY, originalDayOfWeek[4]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_FRIDAY, originalDayOfWeek[5]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_SATURDAY, originalDayOfWeek[6]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_START_DATE, originalStartDate);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_END_DATE, originalEndDate);
        long id = dbW.insert(TaskDBContract.TaskDB.REPEATING_TABLE_NAME, null, values);
        repeatingBasisEditor.deleteBasis((int) id + 1);
        String[] projection = {
                TaskDBContract.TaskDB._ID,
                TaskDBContract.TaskDB.COLUMN_NAME_PERIODICAL_NUM,
                TaskDBContract.TaskDB.COLUMN_NAME_PERIODICAL_PERIOD,
                TaskDBContract.TaskDB.COLUMN_NAME_ORDINAL_NUM,
                TaskDBContract.TaskDB.COLUMN_NAME_ORDINAL_PERIOD,
                TaskDBContract.TaskDB.COLUMN_NAME_SUNDAY,
                TaskDBContract.TaskDB.COLUMN_NAME_MONDAY,
                TaskDBContract.TaskDB.COLUMN_NAME_TUESDAY,
                TaskDBContract.TaskDB.COLUMN_NAME_WEDNESDAY,
                TaskDBContract.TaskDB.COLUMN_NAME_THURSDAY,
                TaskDBContract.TaskDB.COLUMN_NAME_FRIDAY,
                TaskDBContract.TaskDB.COLUMN_NAME_SATURDAY,
                TaskDBContract.TaskDB.COLUMN_NAME_START_DATE,
                TaskDBContract.TaskDB.COLUMN_NAME_END_DATE
        };

        Cursor cursor = dbR.query(
                TaskDBContract.TaskDB.REPEATING_TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                TaskDBContract.TaskDB._ID + "=?",         // The columns for the WHERE clause
                new String[]{String.valueOf(id)},                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );
        cursor.moveToFirst();
        assertTrue(!cursor.isAfterLast());
    }

    @Test
    public void testGetDailyBasis6_1(){
        Calendar calStart = Calendar.getInstance();
        calStart.set(2000,0,1);
        Calendar calEnd = Calendar.getInstance();
        calEnd.set(2000, 0, 3);
        ArrayList<String> result = repeatingBasisEditor.getDailyBasis(calStart, calEnd, 2);
        assertEquals("2000-01-01",result.get(0));
        assertEquals("2000-01-03",result.get(1));
    }

    @Test
    public void testGetWeeklyBasis7_1(){
        Calendar calStart = Calendar.getInstance();
        calStart.set(2015,10,8);
        Calendar calEnd = Calendar.getInstance();
        calEnd.set(2015, 10, 22);
        ArrayList<String> result = repeatingBasisEditor.getWeeklyBasis(calStart,calEnd,2,1,1,1,1,1,1,1);
        assertEquals("2015-11-08",result.get(0));
        assertEquals("2015-11-09",result.get(1));
        assertEquals("2015-11-10",result.get(2));
        assertEquals("2015-11-11",result.get(3));
        assertEquals("2015-11-12", result.get(4));
        assertEquals("2015-11-13", result.get(5));
        assertEquals("2015-11-14",result.get(6));
        assertEquals("2015-11-22",result.get(7));
    }
    @Test
    public void testGetWeeklyBasis7_2(){
        Calendar calStart = Calendar.getInstance();
        calStart.set(2015, 10, 14);
        Calendar calEnd = Calendar.getInstance();
        calEnd.set(2015, 10, 14);
        ArrayList<String> result = repeatingBasisEditor.getWeeklyBasis(calStart,calEnd,2,0,0,0,0,0,0,0);
        assertTrue(result.isEmpty());
    }
    @Test
    public void testGetDayOfMonthBasis8_1(){
        Calendar calStart = Calendar.getInstance();
        calStart.set(2015, 10, 1);
        Calendar calEnd = Calendar.getInstance();
        calEnd.set(2015, 10, 1);
        ArrayList<String> result = repeatingBasisEditor.getDayOfMonthBasis(calStart, calEnd, 30);
        assertTrue(result.isEmpty());
    }
    @Test
    public void testGetDayOfMonthBasis8_2(){
        Calendar calStart = Calendar.getInstance();
        calStart.set(2015, 9, 1);
        Calendar calEnd = Calendar.getInstance();
        calEnd.set(2015, 11, 1);
        ArrayList<String> result = repeatingBasisEditor.getDayOfMonthBasis(calStart,calEnd,31);
        assertEquals("2015-10-31", result.get(0));
    }
    @Test
    public void testGetDayOfMonthBasis8_3(){
        Calendar calStart = Calendar.getInstance();
        calStart.set(2015, 8, 30);
        Calendar calEnd = Calendar.getInstance();
        calEnd.set(2015, 11, 31);
        ArrayList<String> result = repeatingBasisEditor.getDayOfMonthBasis(calStart,calEnd,31);
        assertEquals("2015-10-31", result.get(0));
        assertEquals("2015-12-31",result.get(1));
    }
    @Test
    public void testGetDayOfWeekInMonthBasis9_1(){
        Calendar calStart = Calendar.getInstance();
        calStart.set(2015, 10, 2);
        Calendar calEnd = Calendar.getInstance();
        calEnd.set(2015, 11, 31);
        ArrayList<String> result = repeatingBasisEditor.getDayOfWeekInMonthBasis(calStart,calEnd,1,1);
        assertEquals("2015-12-06",result.get(0));
    }
    @Test
    public void testGetLastDayOfMonthBasis10_1(){
        Calendar calStart = Calendar.getInstance();
        calStart.set(2015, 10, 1);
        Calendar calEnd = Calendar.getInstance();
        calEnd.set(2015, 11, 1);
        ArrayList<String> result = repeatingBasisEditor.getLastDayOfMonthBasis(calStart, calEnd);
        assertEquals("2015-11-30",result.get(0));
    }
    @Test
     public void testGetLastWeekOfMonthBasis12_1(){
        Calendar calStart = Calendar.getInstance();
        calStart.set(2015, 10, 1);
        Calendar calEnd = Calendar.getInstance();
        calEnd.set(2016, 0, 1);
        ArrayList<String> result = repeatingBasisEditor.getLastWeekInMonthBasis(calStart, calEnd, 1);
        assertEquals("2015-11-29",result.get(0));
        assertEquals("2015-12-27",result.get(1));
    }
    @Test
    public void testGetLastWeekOfMonthBasis12_2(){
        Calendar calStart = Calendar.getInstance();
        calStart.set(2015, 10, 1);
        Calendar calEnd = Calendar.getInstance();
        calEnd.set(2016, 0, 1);
        ArrayList<String> result = repeatingBasisEditor.getLastWeekInMonthBasis(calStart,calEnd,7);
        assertEquals("2015-11-28",result.get(0));
        assertEquals("2015-12-26",result.get(1));
    }
    @Test
    public void testGetMonthInYearBasis11_1(){
        Calendar calStart = Calendar.getInstance();
        calStart.set(2015, 4, 1);
        Calendar calEnd = Calendar.getInstance();
        calEnd.set(2017, 0, 1);
        ArrayList<String> result = repeatingBasisEditor.getMonthInYearBasis(calStart,calEnd,2);
        assertEquals("2016-02-01",result.get(0));
    }

}