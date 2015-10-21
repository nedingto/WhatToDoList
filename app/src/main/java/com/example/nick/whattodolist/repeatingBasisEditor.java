package com.example.nick.whattodolist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Nick on 10/13/2015.
 */
public class RepeatingBasisEditor {
    TaskDBContract dbContract;
    TaskDbHelper mDbHelper;
    SQLiteDatabase dbR;
    SQLiteDatabase dbW;

    Context c;

    public RepeatingBasisEditor(Context context) {
        c = context;
        dbContract = new TaskDBContract();
        mDbHelper = new TaskDbHelper(context);

        //set up reader and writer
        dbR = mDbHelper.getReadableDatabase();
        dbW = mDbHelper.getWritableDatabase();
    }

    public long createBasis(int periodicalNum, int periodicalPeriod, int ordinalNum,
                            int ordinalPeriod, int[] dayOfWeek, String startDate, String endDate) {
        //this adds a row to the repeating basis table specified by the input params
        long basisId = -1;
        ContentValues values = new ContentValues();
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_PERIODICAL_NUM, periodicalNum);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_PERIODICAL_PERIOD, periodicalPeriod);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_ORDINAL_NUM, ordinalNum);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_ORDINAL_PERIOD, ordinalPeriod);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_SUNDAY, dayOfWeek[0]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_MONDAY, dayOfWeek[1]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_TUESDAY, dayOfWeek[2]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_WEDNESDAY, dayOfWeek[3]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_THURSDAY, dayOfWeek[4]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_FRIDAY, dayOfWeek[5]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_SATURDAY, dayOfWeek[6]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_START_DATE, startDate);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_END_DATE, endDate);


        basisId = dbW.insert(
                TaskDBContract.TaskDB.REPEATING_TABLE_NAME,
                null,
                values);
        return basisId;
    }

    public ArrayList<String> getBasisDates(float basisId) {
        //returns all of the dates the basis refers to
        ArrayList<String> basisDates = new ArrayList<>();
        Calendar calStart;
        Calendar calTmp;
        Calendar calEnd;

        //get the basis
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
                TaskDBContract.TaskDB.REPEATING_TABLE_NAME + "." + TaskDBContract.TaskDB._ID + "=?",         // The columns for the WHERE clause
                new String[]{String.valueOf(basisId)},                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );

        //there should be exatly one here
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            int periodicalNum = cursor.getInt(cursor.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_PERIODICAL_NUM));
            int periodicalPeriod = cursor.getInt(cursor.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_PERIODICAL_PERIOD));
            int ordinalNum = cursor.getInt(cursor.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_ORDINAL_NUM));
            int ordinalPeriod = cursor.getInt(cursor.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_ORDINAL_PERIOD));
            int sunday = cursor.getInt(cursor.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_SUNDAY));
            int monday = cursor.getInt(cursor.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_MONDAY));
            int tuesday = cursor.getInt(cursor.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_TUESDAY));
            int wednesday = cursor.getInt(cursor.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_WEDNESDAY));
            int thursday = cursor.getInt(cursor.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_THURSDAY));
            int friday = cursor.getInt(cursor.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_FRIDAY));
            int saturday = cursor.getInt(cursor.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_SATURDAY));
            String startDate = cursor.getString(cursor.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_START_DATE));
            String endDate = cursor.getString(cursor.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_END_DATE));

            //set the start and end dates and a temp calendar for later use
            calStart = dateConverter.sqlToCalendar(startDate);
            calTmp = dateConverter.sqlToCalendar(startDate);
            calEnd = dateConverter.sqlToCalendar(endDate);

            if (periodicalPeriod == c.getResources().getInteger(R.integer.periodical_period_day)) {
                //this would be day
                basisDates = getDailyBasis(calStart, calEnd, calTmp, periodicalNum);
            } else if (periodicalPeriod == c.getResources().getInteger(R.integer.periodical_period_week)) {
                //this would be week
                basisDates = getWeeklyBasis(calStart, calEnd, calTmp, periodicalNum, sunday,
                        monday, tuesday, wednesday, thursday, friday, saturday);
            } else if (periodicalPeriod == c.getResources().getInteger(R.integer.periodical_period_month)) {
                if (ordinalNum != 0) {
                    if (ordinalPeriod == c.getResources().getInteger(R.integer.ordinal_period_day_or_month)) {
                        basisDates = getDayOfMonthBasis(calStart, calEnd, calTmp, ordinalNum);
                    } else {
                        //the ordinal period is a day of the week that is wanted
                        basisDates = getDayOfWeekInMonthBasis(calStart, calEnd, calTmp,
                                ordinalPeriod, ordinalNum);
                    }
                } else {
                    //this means last was selected
                    if (ordinalPeriod == c.getResources().getInteger(R.integer.ordinal_period_day_or_month)) {
                        basisDates = getLastDayOfMonthBasis(calStart, calEnd, calTmp);
                    } else {
                        basisDates = getLastWeekInMonthBasis(calStart, calEnd, calTmp, ordinalPeriod);
                    }
                }
            } else {
                //the user has selected a year basis
                basisDates = getMonthInYearBasis(calStart, calEnd, calTmp, ordinalNum);
            }
        }
        return basisDates;
    }

    public ArrayList<Integer> getBasisTaskIds(float basisId, String statDate) {
        //return array list of the taskIds associated with this task
        ArrayList<Integer> basisTaskIds = new ArrayList<>();
        String[] projection = {
                TaskDBContract.TaskDB._ID,
                TaskDBContract.TaskDB.COLUMN_NAME_REPEATING_BASIS,
                TaskDBContract.TaskDB.COLUMN_NAME_DUE_DATE
        };

        //only get tasks after the given start date
        String selection = "(" + TaskDBContract.TaskDB.TASK_TABLE_NAME + "." +
                TaskDBContract.TaskDB.COLUMN_NAME_REPEATING_BASIS + "= " +
                String.valueOf(basisId) + " AND date(" + TaskDBContract.TaskDB.COLUMN_NAME_DUE_DATE +
                ") >= date('" + statDate + "'))";


        Cursor cursor = dbR.query(
                TaskDBContract.TaskDB.TASK_TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                selection,         // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            basisTaskIds.add(cursor.getInt(cursor.getColumnIndex(TaskDBContract.TaskDB._ID)));
            cursor.moveToNext();
        }
        return basisTaskIds;
    }

    public void deleteBasis(int basisId) {
        //check if there are any remaining tasks associated
        String[] projection = {
                TaskDBContract.TaskDB._ID,
                TaskDBContract.TaskDB.COLUMN_NAME_REPEATING_BASIS
        };

        String selection = "(" + TaskDBContract.TaskDB.TASK_TABLE_NAME + "." +
                TaskDBContract.TaskDB.COLUMN_NAME_REPEATING_BASIS + "= " +
                String.valueOf(basisId) + ")";


        Cursor cursor = dbR.query(
                TaskDBContract.TaskDB.TASK_TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                selection,         // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );
        cursor.moveToFirst();
        //if the return list is empty delete the basis
        if (cursor.isAfterLast()) {
            selection = TaskDBContract.TaskDB._ID + " = " + basisId;
            dbR.delete(TaskDBContract.TaskDB.REPEATING_TABLE_NAME, selection, null);
        }
    }

    public Bundle getBasis(int basisId) {
        //returns a bundle corresponding to the given id
        Bundle basis = new Bundle();
        //if the basis id is null, return a initialized basis
        if (basisId == -1) {
            basis.putInt(createRepeatingDialog.BUNDLE_PERIODICAL_NUM, 0);
            basis.putInt(createRepeatingDialog.BUNDLE_PERIODICAL_PERIOD, 0);
            basis.putInt(createRepeatingDialog.BUNDLE_ORDINAL_NUM, 0);
            basis.putInt(createRepeatingDialog.BUNDLE_ORDINAL_PERIOD, 0);
            basis.putIntArray(createRepeatingDialog.BUNDLE_DAY_OF_WEEK, new int[]{0, 0, 0, 0, 0, 0, 0});
            Calendar c = Calendar.getInstance();
            //this method should return as if it came from an sql query
            String today = dateConverter.calendarToSql(c);
            basis.putString(createRepeatingDialog.BUNDLE_START_DATE, today);
            basis.putString(createRepeatingDialog.BUNDLE_END_DATE, today);
        } else {
            //otherwise query the database and return the basis in a bundle
            String[] projection = new String[]{
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
            String[] ID = {String.valueOf(basisId)};

            Cursor cursor = dbR.query(
                    TaskDBContract.TaskDB.REPEATING_TABLE_NAME,  // The table to query
                    projection,                               // The columns to return
                    TaskDBContract.TaskDB._ID + "=?",                                // The columns for the WHERE clause
                    ID,                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    null                                // The sort order
            );
            cursor.moveToFirst();

            int periodicalNum = cursor.getInt(cursor.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_PERIODICAL_NUM));
            int periodicalPeriod = cursor.getInt(cursor.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_PERIODICAL_PERIOD));
            int ordinalNum = cursor.getInt(cursor.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_ORDINAL_NUM));
            int ordinalPeriod = cursor.getInt(cursor.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_ORDINAL_PERIOD));
            int sunday = cursor.getInt(cursor.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_SUNDAY));
            int monday = cursor.getInt(cursor.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_MONDAY));
            int tuesday = cursor.getInt(cursor.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_TUESDAY));
            int wednesday = cursor.getInt(cursor.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_WEDNESDAY));
            int thursday = cursor.getInt(cursor.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_THURSDAY));
            int friday = cursor.getInt(cursor.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_FRIDAY));
            int saturday = cursor.getInt(cursor.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_SATURDAY));
            String startDate = cursor.getString(cursor.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_START_DATE));
            String endDate = cursor.getString(cursor.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_END_DATE));

            int[] dayOfWeek = {sunday, monday, tuesday, wednesday, thursday, friday, saturday};

            basis.putInt(createRepeatingDialog.BUNDLE_PERIODICAL_NUM, periodicalNum);
            basis.putInt(createRepeatingDialog.BUNDLE_PERIODICAL_PERIOD, periodicalPeriod);
            basis.putInt(createRepeatingDialog.BUNDLE_ORDINAL_NUM, ordinalNum);
            basis.putInt(createRepeatingDialog.BUNDLE_ORDINAL_PERIOD, ordinalPeriod);
            basis.putIntArray(createRepeatingDialog.BUNDLE_DAY_OF_WEEK, dayOfWeek);
            basis.putString(createRepeatingDialog.BUNDLE_START_DATE, startDate);
            basis.putString(createRepeatingDialog.BUNDLE_END_DATE, endDate);

        }
        return basis;
    }

    private ArrayList<String> getDailyBasis(Calendar calStart, Calendar calEnd, Calendar calTmp,
                                            int periodicalNum) {
        ArrayList<String> basisDates = new ArrayList<>();
        while (!calTmp.after(calEnd)) {
            basisDates.add(dateConverter.calendarToSql(calTmp));
            calTmp.add(Calendar.DAY_OF_YEAR, periodicalNum);
        }
        return basisDates;
    }

    private ArrayList<String> getWeeklyBasis(Calendar calStart, Calendar calEnd, Calendar calTmp,
                                             int periodicalNum, int sunday, int monday,
                                             int tuesday, int wednesday, int thursday, int friday,
                                             int saturday) {
        ArrayList<String> basisDates = new ArrayList<>();
        while (!calTmp.after(calEnd)) {
            //if the day is marked add the date
            if (calTmp.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY && sunday == 1) {
                basisDates.add(dateConverter.calendarToSql(calTmp));
            } else if (calTmp.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY && monday == 1) {
                basisDates.add(dateConverter.calendarToSql(calTmp));
            } else if (calTmp.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY && tuesday == 1) {
                basisDates.add(dateConverter.calendarToSql(calTmp));
            } else if (calTmp.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY && wednesday == 1) {
                basisDates.add(dateConverter.calendarToSql(calTmp));
            } else if (calTmp.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY && thursday == 1) {
                basisDates.add(dateConverter.calendarToSql(calTmp));
            } else if (calTmp.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY && friday == 1) {
                basisDates.add(dateConverter.calendarToSql(calTmp));
            } else if (calTmp.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                if (saturday == 1) {
                    basisDates.add(dateConverter.calendarToSql(calTmp));
                }
                //since it is not the other days of the week it must be the end of the week,
                //so now it will move to the next specified week, decided by
                //adding seven days for each integer the periodicalNum is past 1
                calTmp.add(Calendar.DAY_OF_YEAR, (periodicalNum - 1) * 7);
            } else {
                //this should never happen
            }
            ///move on to the next day
            calTmp.add(Calendar.DAY_OF_YEAR, 1);
        }
        return basisDates;
    }

    private ArrayList<String> getDayOfMonthBasis(Calendar calStart, Calendar calEnd, Calendar calTmp,
                                                 int ordinalNum) {
        ArrayList<String> basisDates = new ArrayList<>();
        //if the start date comes before the day of the month specified and the
        //current month has the given day, then add it to the date list, otherwise skip it
        if (calTmp.get(Calendar.DAY_OF_MONTH) <= ordinalNum && ordinalNum
                <= calTmp.getActualMaximum(Calendar.DAY_OF_MONTH)) {
            calTmp.set(Calendar.DAY_OF_MONTH, ordinalNum);
            if (!calTmp.after(calEnd)) {
                basisDates.add(dateConverter.calendarToSql(calTmp));
            }
        }

        calTmp.add(Calendar.MONTH, 1);
        //if the moth does not have the day, skip that month, thee next month will have it
        if (ordinalNum > calTmp.getActualMaximum(Calendar.DAY_OF_MONTH))
            calTmp.add(Calendar.MONTH, 1);
        calTmp.set(Calendar.DAY_OF_MONTH, ordinalNum);

        //save the date and keep progressing using the same rule as above
        while (!calTmp.after(calEnd)) {
            basisDates.add(dateConverter.calendarToSql(calTmp));
            calTmp.add(Calendar.MONTH, 1);
            //if the moth does not have the day, skip that month, thee next month will have it
            if (ordinalNum > calTmp.getActualMaximum(Calendar.DAY_OF_MONTH))
                calTmp.add(Calendar.MONTH, 1);
            calTmp.set(Calendar.DAY_OF_MONTH, ordinalNum);
        }
        return basisDates;
    }

    private ArrayList<String> getDayOfWeekInMonthBasis(Calendar calStart, Calendar calEnd,
                                                       Calendar calTmp, int ordinalPeriod,
                                                       int ordinalNum) {
        ArrayList<String> basisDates = new ArrayList<>();
        dateConverter.setDayOfWeekInMonth(calTmp, ordinalPeriod, ordinalNum);


        while (!calTmp.after(calEnd)) {
            if (!calTmp.before(calStart)) {
                basisDates.add(dateConverter.calendarToSql(calTmp));
            }
            calTmp.add(Calendar.MONTH, 1);
            dateConverter.setDayOfWeekInMonth(calTmp, ordinalPeriod, ordinalNum);

        }
        dateConverter.setDayOfWeekInMonth(calTmp, ordinalPeriod, ordinalNum);


        while (!calTmp.after(calEnd)) {
            if (!calTmp.before(calStart)) {
                basisDates.add(dateConverter.calendarToSql(calTmp));
            }
            calTmp.add(Calendar.MONTH, 1);
            dateConverter.setDayOfWeekInMonth(calTmp, ordinalPeriod, ordinalNum);

        }
        return basisDates;
    }

    private ArrayList<String> getLastDayOfMonthBasis(Calendar calStart, Calendar calEnd,
                                                     Calendar calTmp){
        ArrayList<String> basisDates = new ArrayList<>();
        calTmp.set(Calendar.DAY_OF_MONTH, calTmp.getActualMaximum(Calendar.DAY_OF_MONTH));
        while (!calTmp.after(calEnd)) {
            basisDates.add(dateConverter.calendarToSql(calTmp));
            calTmp.add(Calendar.MONTH, 1);
            calTmp.set(Calendar.DAY_OF_MONTH, calTmp.getActualMaximum(Calendar.DAY_OF_MONTH));
        }
        return basisDates;
    }
    private ArrayList<String> getLastWeekInMonthBasis(Calendar calStart, Calendar calEnd,
                                                     Calendar calTmp, int ordinalPeriod){
        ArrayList<String> basisDates = new ArrayList<>();
        //the array is set up so that the indexes of the array line up with the
        //indexes of the Calendar day of the week
        int dayOfWeek = ordinalPeriod;
        calTmp.set(Calendar.DAY_OF_MONTH, calTmp.getActualMaximum(Calendar.DAY_OF_MONTH));
        int daysBack = 0;
        if (dayOfWeek > calTmp.get(Calendar.DAY_OF_WEEK)) {
            daysBack = -7 + dayOfWeek - calTmp.get(Calendar.DAY_OF_WEEK);
        } else {
            daysBack = dayOfWeek - calTmp.get(Calendar.DAY_OF_WEEK);
        }
        calTmp.add(Calendar.DAY_OF_MONTH, daysBack);
        while (!calTmp.after(calEnd)) {
            basisDates.add(dateConverter.calendarToSql(calTmp));
            calTmp.add(Calendar.MONTH, 1);
            calTmp.set(Calendar.DAY_OF_MONTH, calTmp.getActualMaximum(Calendar.DAY_OF_MONTH));
            if (dayOfWeek > calTmp.get(Calendar.DAY_OF_WEEK)) {
                daysBack = -7 + dayOfWeek - calTmp.get(Calendar.DAY_OF_WEEK);
            } else {
                daysBack = dayOfWeek - calTmp.get(Calendar.DAY_OF_WEEK);
            }
            calTmp.add(Calendar.DAY_OF_MONTH, daysBack);
        }
        return basisDates;
    }

    private ArrayList<String> getMonthInYearBasis(Calendar calStart, Calendar calEnd, Calendar calTmp,
                                                  int ordinalNum){
        ArrayList<String> basisDates = new ArrayList<>();
        calTmp.set(Calendar.MONTH, ordinalNum + 1);
        calTmp.set(Calendar.DAY_OF_MONTH, 1);
        while (!calTmp.after(calEnd)) {
            if (!calTmp.before(calStart)) {
                basisDates.add(dateConverter.calendarToSql(calTmp));
            }
            calTmp.add(Calendar.YEAR, 1);
        }
        return basisDates;
    }

}
