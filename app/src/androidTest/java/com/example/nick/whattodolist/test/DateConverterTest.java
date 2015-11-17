package com.example.nick.whattodolist.test;

import android.test.ActivityInstrumentationTestCase2;

import com.example.nick.whattodolist.MainToDo;
import com.example.nick.whattodolist.dateConverter;

import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;

/**
 * Created by Nicole on 11/14/2015.
 */
public class DateConverterTest extends ActivityInstrumentationTestCase2<MainToDo> {

    public DateConverterTest() {
        super(MainToDo.class);
    }


    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        getInstrumentation().waitForIdleSync();
    }
    @Test
    public void testCalendarToString1_1(){
        Calendar cal = Calendar.getInstance();
        cal.set(2015, 10, 12);
        String result = dateConverter.calendarToString(cal);
        assertEquals("11/12/2015", result);
    }
    @Test
    public void testCalendarToString1_2(){
        Calendar cal = Calendar.getInstance();
        cal.set(2015,0,2);
        String result = dateConverter.calendarToString(cal);
        assertEquals("01/02/2015",result);
    }
    @Test
    public void testStringToCalendar2_1(){
        String dateString = "11/12/2015";
        Calendar cal = dateConverter.stringToCalendar(dateString);
        assertEquals(2015,cal.get(Calendar.YEAR));
        assertEquals(10, cal.get(Calendar.MONTH));
        assertEquals(12, cal.get(Calendar.DAY_OF_MONTH));
    }
    @Test
    public void testCalendarToSql3_1(){
        Calendar cal = Calendar.getInstance();
        cal.set(2015, 10, 12);
        String result = dateConverter.calendarToSql(cal);
        assertEquals("2015-11-12", result);
    }
    @Test
    public void testCalendarToSql3_2(){
        Calendar cal = Calendar.getInstance();
        cal.set(2015, 0, 2);
        String result = dateConverter.calendarToSql(cal);
        assertEquals("2015-01-02", result);
    }
    @Test
    public void testSqlToCalendar4_1(){
        String sqlString = "2015-11-12";
        Calendar cal = dateConverter.sqlToCalendar(sqlString);
        assertEquals(2015,cal.get(Calendar.YEAR));
        assertEquals(10, cal.get(Calendar.MONTH));
        assertEquals(12, cal.get(Calendar.DAY_OF_MONTH));
    }
    @Test
    public void testStringToSql5_1(){
        String dateString = "11/12/2015";
        String sqlString = dateConverter.stringToSql(dateString);
        assertEquals("2015-11-12", sqlString);
    }
    @Test
    public void testSqlToString6_1(){
        String sqlString = "2015-11-12";
        String dateString = dateConverter.sqlToString(sqlString);
        assertEquals("11/12/2015", dateString);
    }
}
