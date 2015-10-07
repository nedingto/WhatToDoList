package com.example.nick.whattodolist;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


import java.util.ArrayList;

/**
 * Created by Nick on 9/24/2015.
 */
public class advancedCreateTaskDialog extends DialogFragment{

    //pre-made category list for the categories displayed
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.advanced_create_task_dialog, null);
        final spinnerPopulater fragment = (spinnerPopulater) getFragmentManager().findFragmentById(R.id.spinner_fragment);



        builder.setView(dialogView)
                //setting the message appears to be causing an issue
                .setMessage(R.string.advanced_create_task_dialog)
                .setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        // TODO: 9/27/2015 make title key a string resource
                        //gets arguments bundled from the simpleCreateTaskDialog
                        Bundle args = getArguments();
                        String taskName = args.getString("task_name");
                        String dueDate = args.getString("due_date");

                        //Cast dialog as this dialog
                        AlertDialog d = (AlertDialog) dialog;


                        //This finds the views for the entry fields and sends them, along with the bundled
                        //info as parameters to the main activity
                        //category must be created
                        int priority = (int) (((RatingBar) d.findViewById(R.id.ratingBar)).getRating());
                        //TODO: fix error for crash on no int entered.
                        int estimatedMins = new Integer(((EditText) d.findViewById(R.id.editText2)).getText().toString());

                        //categories come from the category array
                        mListener.onAdvancedDialogPositiveClick(taskName, dueDate, priority, estimatedMins, fragment.getSelected());

                    }
                })
                .setNeutralButton(R.string.edit, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //this will eventually call the edit function


                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog so do nothing
                    }
                });

        // Create the AlertDialog object and return it
        AlertDialog alert = builder.create();


        return alert;
    }

    //interface for implementation by the main activity
    public interface AdvancedCreateTaskListener {
        public void onAdvancedDialogPositiveClick(String taskName, String dueDate, int priority, int estimatedMins, ArrayList<String> categories);
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
    public void onDestroyView() {
        super.onDestroyView();
        FragmentManager fm = getActivity().getFragmentManager();
        Fragment fragment = (fm.findFragmentById(R.id.spinner_fragment));
        FragmentTransaction ft = fm.beginTransaction();
        ft.remove(fragment);
        ft.commit();
    }

}
