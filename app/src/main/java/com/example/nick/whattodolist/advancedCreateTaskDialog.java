package com.example.nick.whattodolist;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.RatingBar;

/**
 * Created by Nick on 9/24/2015.
 */
public class advancedCreateTaskDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();


        builder.setView(inflater.inflate(R.layout.advanced_create_task_dialog,null))
                //setting the message appears to be causing an issue
                .setMessage(R.string.advanced_create_task_dialog)
                .setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        //// TODO: 9/27/2015 make title key a string resource
                        Bundle args = getArguments();
                        String taskName = args.getString("task_name");
                        String dueDate = args.getString("due_date");

                        AlertDialog d = (AlertDialog)dialog;
                        //category must be created
                        int priority = ((RatingBar)d.findViewById(R.id.ratingBar)).getNumStars();
                        int estimatedMins = new Integer(((EditText)d.findViewById(R.id.editText2)).getText().toString());
                        mListener.onAdvancedDialogPositiveClick(taskName, dueDate, priority, estimatedMins);
                    }
                })
                .setNeutralButton(R.string.edit, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id){

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

    public interface AdvancedCreateTaskListener {
        public void onAdvancedDialogPositiveClick(String taskName, String dueDate, int priority, int estimatedMins);
    }


    // Use this instance of the interface to deliver action events
    AdvancedCreateTaskListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (AdvancedCreateTaskListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}
