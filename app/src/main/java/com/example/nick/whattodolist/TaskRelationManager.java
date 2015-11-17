package com.example.nick.whattodolist;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Nicole on 10/30/2015.
 */
public class TaskRelationManager {
    TaskDBContract dbContract;
    TaskDbHelper mDbHelper;
    SQLiteDatabase dbR;
    SQLiteDatabase dbW;
    Dag relationDag;
    ArrayList<Integer> allowedTaskIds;
    ArrayList<Integer> forbiddenTaskIds;
    int taskId;
    HashMap<Integer, Bundle> tmpTasks;
    int tmpTaskId = 0;

    public static final String BUNDLE_TASK_IDS = "task_ids";
    public static final String BUNDLE_TASK_NAMES = "task_names";

    public TaskRelationManager(Context context, int taskId){
        this.taskId = taskId;
        dbContract = new TaskDBContract();
        mDbHelper = new TaskDbHelper(context);

        //set up reader and writer
        dbR = mDbHelper.getReadableDatabase();
        dbW = mDbHelper.getWritableDatabase();

        tmpTasks = new HashMap<>();
        relationDag = getTaskRelationDag();
        forbiddenTaskIds = relationDag.getForbiddenIds(this.taskId);
        allowedTaskIds = getAllowedTaskIds();
    }

    public void createTmpRelation(int parentId, int subId){
        relationDag.addEdge(parentId, subId);
    }

    public void removeTmpRelations(int parentId, int subId){
        relationDag.removeEdge(parentId, subId);
    }
    //made public for testing
    public Dag getTaskRelationDag(){
        Dag taskDag = new Dag();
        String[] projectionRelations = {
                TaskDBContract.TaskDB._ID,
                TaskDBContract.TaskDB.COLUMN_NAME_SUB_TASK,
                TaskDBContract.TaskDB.COLUMN_NAME_PARENT_TASK
        };

        Cursor c = dbR.query(
                TaskDBContract.TaskDB.TASK_RELATION_TABLE_NAME,  // The table to query
                projectionRelations,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );
        c.moveToFirst();
        while(!c.isAfterLast()){
            //add to the graph
            int parent = c.getInt(c.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_PARENT_TASK));
            int sub = c.getInt(c.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_SUB_TASK));
            taskDag.addEdge(parent, sub);
            c.moveToNext();
        }

        return taskDag;
    }
    //made pubic for testing
    public ArrayList<Integer> getAllowedTaskIds(){
        ArrayList<Integer> allowedTaskIds = new ArrayList<>();
        String[] projection = {
                TaskDBContract.TaskDB._ID,
                TaskDBContract.TaskDB.COLUMN_NAME_CHECKED
        };

        String clause = TaskDBContract.TaskDB.COLUMN_NAME_CHECKED + "= 0";

        if(!forbiddenTaskIds.isEmpty()){
            clause += " AND (";
            for(int i = 0; i < forbiddenTaskIds.size();i++){
                clause += TaskDBContract.TaskDB._ID + " != " + forbiddenTaskIds.get(i);
                if(i<forbiddenTaskIds.size()-1){
                    clause += " OR ";
                }
            }
            clause += ")";
        }


        Cursor c = dbR.query(
                TaskDBContract.TaskDB.TASK_TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                clause,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );
        c.moveToFirst();
        while(!c.isAfterLast()){
            int taskId = c.getInt(c.getColumnIndex(TaskDBContract.TaskDB._ID));
            allowedTaskIds.add(taskId);
            c.moveToNext();
        }
        return allowedTaskIds;
    }

    public void updateAllowedTaskIds(){
        ArrayList<Integer> newForbiddenTaskIds = relationDag.getForbiddenIds(taskId);
        allowedTaskIds.removeAll(newForbiddenTaskIds);
        //the result here with leave forbidden task ids will all the ids removed from the forbbiden
        //list
        forbiddenTaskIds.removeAll(newForbiddenTaskIds);
        allowedTaskIds.addAll(forbiddenTaskIds);
        forbiddenTaskIds = newForbiddenTaskIds;
    }

    public Bundle getAllowedTaskBundle() {
        updateAllowedTaskIds();
        Bundle allowedTaskBundle = new Bundle();
        ArrayList<String> taskNames = new ArrayList<>();
        ArrayList<Integer> taskIds = new ArrayList<>();
        String[] projection = {
                TaskDBContract.TaskDB._ID,
                TaskDBContract.TaskDB.COLUMN_NAME_TASK_NAME,
                TaskDBContract.TaskDB.COLUMN_NAME_CHECKED
        };

        String clause = "";
        String[] values = new String[allowedTaskIds.size()];
        if(!allowedTaskIds.isEmpty()){
            clause += "(";
            for(int i = 0; i < allowedTaskIds.size();i++){
                clause += TaskDBContract.TaskDB._ID + " = " + allowedTaskIds.get(i);
                if(i<allowedTaskIds.size()-1){
                    clause += " OR ";
                }
            }
            clause += ")";

            Cursor c = dbR.query(
                    TaskDBContract.TaskDB.TASK_TABLE_NAME,  // The table to query
                    projection,                               // The columns to return
                    clause,                                // The columns for the WHERE clause
                    null,                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    null                                 // The sort order
            );
            c.moveToFirst();
            while (!c.isAfterLast()) {
                taskIds.add(c.getInt(c.getColumnIndex(TaskDBContract.TaskDB._ID)));
                taskNames.add(c.getString(c.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_TASK_NAME)));
                c.moveToNext();
            }
        }
        allowedTaskBundle.putStringArrayList(BUNDLE_TASK_NAMES, taskNames);
        allowedTaskBundle.putIntegerArrayList(BUNDLE_TASK_IDS, taskIds);
        return allowedTaskBundle;
    }

    public int addTmpTask(Bundle tmpTaskBundle){
        //this increments a temperarry task id, which will be negative, to ensure uniqiness,
        //then adds it to the dag, with the temp task id, and adds it to a hash map of task
        //bundles with the id as a key, also returns the tmp task id
        tmpTaskId--;
        relationDag.addEdge(taskId, tmpTaskId);
        tmpTasks.put(tmpTaskId, tmpTaskBundle);
        return tmpTaskId;
    }

    public void removeTmpTask(int tmpTaskId){
        // uses the give task id to remove the tmp task from the dag, and to remove it from the
        //map of tasks
        relationDag.removeEdge(taskId, tmpTaskId);
        tmpTasks.remove(tmpTaskId);
    }

    public Bundle getTmpTask(int tmpTaskId){
        return tmpTasks.get(tmpTaskId);
    }
}
