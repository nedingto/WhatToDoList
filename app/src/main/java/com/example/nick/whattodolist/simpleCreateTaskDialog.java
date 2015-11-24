package com.example.nick.whattodolist;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

import java.util.Calendar;

/**
 * Created by Nick on 9/20/2015.
 */
public class simpleCreateTaskDialog extends DialogFragment {
    public static String BUNDLE_TASK_NAME = "task_name";
    public static String BUNDLE_DUE_DATE = "due_date";
    public static String BUNDLE_CURRENT_YEAR = "current_year";
    public static String BUNDLE_CURRENT_MONTH = "current_month";
    public static String BUNDLE_CURRENT_DAY = "current_day";
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.simple_create_task_dialog, null);

        builder.setView(dialogView)
                //setting the message appears to be causing an issue
        .setMessage(R.string.simple_create_task_dilog)
                .setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //create the task
                        String taskName = ((EditText)((AlertDialog) dialog).findViewById(R.id.editText6)).getText().toString();
                        DatePicker dp = (DatePicker) ((AlertDialog) dialog).findViewById(R.id.datePicker);
                        //have to add some pieces to the string to use in sql database
                        Calendar cal = Calendar.getInstance();
                        cal.set(dp.getYear(), dp.getMonth(), dp.getDayOfMonth());
                        String dueDate = dateConverter.calendarToSql(cal);
                        mListener.onSimpleDialogPositiveClick(taskName, dueDate);
                    }
                })
                .setNeutralButton(R.string.next, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Move on to the next dialog pack a bundle to send to the next dialog.
                        //Might be useful to have this somehow happen beforehand so it can also be
                        //the arguments pass for the onPositive click function
                        Bundle args = new Bundle();
                        //TODO break the function for getting the date from the date picker out
                        String taskName = ((EditText) ((AlertDialog) dialog).findViewById(R.id.editText6)).getText().toString();
                        DatePicker dp = (DatePicker) ((AlertDialog) dialog).findViewById(R.id.datePicker);

                        Calendar cal = Calendar.getInstance();
                        cal.set(dp.getYear(), dp.getMonth(), dp.getDayOfMonth());
                        String dueDate = dateConverter.calendarToSql(cal);
                        //should be a better way of making sure these key names stay consistent
                        args.putString(BUNDLE_TASK_NAME, taskName);
                        args.putString(BUNDLE_DUE_DATE, dueDate);

                        //use the bundle to send to an advanced task dialog
                        DialogFragment newFragment = new advancedCreateTaskDialog();
                        newFragment.setArguments(args);
                        newFragment.show(getFragmentManager(), "create task advanced");
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog so do nothing
                    }
                });
        // Create the AlertDialog object and return it
        final AlertDialog alert =  builder.create();
        Bundle args = getArguments();
        DatePicker dp = (DatePicker)dialogView.findViewById(R.id.datePicker);
        dp.updateDate(args.getInt(BUNDLE_CURRENT_YEAR), args.getInt(BUNDLE_CURRENT_MONTH), args.getInt(BUNDLE_CURRENT_DAY));
        //set the keyboard to visible
        alert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        return alert;

    }
    public interface SimpleCreateTaskListener {
        public void onSimpleDialogPositiveClick(String taskName, String dueDate);
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
