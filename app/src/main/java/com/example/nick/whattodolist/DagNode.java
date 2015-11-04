package com.example.nick.whattodolist;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by Nicole on 10/29/2015.
 */
public class DagNode {
    int taskId;
    //note that all of the ids here will be unique

    ArrayList<DagNode> parentTaskIds;
    ArrayList<DagNode> subTaskIds;
    boolean discovered = false;

    public DagNode(int taskId){
        subTaskIds = new ArrayList<>();
        parentTaskIds = new ArrayList<>();
        this.taskId = taskId;
    }
    public int addParentTask(DagNode parentNode){
        //if the entry already exists, don't add it and return an error value
        if(parentTaskIds.contains(parentNode)){
            return -1;
        } else {
            parentTaskIds.add(parentNode);
        }
        return 0;
    }

    public int addSubTask(DagNode subNode){
        //if the entry already exists, don't add it and return an error value
        if(subTaskIds.contains(subNode)){
            return -1;
        } else {
            subTaskIds.add(subNode);
        }
        return 0;
    }

    public boolean removeParentTask(int parentId){
        return parentTaskIds.remove((Integer)parentId);
    }

    public boolean removeSubTask(int subId){
        return subTaskIds.remove((Integer)subId);
    }

    public ArrayList<DagNode> getParentTaskIds() {
        return parentTaskIds;
    }

    public ArrayList<DagNode> getSubTaskIds() {
        return subTaskIds;
    }

    public void setDiscovered(boolean discovered){
        this.discovered = discovered;
    }

    public boolean isDiscovered(){
        return discovered;
    }

    public int getTaskId() {
        return taskId;
    }
}
