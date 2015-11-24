package com.example.nick.whattodolist;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by Nick on 10/18/2015.
 */
public class cascadeChangesDialog extends DialogFragment {
    //This class is used to check if the user would like the changes they have made to a task
    //to cascade through the recurring series
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.cascade_changes, null);
        builder.setView(dialogView)
                .setMessage(R.string.cascade_changes_message)
                .setPositiveButton(R.string.cascade_positive, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onCascadeChangesPositiveClick();
                    }
                })
                .setNeutralButton(R.string.cascade_neutral, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onCascadeChangesNeutralClick();
                    }
                })
                .setNegativeButton(R.string.cascade_negative, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onCascadeChangesNegativeClick();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface cascadeChangesDialogListener{
        public void onCascadeChangesPositiveClick();
        public void onCascadeChangesNegativeClick();
        public void onCascadeChangesNeutralClick();
    }

    // Use this instance of the interface to deliver action events
    cascadeChangesDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (cascadeChangesDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}
