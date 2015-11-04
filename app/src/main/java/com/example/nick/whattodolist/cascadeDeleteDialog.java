package com.example.nick.whattodolist;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by Nick on 10/18/2015.
 */
public class cascadeDeleteDialog extends DialogFragment {
    //This class is used to check if the deletion should cascade through the recurring series
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.cascade_delete_message)
                .setPositiveButton(R.string.cascade_positive, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onCascadeDeletePositiveClick();
                    }
                })
                .setNeutralButton(R.string.cascade_neutral, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onCascadeDeleteNeutralClick();
                    }
                })
                .setNegativeButton(R.string.cascade_negative, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Only wants to apply to this one
                        mListener.onCascadeDeleteNegativeClick();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface cascadeDeleteDialogListener{
        public void onCascadeDeletePositiveClick();
        public void onCascadeDeleteNegativeClick();
        public void onCascadeDeleteNeutralClick();
    }

    // Use this instance of the interface to deliver action events
    cascadeDeleteDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (cascadeDeleteDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}

