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
public class repeatingBasisEditor {


    public static long createBasis(SQLiteDatabase dbW,
                                   int periodicalNum, int periodicalPeriod, int ordinalNum,
                                   int ordinalPeriod, int[] dayOfWeek, String startDate, String endDate){
        long basisId = -1;
        ContentValues values = new ContentValues();
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_PERIODICAL_NUM,periodicalNum);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_PERIODICAL_PERIOD,periodicalPeriod);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_ORDINAL_NUM,ordinalNum);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_ORDINAL_PERIOD, ordinalPeriod);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_SUNDAY,dayOfWeek[0]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_MONDAY,dayOfWeek[1]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_TUESDAY,dayOfWeek[2]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_WEDNESDAY,dayOfWeek[3]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_THURSDAY,dayOfWeek[4]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_FRIDAY,dayOfWeek[5]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_SATURDAY,dayOfWeek[6]);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_START_DATE,startDate + " 00:00:00");
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_END_DATE,endDate + " 00:00:00");

        //hook if row id is needed later
        //might be good to have function
        //that only updates the newly created task

        basisId = dbW.insert(
                TaskDBContract.TaskDB.REPEATING_TABLE_NAME,
                null,
                values);
        return basisId;
    }
    public static ArrayList<String> getBasisDates(Context c, SQLiteDatabase dbR, float basisId){
        ArrayList<String> basisDates = new ArrayList<>();
        Calendar calStart = Calendar.getInstance();
        Calendar calTmp = Calendar.getInstance();
        Calendar calEnd = Calendar.getInstance();
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

        //if the category exists, just add its id and the tasks id to the task_category table
        //otherwise create the category and then add their ids

        Cursor cursor = dbR.query(
                    TaskDBContract.TaskDB.REPEATING_TABLE_NAME,  // The table to query
                    projection,                               // The columns to return
                TaskDBContract.TaskDB.REPEATING_TABLE_NAME + "." + TaskDBContract.TaskDB._ID + "=?",         // The columns for the WHERE clause
                    new String[] {String.valueOf(basisId)},                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    null                                 // The sort order
        );
        cursor.moveToNext();
        if(cursor.getCount() > 0) {
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

            //set the start dates and a tmp calendar for later use
            String[] date = startDate.split("-");
            int year = Integer.parseInt(date[0]);
            //have to account for difference in date picker indexes and sql date indexes
            int month = Integer.parseInt(date[1]) - 1;
            int day = Integer.parseInt(date[2].substring(0, 2));
            calStart.set(year, month, day);
            calTmp.set(year, month, day);
            date = endDate.split("-");
            year = Integer.parseInt(date[0]);
            //have to account for difference in date picker indexes and sql date indexes
            month = Integer.parseInt(date[1]) - 1;
            day = Integer.parseInt(date[2].substring(0, 2));

            calEnd.set(year, month, day);

            if (periodicalPeriod == c.getResources().getInteger(R.integer.periodical_period_day)) {
                //this would be day
                while (!calTmp.after(calEnd)) {
                    basisDates.add(getDateString(calTmp));
                    calTmp.add(Calendar.DAY_OF_YEAR, periodicalNum);
                }
            } else if (periodicalPeriod == c.getResources().getInteger(R.integer.periodical_period_week)) {
                //this would be week
                while (!calTmp.after(calEnd)) {
                    //if the day is marked add the date
                    if (calTmp.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY && sunday == 1) {
                        basisDates.add(getDateString(calTmp));
                    } else if (calTmp.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY && monday == 1) {
                        basisDates.add(getDateString(calTmp));
                    } else if (calTmp.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY && tuesday == 1) {
                        basisDates.add(getDateString(calTmp));
                    } else if (calTmp.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY && wednesday == 1) {
                        basisDates.add(getDateString(calTmp));
                    } else if (calTmp.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY && thursday == 1) {
                        basisDates.add(getDateString(calTmp));
                    } else if (calTmp.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY && friday == 1) {
                        basisDates.add(getDateString(calTmp));
                    } else if (calTmp.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY){
                        if (saturday == 1) {
                            basisDates.add(getDateString(calTmp));
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
            } else if (periodicalPeriod == c.getResources().getInteger(R.integer.periodical_period_month)) {
                if(ordinalNum !=0) {
                    if (ordinalPeriod == c.getResources().getInteger(R.integer.ordinal_period_day)) {
                        //if the start date comes before the day of the month specified and the
                        //current month has the given day, then add it to the date list, otherwise skip it
                        if (calTmp.get(Calendar.DAY_OF_MONTH) <= ordinalNum && ordinalNum <= calTmp.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                            calTmp.set(Calendar.DAY_OF_MONTH, ordinalNum);
                            if (!calTmp.after(calEnd)) {
                                basisDates.add(getDateString(calTmp));
                            }
                        }

                        calTmp.add(Calendar.MONTH, 1);
                        //if the moth does not have the day, skip that month, thee next month will have it
                        if (ordinalNum > calTmp.getActualMaximum(Calendar.DAY_OF_MONTH))
                            calTmp.add(Calendar.MONTH, 1);
                        calTmp.set(Calendar.DAY_OF_MONTH, ordinalNum);

                        //save the date and keep progressing using the same rule as above
                        while (!calTmp.after(calEnd)) {
                            basisDates.add(getDateString(calTmp));
                            calTmp.add(Calendar.MONTH, 1);
                            //if the moth does not have the day, skip that month, thee next month will have it
                            if (ordinalNum > calTmp.getActualMaximum(Calendar.DAY_OF_MONTH))
                                calTmp.add(Calendar.MONTH, 1);
                            calTmp.set(Calendar.DAY_OF_MONTH, ordinalNum);
                        }
                    } else {
                        //the ordinal period is a day of the week tht is wanted
                        //todo make this an extension of the calendar class
                        setDayOfWeekInMonth(calTmp,ordinalPeriod, ordinalNum);


                        while (!calTmp.after(calEnd)) {
                            if (!calTmp.before(calStart)) {
                                basisDates.add(getDateString(calTmp));
                            }
                            calTmp.add(Calendar.MONTH, 1);
                            setDayOfWeekInMonth(calTmp, ordinalPeriod, ordinalNum);

                        }

                    }
                } else{
                    //this means last was selected
                    if (ordinalPeriod == c.getResources().getInteger(R.integer.ordinal_period_day)) {
                        calTmp.set(Calendar.DAY_OF_MONTH, calTmp.getActualMaximum(Calendar.DAY_OF_MONTH));
                        while(!calTmp.after(calEnd)){
                            basisDates.add(getDateString(calTmp));
                            calTmp.add(Calendar.MONTH,1);
                            calTmp.set(Calendar.DAY_OF_MONTH, calTmp.getActualMaximum(Calendar.DAY_OF_MONTH));
                        }
                    } else {
                        //the array is set up so that the indexes of the array line up with the
                        //indexes of the Calendar day of the week
                        int dayOfWeek = ordinalPeriod;
                        calTmp.set(Calendar.DAY_OF_MONTH, calTmp.getActualMaximum(Calendar.DAY_OF_MONTH));
                        int daysBack = 0;
                        if(dayOfWeek > calTmp.get(Calendar.DAY_OF_WEEK)){
                            daysBack = -7 + dayOfWeek - calTmp.get(Calendar.DAY_OF_WEEK);
                        } else {
                            daysBack = dayOfWeek - calTmp.get(Calendar.DAY_OF_WEEK);
                        }
                        calTmp.add(Calendar.DAY_OF_MONTH, daysBack);
                        while(!calTmp.after(calEnd)){
                            basisDates.add(getDateString(calTmp));
                            calTmp.add(Calendar.MONTH, 1);
                            calTmp.set(Calendar.DAY_OF_MONTH, calTmp.getActualMaximum(Calendar.DAY_OF_MONTH));
                            if(dayOfWeek > calTmp.get(Calendar.DAY_OF_WEEK)){
                                daysBack = -7 + dayOfWeek - calTmp.get(Calendar.DAY_OF_WEEK);
                            } else {
                                daysBack = dayOfWeek - calTmp.get(Calendar.DAY_OF_WEEK);
                            }
                            calTmp.add(Calendar.DAY_OF_MONTH, daysBack);
                        }
                    }
                }
            } else if (periodicalPeriod == c.getResources().getInteger(R.integer.periodical_period_year)) {
                calTmp.set(Calendar.MONTH,ordinalNum+1);
                calTmp.set(Calendar.DAY_OF_MONTH, 1);
                while(!calTmp.after(calEnd)){
                    if(!calTmp.before(calStart)){
                        basisDates.add(getDateString(calTmp));
                    }
                    calTmp.add(Calendar.YEAR, 1);
                }
            }   else {
                //this should never happen
            }
        }//if it does not execute there is a problem
        return basisDates;
    }

    //this probably should be somwhere else
    public static String getDateString(Calendar cal){
        String dateString = "";
        String  year = String.valueOf(cal.get(Calendar.YEAR));
        String month = String.valueOf((cal.get(Calendar.MONTH) + 1));
        if(cal.get(Calendar.MONTH)<9) month = "0" + month;
        String day = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
        if(cal.get(Calendar.DAY_OF_MONTH)<10) day = "0" + day;
        dateString = year + "-" + month + "-" + day + " 00:00:00";
        return dateString;
    }

    public static Calendar setDayOfWeekInMonth(Calendar cal, int targetDay, int week){
        cal.set(Calendar.DAY_OF_MONTH, 1);
        int currentDay = cal.get(Calendar.DAY_OF_WEEK);
        int dayDifference = (targetDay-currentDay);
        //if the day difference is positive subtract seven
        if(dayDifference >= 0){
            dayDifference -= 7;
        }
        cal.add(Calendar.DAY_OF_MONTH, dayDifference+(7*week));
        return cal;
    }
}
