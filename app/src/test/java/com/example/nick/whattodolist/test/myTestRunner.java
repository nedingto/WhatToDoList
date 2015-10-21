package com.example.nick.whattodolist.test;

/**
 * Created by Nick on 10/14/2015.
 */
import com.example.nick.whattodolist.MainToDo;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class myTestRunner {
    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(MainToDoTest.class);
        for (Failure failure : result.getFailures()) {
            System.out.println(failure.toString());
        }
    }
}
