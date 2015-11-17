package com.example.nick.whattodolist.test;

import android.test.ActivityInstrumentationTestCase2;

import com.example.nick.whattodolist.Dag;
import com.example.nick.whattodolist.DagNode;
import com.example.nick.whattodolist.MainToDo;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Nicole on 11/14/2015.
 */
public class DagTest extends ActivityInstrumentationTestCase2<MainToDo> {
    Dag dag;
    public DagTest() {
        super(MainToDo.class);
    }


    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        getInstrumentation().waitForIdleSync();
        dag = new Dag();
    }

    @Test
    public void testAddEdge1_1(){
        int id1 = 1;
        int id2 = 2;
        DagNode dn1 = new DagNode(id1);
        DagNode dn2 = new DagNode(id2);
        dn1.addSubTask(dn2);
        dn2.addParentTask(dn1);
        dag.addNode(id1, dn1);
        dag.addNode(id1, dn1);
        dag.addEdge(id1, id2);
        HashMap<Integer,DagNode> result = dag.getDagNodes();
        assertEquals(2, result.size());
        assertEquals(dn1, result.get(id1));
        assertEquals(dn2, result.get(id2));
        assertEquals(0, result.get(id1).getParentTaskIds().size());
        assertEquals(1, result.get(id1).getSubTaskIds().size());
        assertEquals(0, result.get(id2).getSubTaskIds().size());
        assertEquals(1, result.get(id2).getParentTaskIds().size());
    }
    @Test
    public void testAddEdge1_2(){
        int id1 = 1;
        int id2 = 2;
        dag.addEdge(id1, id2);
        HashMap<Integer,DagNode> result = dag.getDagNodes();
        assertEquals(2, result.size());
        assertEquals(0, result.get(id1).getParentTaskIds().size());
        assertEquals(1, result.get(id1).getSubTaskIds().size());
        assertEquals(0, result.get(id2).getSubTaskIds().size());
        assertEquals(1, result.get(id2).getParentTaskIds().size());
        assertTrue(result.get(id2).getParentTaskIds().contains(id1));
        assertTrue(result.get(id1).getSubTaskIds().contains(id2));
    }

    @Test
    public void testRemoveEdge2_1(){
        int id1 = 1;
        int id2 = 2;
        DagNode dn1 = new DagNode(id1);
        DagNode dn2 = new DagNode(id2);
        dn1.addSubTask(dn2);
        dn2.addParentTask(dn1);
        dag.addNode(id1, dn1);
        dag.addNode(id1, dn1);
        dag.removeEdge(id1, id2);
        HashMap<Integer,DagNode> result = dag.getDagNodes();
        assertTrue(result.get(id1).getSubTaskIds().isEmpty());
        assertTrue(result.get(id1).getParentTaskIds().isEmpty());
        assertTrue(result.get(id2).getSubTaskIds().isEmpty());
        assertTrue(result.get(id2).getParentTaskIds().isEmpty());
    }

    @Test
    public void testRemoveEdge2_2(){
        int id1 = 1;
        int id2 = 2;
        DagNode dn1 = new DagNode(id1);
        DagNode dn2 = new DagNode(id2);
        dn1.addSubTask(dn2);
        dn2.addParentTask(dn1);
        dag.addNode(id1, dn1);
        dag.addNode(id1, dn1);
        dag.removeEdge(-1, -1);
        HashMap<Integer,DagNode> result = dag.getDagNodes();
        assertEquals(2, result.size());
        assertEquals(dn1, result.get(id1));
        assertEquals(dn2, result.get(id2));
        assertEquals(0, result.get(id1).getParentTaskIds().size());
        assertEquals(1, result.get(id1).getSubTaskIds().size());
        assertEquals(0, result.get(id2).getSubTaskIds().size());
        assertEquals(1, result.get(id2).getParentTaskIds().size());
    }

    @Test
    public void testGetForbiddenIds3_1(){
        int id1 = 1;
        int id2 = 2;
        DagNode dn1 = new DagNode(id1);
        DagNode dn2 = new DagNode(id2);
        dn1.addSubTask(dn2);
        dn2.addParentTask(dn1);
        dag.addNode(id1, dn1);
        dag.addNode(id1, dn1);
        ArrayList<Integer> result = dag.getForbiddenIds(-1);
        assertTrue(result.isEmpty());
    }
    @Test
    public void testGetForbiddenIds3_2(){
        int id1 = 1;
        int id2 = 2;
        DagNode dn1 = new DagNode(id1);
        DagNode dn2 = new DagNode(id2);
        dn1.addSubTask(dn2);
        dn2.addParentTask(dn1);
        dag.addNode(id1, dn1);
        dag.addNode(id1, dn1);
        ArrayList<Integer> result = dag.getForbiddenIds(id2);
        assertEquals(2,result.size());
        assertTrue(result.contains(id1));
        assertTrue(result.contains(id2));
    }

    @Test
    public void testGetParentNodes4_1(){
        int id1 = 1;
        int id2 = 2;
        DagNode dn1 = new DagNode(id1);
        DagNode dn2 = new DagNode(id2);
        dn1.addSubTask(dn2);
        dn2.addParentTask(dn1);
        dag.addNode(id1, dn1);
        dag.addNode(id1, dn1);
        ArrayList<DagNode> result = dag.getAllParentNodes(dn2);
        assertEquals(1,result.size());
        assertEquals(dn1,  result.get(0));
    }
}
