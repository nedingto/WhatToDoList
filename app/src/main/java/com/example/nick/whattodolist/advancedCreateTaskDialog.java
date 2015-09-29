package com.example.nick.whattodolist;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SimpleCursorAdapter;

import java.util.ArrayList;

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
                        //gets arguments bundled from the simpleCreateTaskDialog
                        Bundle args = getArguments();
                        String taskName = args.getString("task_name");
                        String dueDate = args.getString("due_date");

                        AlertDialog d = (AlertDialog) dialog;
                        //This finds the views for the entry fields and sends them, along with the bundled
                        //info as parameters to the main activity
                        //category must be created
                        int priority = (int) (((RatingBar) d.findViewById(R.id.ratingBar)).getRating());
                        int estimatedMins = new Integer(((EditText) d.findViewById(R.id.editText2)).getText().toString());

                        ExpandableListView listView = ((ExpandableListView) d.findViewById(R.id.expandableListView2));

                        ArrayList<String> categories = new ArrayList<String>();
                        for (int i = 0; i < listView.getAdapter().getCount(); i++) {
                            CheckBox cb = (CheckBox) listView.getAdapter().getItem(i);
                            if (cb.isChecked()) {
                                categories.add(cb.getText().toString());
                            }
                        }

                        mListener.onAdvancedDialogPositiveClick(taskName, dueDate, priority, estimatedMins, categories);


                    }
                })
                .setNeutralButton(R.string.edit, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog so do nothing
                    }
                });

        // Create the AlertDialog object and return it
        AlertDialog alert = builder.create();
        populateCategoryList();
        return alert;
    }

    private void populateCategoryList(){
        String[] projection = {
                TaskDBContract.TaskDB.COLUMN_NAME_CATEGORY_NAME
        };
        String sortOrder =
                TaskDBContract.TaskDB.COLUMN_NAME_CATEGORY_NAME + " DESC";

        Cursor cursor = ((MainToDo)getActivity()).dbR.query(
                TaskDBContract.TaskDB.CATEGORY_TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        int[] toViewIds = {R.id.checkBox10};
        SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(getActivity(),R.layout.advanced_create_task_dialog, cursor, projection, toViewIds, 0);
        ListView listView = (ListView) getDialog().findViewById(R.id.expandableListView2);
        listView.setAdapter(simpleCursorAdapter);

    }
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
}
