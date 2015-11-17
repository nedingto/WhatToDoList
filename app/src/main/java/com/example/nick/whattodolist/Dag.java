package com.example.nick.whattodolist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Queue;

/**
 * Created by Nicole on 10/29/2015.
 */
public class Dag {

    //hash map that will keep track of the nodes by the task id they correspond to
    HashMap<Integer, DagNode> nodes;

    public Dag(){
        nodes = new HashMap<>();
    }
    public void addEdge(int parentId, int subId){
        //add the edges, if the task ids do not exist in the dag yet, create the node
        DagNode parent = nodes.get(parentId);
        DagNode sub = nodes.get(subId);
        if(parent == null){
            DagNode newParent = new DagNode(parentId);
            nodes.put(parentId, newParent);
            parent = nodes.get(parentId);
        }
        if(sub == null){
            DagNode newSub = new DagNode(subId);
            nodes.put(subId, newSub);
            sub = nodes.get(subId);
        }
        //only add the edge if it doesn't already exist
        if(!parent.getSubTaskIds().contains(sub)) {
            parent.addSubTask(sub);
            sub.addParentTask(parent);
        }
    }

    public void removeEdge(int parentId, int subId){
        //will remove the corresponding node relations, provided they exist
        DagNode parent = nodes.get(parentId);
        DagNode sub = nodes.get(subId);
        if(parent != null){
            parent.removeSubTask(subId);
        }
        if(sub != null){
            sub.removeParentTask(parentId);
        }
    }

    public ArrayList<Integer> getForbiddenIds(int taskId){
        //will preform a depth first search of the dag, and return the ids of all parent nodes
        ArrayList<Integer> forbiddenIds = new ArrayList<>();
        DagNode node = nodes.get(taskId);
        forbiddenIds.add(taskId);
        //list will be empty if the task id does not correspond to a node
        if(node != null) {
            //the given task id will automatically be in the list
            //get all of the parent nodes this node
            node.setDiscovered(true);

            for (DagNode parent : getAllParentNodes(node)) {
                //add each node to the forbidden list
                forbiddenIds.add(parent.getTaskId());
            }
            //be sure to un-mark all nodes
            for(DagNode n: nodes.values()){
                n.setDiscovered(false);
            }
        }
        return forbiddenIds;
    }

    public ArrayList<DagNode> getAllParentNodes(DagNode node){
        //mark this node as discovered and find all of its parents, calling this function on each
        node.setDiscovered(true);
        ArrayList<DagNode> allParentNodes = new ArrayList<>();
        allParentNodes.add(node);
        for(DagNode parentNode : node.getParentTaskIds()){
            if(!parentNode.isDiscovered()){
                //if it has not been discoverd yet, add it to the list to return
                allParentNodes.addAll(getAllParentNodes(parentNode));
            }
        }
        return allParentNodes;
    }
    //these are more for testing
    public HashMap<Integer, DagNode> getDagNodes(){
        return nodes;
    }

    public void addNode(Integer id, DagNode node){
        nodes.put(id,node);
    }
}
