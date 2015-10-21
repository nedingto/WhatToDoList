package com.example.nick.whattodolist;

import java.util.Calendar;

/**
 * Created by Nick on 10/19/2015.
 */
public class dateConverter {
    public static final String firstDateSql = "1970-01-01";
    //classes for converting between different date formats
    public static String calendarToString(Calendar cal){
        String dateString = "";
        String  y = String.valueOf(cal.get(Calendar.YEAR));
        String m = String.valueOf((cal.get(Calendar.MONTH))+1);
        //have to adjust for different indexes
        if(cal.get(Calendar.MONTH)<9) m = "0" + m;
        String d = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
        if(cal.get(Calendar.DAY_OF_MONTH)<10) d = "0" + d;
        dateString = d + "/" + m + "/" + y;
        return dateString;
    }
    public static Calendar stringToCalendar(String dateString){
        Calendar cal = Calendar.getInstance();
        String[] pieces = dateString.split("/");
        cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(pieces[0]));
        cal.set(Calendar.MONTH, Integer.parseInt(pieces[1]) - 1);
        cal.set(Calendar.YEAR, Integer.parseInt(pieces[2]));

        return cal;
    }
    public static String calendarToSql(Calendar cal){
        String sqlDate = "";
        String  year = String.valueOf(cal.get(Calendar.YEAR));
        String month = String.valueOf((cal.get(Calendar.MONTH) + 1));
        if(cal.get(Calendar.MONTH)<9) month = "0" + month;
        String day = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
        if(cal.get(Calendar.DAY_OF_MONTH)<10) day = "0" + day;
        sqlDate = year + "-" + month + "-" + day;
        return sqlDate;
    }
    public static Calendar sqlToCalendar(String sqlDate){
        Calendar cal = Calendar.getInstance();
        String[] pieces = sqlDate.split("-");
        cal.set(Calendar.YEAR, Integer.parseInt(pieces[0]));
        cal.set(Calendar.MONTH, Integer.parseInt(pieces[1]) - 1);
        cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(pieces[2]));
        return cal;
    }
    public static String stringToSql(String dateString){
        String sqlDate = "";
        String[] pieces = dateString.split("/");
        sqlDate  = pieces[2] + "-" + pieces[1] + "-" + pieces[0];
        return sqlDate;
    }
    public static String sqlToString(String sqlDate){
        String dateString = "";
        String[] pieces = sqlDate.split("-");
        dateString = pieces[2] + "/" + pieces[1] + "/" + pieces[0];
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
        cal.add(Calendar.DAY_OF_MONTH, dayDifference + (7 * week));
        return cal;
    }
}
