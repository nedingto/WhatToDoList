package com.example.nick.whattodolist;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by Nick on 10/19/2015.
 */
public class confirmBasisChangeDialog extends DialogFragment {
    //This is a check to warn the user that thier basis chang will delete other tasks
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.basis_change_confirmation_message)
                .setPositiveButton(R.string.confirmation_positive, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //the user wants to continue, give them back their bundle
                        mListener.onConfirmationBasisChangePositiveClick(getArguments());
                    }
                })
                .setNegativeButton(R.string.confirmation_negative, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // The user does not want to make the change
                        mListener.onConfirmationBasisChangeNegativeClick();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    public interface confirmationBasisChangeDialogListener {
        public void onConfirmationBasisChangePositiveClick(Bundle repeatingBundle);
        public void onConfirmationBasisChangeNegativeClick();
    }

    // Use this instance of the interface to deliver action events
    confirmationBasisChangeDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (confirmationBasisChangeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}
