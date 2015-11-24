package com.example.nick.whattodolist;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by Nicole on 11/2/2015.
 */
public class addExistingSubTaskDialog extends DialogFragment implements AdapterView.OnItemClickListener {
    ArrayList<String> taskNames;
    ArrayList<Integer> taskIds;
    int selectedTaskId = -1;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // This dialog will display a list of the given tasks and return the id of the selected
        //task on positive click
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        //get the arrays from the args bundle, these array must align to work correctly
        taskIds = getArguments().getIntegerArrayList(TaskRelationManager.BUNDLE_TASK_IDS);
        taskNames = getArguments().getStringArrayList(TaskRelationManager.BUNDLE_TASK_NAMES);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.add_sub_task_dialog, null);
        builder.setView(dialogView).setMessage(R.string.add_sub_task_message)
                .setPositiveButton(R.string.add_sub_task_positive, new DialogInterface.OnClickListener() {
                    //if there is not selection, nothing will happen
                    public void onClick(DialogInterface dialog, int id) {
                        if(selectedTaskId != -1) mListener.onAddSubTaskPositiveClick(selectedTaskId);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Nothing will happen
                    }
                });
        // Create the AlertDialog object and return it
        AlertDialog alert = builder.create();

        //set up the list view to use the array as its adapter, and define on click action
        ListView lv = (ListView)dialogView.findViewById(R.id.listView);
        lv.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.list_layout, taskNames));
        lv.setOnItemClickListener(this);
        return alert;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //this will set the selected taskId to correspond to the selected element
        selectedTaskId = taskIds.get(position);
    }

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface addSubTaskDialogListener{
        public  void onAddSubTaskPositiveClick(int taskId);
    }

    // Use this instance of the interface to deliver action events
    addSubTaskDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (addSubTaskDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}
