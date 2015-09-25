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
public class simpleCreateTaskDialog extends DialogFragment
        implements advancedCreateTaskDialog.AdvancedCreateTaskListener{
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();


        builder.setView(inflater.inflate(R.layout.simple_create_task_dialog, null))
                //setting the message appears to be causing an issue
        .setMessage(R.string.simple_create_task_dilog)
                .setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //create the task
                        mListener.onDialogPositiveClick(simpleCreateTaskDialog.this);
                    }
                })
                .setNeutralButton(R.string.next, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id){
                        //move on to the next dialog
                        DialogFragment newFragment = new advancedCreateTaskDialog();
                        newFragment.show(getFragmentManager(), "create task advanced");
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog so do nothing
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
    public interface SimpleCreateTaskListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onAdvancedDialogPositiveClick(DialogFragment simpleDialog, DialogFragment advancedDialog);
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
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        mListener.onAdvancedDialogPositiveClick(simpleCreateTaskDialog.this, dialog);
    }
}
