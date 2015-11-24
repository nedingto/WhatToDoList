package com.example.nick.whattodolist.test;

/**
 * Created by Nicole on 11/10/2015.
 */
import android.test.ActivityInstrumentationTestCase2;
import android.content.Context;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.example.nick.whattodolist.MainToDo;
import com.example.nick.whattodolist.TaskDBContract;
import com.example.nick.whattodolist.repeatingBasisEditor;

import junit.framework.Assert;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class QualityTest extends ActivityInstrumentationTestCase2<MainToDo> {
    MainToDo activity;
    Context mContext;
    repeatingBasisEditor repeatingEditor;

    public QualityTest() {
        super(MainToDo.class);
    }

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        getInstrumentation().waitForIdleSync();
        activity = getActivity();
        mContext = activity;
        repeatingEditor = new repeatingBasisEditor(mContext);
    }

    @After
    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        activity = null;
        mContext = null;
        repeatingEditor = null;
    }

    @Test
    public void testGetDayOfMonthBasis1(){
        Calendar calStart = Calendar.getInstance();
        Calendar calTmp = Calendar.getInstance();
        Calendar calEnd = Calendar.getInstance();
        int nthDay = Integer.MAX_VALUE;
        calStart.set(2000, 1, 1);
        calTmp.set(2000,1,1);
        calEnd.set(92269055, 1, 1);
        ArrayList<String> result = repeatingEditor.getDayOfMonthBasisOld(calStart,calEnd,calTmp, nthDay);
        Assert.assertTrue(result.size() >= 1);
        for(String entry:result){
            Assert.assertEquals(entry.split("-").length, 3);
        }
    }

    @Test
    public void testGetDayOfMonthBasis2(){
        Object calStart = new Date();
        Calendar calTmp = Calendar.getInstance();
        Calendar calEnd = Calendar.getInstance();
        int nthDay = Integer.MAX_VALUE;
        calTmp.set(2000,1,1);
        calEnd.set(3000, 1, 1);
        try {
            repeatingEditor.getDayOfMonthBasisOld((Calendar)calStart,calEnd,calTmp, nthDay);
            Assert.fail();
        }catch (Exception e){
            //success
        }

    }

    @Test
    public void testGetDayOfMonthBasis3(){
        Calendar calStart = Calendar.getInstance();
        Object calTmp = new Date();
        Calendar calEnd = Calendar.getInstance();
        int nthDay = Integer.MAX_VALUE;
        calStart.set(2000, 1, 1);
        calEnd.set(3000, 1, 1);
        try {
            repeatingEditor.getDayOfMonthBasisOld(calStart,calEnd,(Calendar)calTmp, nthDay);
            Assert.fail();
        }catch (Exception e){
            //success
        }
    }

    @Test
    public void testGetDayOfMonthBasis4(){
        Calendar calStart = Calendar.getInstance();
        Calendar calTmp = Calendar.getInstance();
        Object calEnd = new Date();
        int nthDay = Integer.MAX_VALUE;
        calStart.set(2000, 1, 1);
        calTmp.set(2000, 1, 1);
        try {
            repeatingEditor.getDayOfMonthBasisOld(calStart,(Calendar)calEnd,calTmp, nthDay);
            Assert.fail();
        }catch (Exception e){
            //success
        }
    }

    @Test
    public void testGetDayOfMonthBasis5(){
        Calendar calStart = Calendar.getInstance();
        Calendar calTmp = Calendar.getInstance();
        Calendar calEnd = Calendar.getInstance();
        BigInteger nthDay = new BigInteger("-2147483649");
        calStart.set(2000, 1, 1);
        calTmp.set(2000,1,1);
        calEnd.set(3000, 1, 1);
        try {
            repeatingEditor.getDayOfMonthBasisOld(calStart,calEnd,calTmp, (int)((Object)nthDay));
            Assert.fail();
        }catch (Exception e){
            //success
        }
    }

    @Test(expected = Exception.class)
    public void testGetDayOfMonthBasis6(){
        Calendar calStart = Calendar.getInstance();
        Calendar calTmp = Calendar.getInstance();
        Calendar calEnd = Calendar.getInstance();
        BigInteger nthDay = new BigInteger("2147483648");
        calStart.set(2000, 1, 1);
        calTmp.set(2000,1,1);
        calEnd.set(3000, 1, 1);
        try {
            repeatingEditor.getDayOfMonthBasisOld(calStart,calEnd,(Calendar)calTmp, (int)((Object)nthDay));
            Assert.fail();
        }catch (Exception e){
            //success
        }
    }

    @Test
     public void testGetDayOfMonthBasisDataFlow1(){
        Calendar calStart = Calendar.getInstance();
        Calendar calTmp = Calendar.getInstance();
        Calendar calEnd = Calendar.getInstance();
        int nthDay = 31;
        calStart.set(2000, 0, 2);
        calTmp.set(2000,0,2);
        calEnd.set(2000, 4, 2);
        ArrayList<String> result = repeatingEditor.getDayOfMonthBasisOld(calStart,calEnd,calTmp, nthDay);
        assertEquals(result.size(), 2);
        assertEquals(result.get(0), "2000-01-31");
        assertEquals(result.get(1), "2000-03-31");
    }
    @Test
    public void testGetDayOfMonthBasisDataFlow2(){
        Calendar calStart = Calendar.getInstance();
        Calendar calTmp = Calendar.getInstance();
        Calendar calEnd = Calendar.getInstance();
        int nthDay = 1;
        calStart.set(2000, 0, 2);
        calTmp.set(2000,0,2);
        calEnd.set(2000, 1, 2);
        ArrayList<String> result = repeatingEditor.getDayOfMonthBasisOld(calStart,calEnd,calTmp, nthDay);
        assertEquals(result.size(), 1);
        assertEquals(result.get(0),"2000-02-01");
    }
    @Test
    public void testGetDayOfMonthBasisDataFlow3(){
        Calendar calStart = Calendar.getInstance();
        Calendar calTmp = Calendar.getInstance();
        Calendar calEnd = Calendar.getInstance();
        int nthDay = 10;
        calStart.set(2000, 1, 2);
        calTmp.set(2000,1,2);
        calEnd.set(2000, 1, 2);
        ArrayList<String> result = repeatingEditor.getDayOfMonthBasisOld(calStart, calEnd, calTmp, nthDay);
        assertEquals(result.size(), 0);
    }
    @Test
    public void testGetDayOfMonthBasisMultipleCondition17(){
        Calendar calStart = Calendar.getInstance();
        Calendar calTmp = Calendar.getInstance();
        Calendar calEnd = Calendar.getInstance();
        int nthDay = 6;
        calStart.set(2000, 1, 1);
        calTmp.set(2000,1,1);
        calEnd.set(2000,2,1);
        repeatingEditor.getDayOfMonthBasisOld(calStart,calEnd,calTmp, nthDay);
    }
    @Test
    public void testGetDayOfMonthBasisMultipleCondition55(){
        Calendar calStart = Calendar.getInstance();
        Calendar calTmp = Calendar.getInstance();
        Calendar calEnd = Calendar.getInstance();
        int nthDay = 29;
        calStart.set(2000, 1, 1);
        calTmp.set(2000,1,1);
        calEnd.set(2000,3,1);
        repeatingEditor.getDayOfMonthBasisOld(calStart,calEnd,calTmp, nthDay);
    }
    @Test
    public void testGetDayOfMonthBasisBoundryInterior1(){
        Calendar calStart = Calendar.getInstance();
        Calendar calTmp = Calendar.getInstance();
        Calendar calEnd = Calendar.getInstance();
        int nthDay = 6;
        calStart.set(2000, 1, 1);
        calTmp.set(2000, 1, 1);
        calEnd.set(2000,1,1);
        ArrayList<String> result = repeatingEditor.getDayOfMonthBasisOld(calStart, calEnd, calTmp, nthDay);
        assertTrue(result.isEmpty());
    }
    @Test
    public void testGetDayOfMonthBasisBoundryInterior2(){
        Calendar calStart = Calendar.getInstance();
        Calendar calTmp = Calendar.getInstance();
        Calendar calEnd = Calendar.getInstance();
        int nthDay = 31;
        calStart.set(2000, 0, 1);
        calTmp.set(2000,0,1);
        calEnd.set(2000, 1, 1);
        ArrayList<String> result = repeatingEditor.getDayOfMonthBasisOld(calStart,calEnd,calTmp, nthDay);
        assertEquals(1, result.size());
        assertEquals("2000-01-31",result.get(0));
    }
    @Test
    public void testGetDayOfMonthBasisBoundryInterior3(){
        Calendar calStart = Calendar.getInstance();
        Calendar calTmp = Calendar.getInstance();
        Calendar calEnd = Calendar.getInstance();
        int nthDay = 5;
        calStart.set(2000, 0, 1);
        calTmp.set(2000,0,1);
        calEnd.set(2000, 2, 1);
        ArrayList<String> result = repeatingEditor.getDayOfMonthBasisOld(calStart,calEnd,calTmp, nthDay);
        assertEquals(2, result.size());
        assertEquals("2000-01-05",result.get(0));
        assertEquals("2000-02-05",result.get(1));
    }
    @Test
    public void testCallOrder(){
        repeatingEditor.getBasis(1);
        repeatingEditor.getBasisDates(1);
        repeatingEditor.getBasisTaskIds(1, "2000-01-01");
        repeatingEditor.getDailyBasis(Calendar.getInstance(), Calendar.getInstance(), 1);
        repeatingEditor.getWeeklyBasis(Calendar.getInstance(), Calendar.getInstance(), 1, 1, 1, 1, 1, 1, 1, 1);
        repeatingEditor.getDayOfMonthBasisOld(Calendar.getInstance(), Calendar.getInstance(), Calendar.getInstance(), 1);
        repeatingEditor.getDayOfWeekInMonthBasis(Calendar.getInstance(), Calendar.getInstance(), 1, 1);
        repeatingEditor.getLastDayOfMonthBasis(Calendar.getInstance(), Calendar.getInstance());
        repeatingEditor.getLastWeekInMonthBasis(Calendar.getInstance(), Calendar.getInstance(), 1);
        repeatingEditor.getMonthInYearBasis(Calendar.getInstance(), Calendar.getInstance(), 1);
        repeatingEditor.createBasis(1, 1, 1, 1, new int[]{1, 1, 1, 1, 1, 1, 1}, "2000-01-01", "2000-01-01");
        repeatingEditor.deleteBasis(1);

    }


}
