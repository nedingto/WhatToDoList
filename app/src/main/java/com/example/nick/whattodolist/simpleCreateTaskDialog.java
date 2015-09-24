package com.example.nick.whattodolist;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

/**
 * Created by Nick on 9/20/2015.
 */
public class simpleCreateTaskDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        //create number pickers and values for date selection
        /*String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        NumberPicker numPicker1 = new NumberPicker(getActivity());
        numPicker1.setDisplayedValues(months);
        numPicker1.setMinValue(0);
        numPicker1.setMaxValue(11);
        NumberPicker numbPicker2 = new NumberPicker(getActivity());
        NumberPicker numPicker3 = new NumberPicker(getActivity());
        numPicker3.setMinValue(2000);
        numPicker3.setMaxValue(2100);
        LinearLayout ll = new LinearLayout(getActivity());
        ll.addView(numPicker1);
        ll.addView(numbPicker2);
        ll.addView(numPicker3);*/

        builder.setView(inflater.inflate(R.layout.simple_create_task_dialog, null))
        .setMessage(R.string.simple_create_task_dilog)
                .setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //create the task
                        mListener.onDialogPositiveClick(simpleCreateTaskDialog.this);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
    public interface SimpleCreateTaskListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    SimpleCreateTaskListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (SimpleCreateTaskListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}
