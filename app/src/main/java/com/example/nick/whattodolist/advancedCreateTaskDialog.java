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
    String taskName = "";
    String dueDate = "";
    int priority = 1;
    int estimation = 0;
    Bundle basisBundle = new Bundle();
    ArrayList<String> addedCategories = new ArrayList<>();

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        //view to be used for this dialog
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.advanced_create_task_dialog, null);

        //build the dialog
        builder.setView(dialogView)
                .setMessage(R.string.advanced_create_task_dialog)
                .setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //pull from the fields and pass the click event
                        gatherFields();
                        mListener.onAdvancedDialogPositiveClick(taskName, dueDate, priority,
                                estimation, addedCategories, basisBundle);

                    }
                })
                .setNeutralButton(R.string.edit, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //pull from the fields and pass the click event
                        gatherFields();
                        mListener.onAdvancedDialogNeutralClick(taskName, dueDate, priority,
                                estimation, addedCategories, basisBundle);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog so do nothing
                    }
                });

        // Create the AlertDialog object and return it
        return  builder.create();
    }

    //interface for implementation to handel events
    public interface AdvancedCreateTaskListener {
        public long onAdvancedDialogPositiveClick(String taskName, String dueDate, int priority, int estimatedMins, ArrayList<String> categories, Bundle repeatingBundle);
        public void onAdvancedDialogNeutralClick(String taskName, String dueDate, int priority, int estimatedMins, ArrayList<String> categories, Bundle repeatingBundle);
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

    private void gatherFields(){
        //gets arguments bundled from the previous dialog
        Bundle args = getArguments();

        //regardless of where it came from it should have have a task name
        taskName = args.getString(simpleCreateTaskDialog.BUNDLE_TASK_NAME);

        if (args.containsKey(simpleCreateTaskDialog.BUNDLE_DUE_DATE)) {
            //if the bundle has due_date key, then it came from
            //a simple create task dialog
            dueDate = args.getString(simpleCreateTaskDialog.BUNDLE_DUE_DATE);
        } else {
            //otherwise leave the due date empty and set the repeating bundle
            basisBundle = args;
        }

        //This finds the views for the entry fields and sends them, along with
        //the bundled info

        priority = (int) (((RatingBar) getDialog().findViewById(R.id.ratingBar)).getRating());

        EditText editText = ((EditText) getDialog().findViewById(R.id.editText2));
        estimation = Integer.parseInt(editText.getText().toString());

        //added categories come from the spinner fragment
        spinnerPopulater fragment = (spinnerPopulater) getFragmentManager().findFragmentById(
                R.id.spinner_fragment);
        addedCategories = fragment.getAdded();

    }

    //destroys the fragment when this closes
    public void onDestroyView() {
        super.onDestroyView();
        FragmentManager fm = getActivity().getFragmentManager();
        Fragment fragment = (fm.findFragmentById(R.id.spinner_fragment));
        FragmentTransaction ft = fm.beginTransaction();
        ft.remove(fragment);
        ft.commit();
    }

}
