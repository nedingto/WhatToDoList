package com.example.nick.whattodolist.test;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.test.ActivityInstrumentationTestCase2;

import com.example.nick.whattodolist.Dag;
import com.example.nick.whattodolist.DagNode;
import com.example.nick.whattodolist.MainToDo;
import com.example.nick.whattodolist.TaskDBContract;
import com.example.nick.whattodolist.TaskDbHelper;
import com.example.nick.whattodolist.TaskRelationManager;
import com.example.nick.whattodolist.repeatingBasisEditor;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Nicole on 11/14/2015.
 */
public class RelationManagerTest extends ActivityInstrumentationTestCase2<MainToDo> {
    TaskRelationManager relationManager;
    MainToDo activity;
    Context mContext;
    SQLiteDatabase dbR;
    SQLiteDatabase dbW;
    String originalTitle = "task";
    String originalDate = "2000-01-01";
    int originalPriority = 1;
    int originalEstimation = 1;
    int originalChecked = 0;
    long id;
    public RelationManagerTest() {
        super(MainToDo.class);
    }


    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        getInstrumentation().waitForIdleSync();
        getInstrumentation().waitForIdleSync();
        activity = getActivity();
        mContext = activity;
        TaskDbHelper mDbHelper = new TaskDbHelper(mContext);

        //set up reader and writer
        dbR = mDbHelper.getReadableDatabase();
        dbW = mDbHelper.getWritableDatabase();
        dbR.delete(TaskDBContract.TaskDB.TASK_TABLE_NAME, null, null);
        dbR.delete(TaskDBContract.TaskDB.REPEATING_TABLE_NAME, null, null);
        dbR.delete(TaskDBContract.TaskDB.CATEGORY_TABLE_NAME, null, null);
        dbR.delete(TaskDBContract.TaskDB.TASK_RELATION_TABLE_NAME, null, null);
        dbR.delete(TaskDBContract.TaskDB.TASK_CATEGORY_TABLE_NAME, null, null);

        ContentValues values = new ContentValues();
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_TASK_NAME, originalTitle);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_DUE_DATE, originalDate);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_PRIORITY, originalPriority);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_ESTIMATED_MINS, originalEstimation);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_CHECKED, originalChecked);
        id = dbW.insert(TaskDBContract.TaskDB.TASK_TABLE_NAME,null,values);
        relationManager = new TaskRelationManager(mContext,(int)id);
    }

    @Test
    public void testGetRelationDag6_1(){
        ContentValues values = new ContentValues();
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_TASK_NAME, originalTitle);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_DUE_DATE, originalDate);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_PRIORITY, originalPriority);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_ESTIMATED_MINS, originalEstimation);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_CHECKED, originalChecked);
        long id2 = dbW.insert(TaskDBContract.TaskDB.TASK_TABLE_NAME,null,values);
        values.clear();
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_SUB_TASK, (int) id2);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_PARENT_TASK, (int) id);
        dbW.insert(TaskDBContract.TaskDB.TASK_RELATION_TABLE_NAME, null, values);
        Dag dag = relationManager.getTaskRelationDag();
        HashMap<Integer,DagNode> nodes = dag.getDagNodes();
        DagNode node1 = nodes.get((int)id);
        DagNode node2 = nodes.get((int)id2);
        assertEquals(1, node1.getSubTaskIds().size());
        assertEquals(0, node1.getParentTaskIds().size());
        assertEquals(0, node2.getSubTaskIds().size());
        assertEquals(1, node2.getParentTaskIds().size());
        assertTrue(node1.getSubTaskIds().contains(node2));
        assertTrue(node2.getParentTaskIds().contains(node1));
    }

    @Test
    public void testGetAllowedTaskIds7_1(){
        ContentValues values = new ContentValues();
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_TASK_NAME, originalTitle);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_DUE_DATE, originalDate);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_PRIORITY, originalPriority);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_ESTIMATED_MINS, originalEstimation);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_CHECKED, originalChecked);
        long id2 = dbW.insert(TaskDBContract.TaskDB.TASK_TABLE_NAME,null,values);
        long id3 = dbW.insert(TaskDBContract.TaskDB.TASK_TABLE_NAME,null,values);
        long id4 = dbW.insert(TaskDBContract.TaskDB.TASK_TABLE_NAME,null,values);
        values.clear();
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_SUB_TASK, (int) id);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_PARENT_TASK, (int) id2);
        dbW.insert(TaskDBContract.TaskDB.TASK_RELATION_TABLE_NAME, null, values);
        values.clear();
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_SUB_TASK, (int) id2);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_PARENT_TASK, (int) id3);
        dbW.insert(TaskDBContract.TaskDB.TASK_RELATION_TABLE_NAME, null, values);
        relationManager = new TaskRelationManager(mContext,(int)id);
        ArrayList<Integer> result = relationManager.getAllowedTaskIds();
        assertTrue(!result.contains((int) id2));
        assertTrue(!result.contains((int)id3));
        assertTrue(result.contains((int) id4));
        assertEquals(1,result.size());
    }
    @Test
    public void testGetAllowedTaskIds7_2(){
        relationManager = new TaskRelationManager(mContext,(int)id);
        ArrayList<Integer> result = relationManager.getAllowedTaskIds();
        assertEquals(0, result.size());
    }
    @Test
    public void testGetAllowedTaskBundle8_1(){
        ContentValues values = new ContentValues();
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_TASK_NAME, originalTitle);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_DUE_DATE, originalDate);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_PRIORITY, originalPriority);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_ESTIMATED_MINS, originalEstimation);
        values.put(TaskDBContract.TaskDB.COLUMN_NAME_CHECKED, originalChecked);
        long id2 = dbW.insert(TaskDBContract.TaskDB.TASK_TABLE_NAME,null,values);
        relationManager = new TaskRelationManager(mContext,(int)id);
        Bundle result = relationManager.getAllowedTaskBundle();
        ArrayList<Integer> ids = result.getIntegerArrayList(TaskRelationManager.BUNDLE_TASK_IDS);
        ArrayList<String> names = result.getStringArrayList(TaskRelationManager.BUNDLE_TASK_NAMES);
        assertEquals(1, ids.size());
        assertEquals(1, names.size());
        assertTrue(ids.contains((int) id2));
        assertEquals(originalTitle,names.get(0));
    }
    @Test
    public void testGetAllowedTaskBundle8_2(){
        dbR.delete(TaskDBContract.TaskDB.TASK_TABLE_NAME, null, null);
        Bundle result = relationManager.getAllowedTaskBundle();
        ArrayList<Integer> ids = result.getIntegerArrayList(TaskRelationManager.BUNDLE_TASK_IDS);
        ArrayList<String> names = result.getStringArrayList(TaskRelationManager.BUNDLE_TASK_NAMES);
        assertTrue(ids.isEmpty());
        assertTrue(names.isEmpty());
    }

}
