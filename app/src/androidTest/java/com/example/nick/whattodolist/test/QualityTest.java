package com.example.nick.whattodolist.test;

/**
 * Created by Nicole on 11/10/2015.
 */
import android.test.ActivityInstrumentationTestCase2;
import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import com.example.nick.whattodolist.MainToDo;
import com.example.nick.whattodolist.repeatingBasisEditor;

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class QualityTest extends ActivityInstrumentationTestCase2<MainToDo> {
    MainToDo activity;
    Context mContext;
    repeatingBasisEditor repeatingEditor;

    public QualityTest(Class<MainToDo> activityClass) {
        super(activityClass);
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

    @Test
    public void testGetDailyBasis1(){
        Calendar calStart = Calendar.getInstance();
        Calendar calTmp = Calendar.getInstance();
        Calendar calEnd = Calendar.getInstance();
        int nthDay = Integer.MAX_VALUE;
        calStart.set(2000, 1, 1);
        calTmp.set(2000,1,1);
        calEnd.set(3000, 1, 1);
        ArrayList<String> result = repeatingEditor.getDayOfMonthBasisOld(calStart,calEnd,calTmp, nthDay);
        Assert.assertTrue(result.size() >= 1);
        for(String entry:result){
            Assert.assertEquals(entry.split("-").length, 3);
        }
    }

    @Test
    public void testGetDailyBasis2(){
        Object calStart = new Date();
        Calendar calTmp = Calendar.getInstance();
        Calendar calEnd = Calendar.getInstance();
        int nthDay = Integer.MAX_VALUE;
        calTmp.set(2000,1,1);
        calEnd.set(3000,1,1);
        repeatingEditor.getDayOfMonthBasisOld((Calendar)calStart,calEnd,calTmp, nthDay);
    }

    @Test
    public void testGetDailyBasis3(){
        Calendar calStart = Calendar.getInstance();
        Object calTmp = new Date();
        Calendar calEnd = Calendar.getInstance();
        int nthDay = Integer.MAX_VALUE;
        calStart.set(2000, 1, 1);
        calEnd.set(3000,1,1);
        repeatingEditor.getDayOfMonthBasisOld(calStart,calEnd,(Calendar)calTmp, nthDay);
    }

    @Test
    public void testGetDailyBasis4(){
        Calendar calStart = Calendar.getInstance();
        Calendar calTmp = Calendar.getInstance();
        Object calEnd = new Date();
        int nthDay = Integer.MAX_VALUE;
        calStart.set(2000, 1, 1);
        calTmp.set(2000,1,1);
        repeatingEditor.getDayOfMonthBasisOld(calStart,(Calendar)calEnd,calTmp, nthDay);
    }

    @Test
    public void testGetDailyBasis5(){
        Calendar calStart = Calendar.getInstance();
        Calendar calTmp = Calendar.getInstance();
        Calendar calEnd = Calendar.getInstance();
        long nthDay = Integer.MIN_VALUE + 1;
        calStart.set(2000, 1, 1);
        calTmp.set(2000,1,1);
        calEnd.set(3000,1,1);
        repeatingEditor.getDayOfMonthBasisOld(calStart,calEnd,calTmp, (int)nthDay);
    }

    @Test
    public void testGetDailyBasis6(){
        Calendar calStart = Calendar.getInstance();
        Calendar calTmp = Calendar.getInstance();
        Calendar calEnd = Calendar.getInstance();
        long nthDay = Integer.MAX_VALUE + 1;
        calStart.set(2000, 1, 1);
        calTmp.set(2000,1,1);
        calEnd.set(3000, 1, 1);
        repeatingEditor.getDayOfMonthBasisOld(calStart,calEnd,calTmp, (int)nthDay);
    }

    @Test
     public void testGetDailyBasisDataFlow1(){
        Calendar calStart = Calendar.getInstance();
        Calendar calTmp = Calendar.getInstance();
        Calendar calEnd = Calendar.getInstance();
        int nthDay = 31;
        calStart.set(2000, 0, 2);
        calTmp.set(2000,0,2);
        calEnd.set(2000, 4, 2);
        ArrayList<String> result = repeatingEditor.getDayOfMonthBasisOld(calStart,calEnd,calTmp, nthDay);
        assertEquals(result.size(), 2);
        assertEquals(result.get(0), "2000-1-31");
        assertEquals(result.get(1), "2000-3-31");
    }
    @Test
    public void testGetDailyBasisDataFlow2(){
        Calendar calStart = Calendar.getInstance();
        Calendar calTmp = Calendar.getInstance();
        Calendar calEnd = Calendar.getInstance();
        int nthDay = 1;
        calStart.set(2000, 0, 2);
        calTmp.set(2000,0,2);
        calEnd.set(2000, 1, 2);
        ArrayList<String> result = repeatingEditor.getDayOfMonthBasisOld(calStart,calEnd,calTmp, nthDay);
        assertEquals(result.size(), 1);
        assertEquals(result.get(0),"2000-2-1");
    }
    @Test
    public void testGetDailyBasisDataFlow3(){
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
    public void testGetDailyMultipleCondition17(){
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
    public void testGetDailyMultipleCondition55(){
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
    public void testGetDailyBasisBoundryInterior1(){
        Calendar calStart = Calendar.getInstance();
        Calendar calTmp = Calendar.getInstance();
        Calendar calEnd = Calendar.getInstance();
        int nthDay = Integer.MAX_VALUE;
        calStart.set(2000, 1, 1);
        calTmp.set(2000,1,1);
        calEnd.set(3000,1,1);
        repeatingEditor.getDayOfMonthBasisOld(calStart,calEnd,calTmp, nthDay);
    }
    @Test
    public void testGetDailyBoundryInterior2(){
        Calendar calStart = Calendar.getInstance();
        Calendar calTmp = Calendar.getInstance();
        Calendar calEnd = Calendar.getInstance();
        int nthDay = Integer.MAX_VALUE;
        calStart.set(2000, 1, 1);
        calTmp.set(2000,1,1);
        calEnd.set(3000,1,1);
        repeatingEditor.getDayOfMonthBasisOld(calStart,calEnd,calTmp, nthDay);
    }
    @Test
    public void testGetDailyBoundryInterior3(){
        Calendar calStart = Calendar.getInstance();
        Calendar calTmp = Calendar.getInstance();
        Calendar calEnd = Calendar.getInstance();
        int nthDay = Integer.MAX_VALUE;
        calStart.set(2000, 1, 1);
        calTmp.set(2000,1,1);
        calEnd.set(3000,1,1);
        repeatingEditor.getDayOfMonthBasisOld(calStart,calEnd,calTmp, nthDay);
    }
    @Test
    public void testCallOrder(){

    }


}
