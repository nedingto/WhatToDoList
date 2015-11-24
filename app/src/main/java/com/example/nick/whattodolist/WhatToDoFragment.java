package com.example.nick.whattodolist;

import android.app.Activity;
import android.support.v4.app.DialogFragment;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WhatToDoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WhatToDoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WhatToDoFragment extends Fragment implements simpleCreateTaskDialog.SimpleCreateTaskListener, advancedCreateTaskDialog.AdvancedCreateTaskListener,
        createRepeatingDialog.RepeatingCreateTaskListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    //editors for the database
    TaskEditor taskEditor;
    com.example.nick.whattodolist.repeatingBasisEditor repeatingBasisEditor;
    boolean assignedHidden = false;
    boolean fullListHidden = false;
    View rootView;
    Calendar today;

    // TODO: Rename and change types of parameters
    private int mParam1;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment WhatToDoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WhatToDoFragment newInstance(int param1) {
        WhatToDoFragment fragment = new WhatToDoFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public WhatToDoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        taskEditor = new TaskEditor(getActivity());
        repeatingBasisEditor = new repeatingBasisEditor(getActivity());

        super.onCreate(savedInstanceState);

        // this is for the pagination, might need this later

        //update the tasks when this starts
        if (getArguments() != null) {
            mParam1 = getArguments().getInt(ARG_PARAM1);
        } else {
            throw new IllegalArgumentException("Expecting date arg when creating new fragment");
        }
        today = Calendar.getInstance();
        today.add(Calendar.DAY_OF_MONTH, mParam1);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_main_to_do, container, false);
        tasksUpdated();
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);

    }

    //method to populate main view
    public void tasksUpdated(){

        ArrayList<Bundle> tasks = taskEditor.getTasksDateAcceding();

        //removes the task list and then programmatically fills it will the newest tasks
        LinearLayout tl = (LinearLayout)rootView.findViewById(R.id.task_layout);
        LinearLayout assignments = (LinearLayout)rootView.findViewById(R.id.assigned_layout);
        tl.removeAllViews();
        assignments.removeAllViews();
        Calendar tmp = dateConverter.sqlToCalendar(dateConverter.firstDateSql);

        for(Bundle task:tasks){
            Calendar dueDate = dateConverter.sqlToCalendar(task.getString(taskEditor.BUNDLE_DUE_DATE));
            //act only if the task is due in the future or is past due
            if(!dueDate.before(today) || task.getInt(taskEditor.BUNDLE_CHECKED) == 0) {
                if (dueDate.after(tmp)) {
                    //if there is a new due date create a divider and reset tmp
                    LinearLayout tr = new LinearLayout(getActivity().getApplicationContext());
                    TextView dueText = new TextView(getActivity().getApplicationContext());
                    dueText.setText(dateConverter.calendarToString(dueDate));
                    tr.addView(dueText);
                    //extra space
                    TextView line = new TextView(getActivity().getApplicationContext());
                    tr.addView(line);
                    tl.addView(tr);
                    tmp.set(dueDate.get(Calendar.YEAR), dueDate.get(Calendar.MONTH), dueDate.get(Calendar.DAY_OF_MONTH), 24, 59, 59);
                }

                int taskId = task.getInt(TaskEditor.BUNDLE_TASK_ID);
                LinearLayout tr = new LinearLayout(getActivity().getApplicationContext());
                CheckBox cb = new CheckBox(getActivity().getApplicationContext());
                cb.setTag(taskId);
                cb.setOnClickListener(new View.OnClickListener() {
                                          public void onClick(View v) {
                                              CheckBox thisView = (CheckBox) v;
                                              taskEditor.updateChecked((int) v.getTag(),
                                                      thisView.isChecked() ? 1 : 0);
                                          }
                                      }
                );
                TextView tv1 = new TextView(getActivity().getApplicationContext());
                tr.addView(cb);
                tr.addView(tv1);
                tv1.setText(task.getString(TaskEditor.BUNDLE_TASK_NAME));
                if (task.getInt(TaskEditor.BUNDLE_INCOMPLETE_SUB) != 0)
                    tv1.setTextColor(Color.WHITE);
                //text field for due date, will likely be displayed in a different way in final

                if (task.getInt(TaskEditor.BUNDLE_CHECKED) == 1) {
                    cb.setChecked(true);
                } else cb.setChecked(false);

                //switch to color the row based on priority
                //TODO cahnge colors
                int priority = task.getInt(TaskEditor.BUNDLE_PRIORITY);
                switch (priority) {
                    case 1:
                        tr.setBackgroundColor(Color.BLACK);
                        break;
                    case 2:
                        tr.setBackgroundColor(Color.DKGRAY);
                        break;
                    case 3:
                        tr.setBackgroundColor(Color.GREEN);
                        break;
                    case 4:
                        tr.setBackgroundColor(Color.YELLOW);
                        break;
                    case 5:
                        tr.setBackgroundColor(Color.RED);
                        break;
                    default:
                        break;
                }

                String assignedDate = task.getString(taskEditor.BUNDLE_ASSIGNED_DATE);
                if(assignedDate == null) {
                    Button assignButton = new Button(getActivity().getApplicationContext());
                    assignButton.setText("Assign");
                    assignButton.setTag(task.getInt(taskEditor.BUNDLE_TASK_ID));
                    assignButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            taskEditor.updateAssignedDate(Integer.parseInt(getTag()),dateConverter.calendarToSql(today));
                            tasksUpdated();
                        }
                    });
                    tr.addView(assignButton);
                } else if(!assignedDate.equals(dateConverter.calendarToSql(today))){
                    TextView todoDate = new TextView(getActivity().getApplicationContext());
                    todoDate.setText(task.getString(taskEditor.BUNDLE_ASSIGNED_DATE));
                    tr.addView(todoDate);
                }else {
                    Button unassignButton = new Button(getActivity().getApplicationContext());
                    unassignButton.setText("Unassign");
                    unassignButton.setTag(task.getInt(taskEditor.BUNDLE_TASK_ID));
                    unassignButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            taskEditor.updateAssignedDate(Integer.parseInt(getTag()),null);
                            tasksUpdated();
                        }
                    });
                    assignments.addView(tr);
                    tr.addView(unassignButton);
                }
                tl.addView(tr);
                tr.setTag(taskId);

                tr.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Intent myIntent = new Intent((MainToDo) getActivity(), EditTaskActivity.class);
                        myIntent.putExtra("taskId", String.valueOf(v.getTag()));
                        ((MainToDo) getActivity()).startActivity(myIntent);
                        return false;
                    }
                });
            }
        }
    }


    /*reactions to creating task button*/
    public void createTask(View v, int year, int month, int day){
        DialogFragment newFragment = new simpleCreateTaskDialog();
        Bundle args = new Bundle();
        args.putInt(simpleCreateTaskDialog.BUNDLE_CURRENT_YEAR, year);
        args.putInt(simpleCreateTaskDialog.BUNDLE_CURRENT_MONTH, month);
        args.putInt(simpleCreateTaskDialog.BUNDLE_CURRENT_DAY, day);
        newFragment.setArguments(args);
        newFragment.show(getFragmentManager(), "create task");
    }

    //create task and save it when one is created
    @Override
    public void onSimpleDialogPositiveClick(String taskName, String dueDate) {


        taskEditor.createTask(taskName, dueDate, 1, 0, -1);
        tasksUpdated();
    }


    @Override
    public long onAdvancedDialogPositiveClick(String taskName, String dueDate, int priority, int estimatedMins, ArrayList<String> categories, Bundle repeatingBundle){
        //task id to return, returns the first task if it is recurring
        long taskId = -1;

        ArrayList<String> dates = new ArrayList<>();

        //cheeck if there should be a repeating basis, if no the basis id will be -1
        float basisId = -1;
        if(repeatingBundle.containsKey(createRepeatingDialog.BUNDLE_ORDINAL_NUM)){
            basisId = repeatingBasisEditor.createBasis(
                    repeatingBundle.getInt(createRepeatingDialog.BUNDLE_PERIODICAL_NUM),
                    repeatingBundle.getInt(createRepeatingDialog.BUNDLE_PERIODICAL_PERIOD),
                    repeatingBundle.getInt(createRepeatingDialog.BUNDLE_ORDINAL_NUM),
                    repeatingBundle.getInt(createRepeatingDialog.BUNDLE_ORDINAL_PERIOD),
                    repeatingBundle.getIntArray(createRepeatingDialog.BUNDLE_DAY_OF_WEEK),
                    repeatingBundle.getString(createRepeatingDialog.BUNDLE_START_DATE),
                    repeatingBundle.getString(createRepeatingDialog.BUNDLE_END_DATE));
            dates = repeatingBasisEditor.getBasisDates(basisId);
        } else dates.add(dueDate);
        //for each date add a task
        for(int i = 0; i < dates.size();i++){
            int id = taskEditor.createTask(taskName, dates.get(i),
                    priority, estimatedMins, (int) basisId);

            //save the first id to return
            if(i == 0) taskId = id;

            taskEditor.updateCategories(categories, new ArrayList<String>(), id);
        }
        tasksUpdated();
        return taskId;
    }

    @Override
    public void onAdvancedDialogNeutralClick(String taskName, String dueDate, int priority,
                                             int estimatedMins, ArrayList<String> categories, Bundle repeatingBundle) {
        //same as positive but then open the edit with the first task id that was created
        long taskId;
        taskId = onAdvancedDialogPositiveClick(taskName, dueDate, priority, estimatedMins,
                categories, repeatingBundle);
        Intent myIntent = new Intent((MainToDo)getActivity(),EditTaskActivity.class);
        myIntent.putExtra("taskId", String.valueOf(taskId));
        ((MainToDo)getActivity()).startActivity(myIntent);
    }

    @Override
    public void onRepeatingDialogPositiveClick(Bundle repeatingBundle) {
        ArrayList<String> dates = new ArrayList<>();

        //create the basis and get its id
        float basisId = -1;
        basisId = repeatingBasisEditor.createBasis(
                repeatingBundle.getInt(createRepeatingDialog.BUNDLE_PERIODICAL_NUM),
                repeatingBundle.getInt(createRepeatingDialog.BUNDLE_PERIODICAL_PERIOD),
                repeatingBundle.getInt(createRepeatingDialog.BUNDLE_ORDINAL_NUM),
                repeatingBundle.getInt(createRepeatingDialog.BUNDLE_ORDINAL_PERIOD),
                repeatingBundle.getIntArray(createRepeatingDialog.BUNDLE_DAY_OF_WEEK),
                repeatingBundle.getString(createRepeatingDialog.BUNDLE_START_DATE),
                repeatingBundle.getString(createRepeatingDialog.BUNDLE_END_DATE));
        dates = repeatingBasisEditor.getBasisDates(basisId);

        //since this came from a simple dialog it will have a task name, save that
        String taskName = repeatingBundle.getString(simpleCreateTaskDialog.BUNDLE_TASK_NAME);

        //for each date create a task
        for(String date: dates) {
            taskEditor.createTask(taskName, date, 1, 0, (int)basisId);
        }
        tasksUpdated();
    }

    @Override
    public void onRepeatingDialogNegativeClick() {
        //user canceled repeating dialog, do nothing
    }

    public void onCreateRepeatingCLicked(View v, int year, int month, int day) {
        DialogFragment newFragment = new createRepeatingDialog();
        Bundle args = new Bundle();
        args.putInt(simpleCreateTaskDialog.BUNDLE_CURRENT_YEAR, year);
        args.putInt(simpleCreateTaskDialog.BUNDLE_CURRENT_MONTH, month);
        args.putInt(simpleCreateTaskDialog.BUNDLE_CURRENT_DAY, day);
        newFragment.setArguments(args);
        newFragment.show(getFragmentManager(), "create repeating");
    }

    public void hideAssignedClicked(){
        if(assignedHidden){
            rootView.findViewById(R.id.scrollView4).setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.button13).setVisibility(View.VISIBLE);
            ((Button)rootView.findViewById(R.id.button)).setText("^");
            assignedHidden = false;
        } else {
            rootView.findViewById(R.id.scrollView4).setVisibility(View.GONE);
            rootView.findViewById(R.id.button13).setVisibility(View.GONE);
            ((Button)rootView.findViewById(R.id.button)).setText("v");
            assignedHidden = true;
        }
    }

    public void hideFullListClicked(){
        if(fullListHidden){
            rootView.findViewById(R.id.scrollView).setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.button).setVisibility(View.VISIBLE);
            ((Button)rootView.findViewById(R.id.button13)).setText("v");
            fullListHidden = false;
        } else {
            rootView.findViewById(R.id.scrollView).setVisibility(View.GONE);
            rootView.findViewById(R.id.button).setVisibility(View.GONE);
            ((Button)rootView.findViewById(R.id.button13)).setText("^");
            fullListHidden = true;
        }
    }

    public void changeDate(int value){
        today.add(Calendar.DAY_OF_MONTH,value);
    }

    public boolean getFullListHidden(){
        return fullListHidden;
    }
    public boolean getAssignedHidden(){
        return assignedHidden;
    }
}
