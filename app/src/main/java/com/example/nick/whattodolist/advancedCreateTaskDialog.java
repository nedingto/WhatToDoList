package com.example.nick.whattodolist;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import junit.framework.Assert;


import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Nick on 9/24/2015.
 */
public class advancedCreateTaskDialog extends DialogFragment
        implements AdapterView.OnItemSelectedListener {

    //pre-made category list for the categories displayed
    ArrayList<String> categories = new ArrayList<String>();

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.advanced_create_task_dialog, null);



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
                        mListener.onAdvancedDialogPositiveClick(taskName, dueDate, priority, estimatedMins, categories);

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


        //TODO create delete buttons

        //set up the button to add categories
        Button btn = (Button)dialogView.findViewById(R.id.button6);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toAdd = ((EditText)dialogView.findViewById(R.id.editText5)).getText().toString();
                addCategory(toAdd,dialogView);


            }
        });

        //set up category spinner, the width is such that only the down arrow shows
        populateCategoryList(dialogView);
        Spinner spinner = (Spinner)dialogView.findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);

        return alert;
    }

    //interface for implementation by the main activity
    public interface AdvancedCreateTaskListener {
        public void onAdvancedDialogPositiveClick(String taskName, String dueDate, int priority, int estimatedMins, ArrayList<String> categories);
    }

    //function that fills the spinner with categories from the DB by giving it an adapter
    private void populateCategoryList(View dialogView){
        //set query variables
        String[] projection = {
                TaskDBContract.TaskDB._ID,
                TaskDBContract.TaskDB.COLUMN_NAME_CATEGORY_NAME
        };

        String[] fromColumn = {
                TaskDBContract.TaskDB.COLUMN_NAME_CATEGORY_NAME
        };

        //get reader from the main activity
        Cursor cursor = ((MainToDo)getActivity()).dbR.query(
                TaskDBContract.TaskDB.CATEGORY_TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );

        //give the adater views to populate into and where it will access values and set it up with the spinner
        int[] toViewIds = {R.id.checkBox10};
        SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(getActivity(), R.layout.category_layout,  cursor, fromColumn, toViewIds, 0);
        Spinner spinner = (Spinner) dialogView.findViewById(R.id.spinner);
        spinner.setAdapter(simpleCursorAdapter);

    }



    //adds a category to the view and to the list of categories
    public ArrayList<String> addCategory(String toAdd, View dialogView){
        if(categories.contains(toAdd)) return categories;
        categories.add(toAdd);

        TableLayout tl = (TableLayout)dialogView.findViewById(R.id.tableLayout2);
        TableRow tr = new TableRow(getActivity());
        TextView textView = new TextView(getActivity());
        textView.setText(toAdd);
        tr.addView(textView);
        tl.addView(tr);
        return categories;
    }



    //populates the edit text based on selection from spinner
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        Cursor c = (Cursor)parent.getItemAtPosition(pos);
        String text = c.getString(c.getColumnIndex(TaskDBContract.TaskDB.COLUMN_NAME_CATEGORY_NAME));
        EditText editText = (EditText)getDialog().findViewById(R.id.editText5);
        editText.setText(text);

    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Nothing will happen

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
