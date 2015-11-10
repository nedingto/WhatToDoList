package com.example.nick.whattodolist;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Calendar;

/**
 * Created by Nick on 10/12/2015.
 */
public class createRepeatingDialog extends DialogFragment {
    //static strings to access bundles created by this dialog
    public static final String BUNDLE_PERIODICAL_NUM = "periodical_num";
    public static final String BUNDLE_PERIODICAL_PERIOD = "periodical_period";
    public static final String BUNDLE_ORDINAL_NUM = "ordinal_num";
    public static final String BUNDLE_ORDINAL_PERIOD = "ordinal_period";
    public static final String BUNDLE_DAY_OF_WEEK = "day_of_week";
    public static final String BUNDLE_START_DATE = "start_date";
    public static final String BUNDLE_END_DATE = "end_date";

    //This is a dialog for the creation of a repeating basis
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        //view used in this dialog
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.create_repeating_dialog, null);

        builder.setView(dialogView).setMessage(R.string.create_repeating_dialog)
                .setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //user clicked create, pass the information from the fields on
                        mListener.onRepeatingDialogPositiveClick(gatherFields());
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        mListener.onRepeatingDialogNegativeClick();

                    }
                });

        //Only give it a neural button for next if it came from the main activity
        if(getActivity() instanceof MainToDo){
            builder.setNeutralButton(R.string.next, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //use clicked next, pass on the data to the advanced dialog
                    DialogFragment newFragment = new advancedCreateTaskDialog();
                    newFragment.setArguments(gatherFields());
                    newFragment.show(getFragmentManager(), "create task advanced");
                }
            });
        }

        // Create the AlertDialog object to return
        final AlertDialog alert = builder.create();

        //If the dialog comes from the edit task, populate everything from the bundle
        //can be ui tested with preconditions
        Bundle args = getArguments();
        if(getActivity() instanceof EditTaskActivity){
            ((Spinner)dialogView.findViewById(R.id.spinner5)).setSelection(args.getInt(BUNDLE_PERIODICAL_NUM));
            ((Spinner)dialogView.findViewById(R.id.spinner2)).setSelection(args.getInt(BUNDLE_PERIODICAL_PERIOD));
            ((Spinner)dialogView.findViewById(R.id.spinner4)).setSelection(args.getInt(BUNDLE_ORDINAL_NUM));
            ((Spinner)dialogView.findViewById(R.id.spinner3)).setSelection(args.getInt(BUNDLE_ORDINAL_PERIOD));
            int[] dayOfWeek = args.getIntArray(BUNDLE_DAY_OF_WEEK);
            if(dayOfWeek[0] == 1) ((CheckBox)dialogView.findViewById(R.id.Sun)).setChecked(true);
            if(dayOfWeek[1] == 1) ((CheckBox)dialogView.findViewById(R.id.Mon)).setChecked(true);
            if(dayOfWeek[2] == 1) ((CheckBox)dialogView.findViewById(R.id.Tue)).setChecked(true);
            if(dayOfWeek[3] == 1) ((CheckBox)dialogView.findViewById(R.id.Wed)).setChecked(true);
            if(dayOfWeek[4] == 1) ((CheckBox)dialogView.findViewById(R.id.Thu)).setChecked(true);
            if(dayOfWeek[5] == 1) ((CheckBox)dialogView.findViewById(R.id.Fri)).setChecked(true);
            if(dayOfWeek[6] == 1) ((CheckBox)dialogView.findViewById(R.id.Sat)).setChecked(true);
            ((TextView)dialogView.findViewById(R.id.textView12)).setText(dateConverter.sqlToString(args.getString(BUNDLE_START_DATE)));
            ((TextView)dialogView.findViewById(R.id.textView23)).setText(dateConverter.sqlToString(args.getString(BUNDLE_END_DATE)));
        } else {

            //otherwise add a feild to task title and initialize the dates to today
            LinearLayout layout = (LinearLayout) dialogView.findViewById(R.id.taskNamePlaceholder);
            EditText editText = new EditText(getActivity());
            //make the edit text fill the view
            editText.setLayoutParams(new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            editText.setHint("Task Name");
            editText.requestFocus();
            editText.setId(R.id.task_name_reepeating_dialog);
            alert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            layout.addView(editText);

            //initialize the dates
            Calendar c = Calendar.getInstance();
            String today = dateConverter.calendarToString(c);
            ((TextView) dialogView.findViewById(R.id.textView12)).setText(today);
            ((TextView) dialogView.findViewById(R.id.textView23)).setText(today);
        }

        //A button for creating the startDate date picker
        Button btn1 = (Button)dialogView.findViewById(R.id.button10);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //convert the date string to a calendar and use that to initialize the date picker
                String date;
                date = ((TextView) getDialog().findViewById(R.id.textView12)).getText().toString();
                Calendar c = dateConverter.stringToCalendar(date);
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePicker = new DatePickerDialog(v.getContext(),
                        new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar cal = Calendar.getInstance();
                        cal.set(year, monthOfYear, dayOfMonth);
                        dateSet(cal, 0);
                    }
                }, year, month, day);
                datePicker.show();
            }
        });

        //same for the end date
        Button btn2 = (Button)dialogView.findViewById(R.id.button11);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //convert the date string to a calendar and use that to initialize the date picker
                String date;
                date = ((TextView) getDialog().findViewById(R.id.textView23)).getText().toString();
                Calendar c = dateConverter.stringToCalendar(date);
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePicker = new DatePickerDialog(v.getContext(),
                        new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar cal = Calendar.getInstance();
                        cal.set(year, monthOfYear, dayOfMonth);
                        dateSet(cal, 1);
                    }
                }, year, month, day);
                datePicker.show();
            }
        });

        //spinners and layouts for the different fields
        final Spinner  periodicalNumSpinner = ((Spinner) dialogView.findViewById(R.id.spinner5));
        final Spinner periodicalPeriodSpinner = (Spinner)dialogView.findViewById(R.id.spinner2);
        final Spinner ordinalNumSpinner = (Spinner)dialogView.findViewById(R.id.spinner4);
        final Spinner ordinalPeriodSpinner = (Spinner)dialogView.findViewById(R.id.spinner3);
        final LinearLayout onGroup = (LinearLayout) dialogView.findViewById(R.id.onGroup);
        final LinearLayout checkBoxGroup1 = (LinearLayout)dialogView.findViewById(R.id.checkGroup1);
        final LinearLayout checkBoxGroup2 = (LinearLayout)dialogView.findViewById(R.id.checkGroup2);

        //set up the first important spinner, should be ui tested in the fragment
        periodicalPeriodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ArrayAdapter adapter;
                if(position == getResources().getInteger(R.integer.periodical_period_day)) {
                    //If periodical day is selected, don't show any extra choices
                    onGroup.setVisibility(View.GONE);
                    checkBoxGroup1.setVisibility(View.GONE);
                    checkBoxGroup2.setVisibility(View.GONE);
                    //let the user pick a number from 1 to 50 for the periodical num
                    adapter = ArrayAdapter.createFromResource(getActivity(),
                            R.array.periodicial_numbers, R.layout.list_layout);
                    periodicalNumSpinner.setAdapter(adapter);
                } else if(position == getResources().getInteger(R.integer.periodical_period_week)) {
                    //If week is selected, show the check boxes but hide the other spinners
                    onGroup.setVisibility(View.GONE);
                    checkBoxGroup1.setVisibility(View.VISIBLE);
                    checkBoxGroup2.setVisibility(View.VISIBLE);
                    //let the user pick a number from 1 to 50 for the periodical num
                    adapter = ArrayAdapter.createFromResource(getActivity(),
                            R.array.periodicial_numbers, R.layout.list_layout);
                    periodicalNumSpinner.setAdapter(adapter);
                } else if (position == getResources().getInteger(R.integer.periodical_period_month)) {
                    //if the user selected month, they shouldn't see the check boxes but they
                    //should see the extra spinners
                    checkBoxGroup1.setVisibility(View.GONE);
                    checkBoxGroup2.setVisibility(View.GONE);
                    onGroup.setVisibility(View.VISIBLE);

                    //Should user periodical periods that apply to the month, including day or
                    //a specific day of the week
                    adapter = ArrayAdapter.createFromResource(getActivity(),
                            R.array.of_the_month, R.layout.list_layout);
                    ordinalPeriodSpinner.setAdapter(adapter);

                    //The user can only choose every one month for now
                    adapter = ArrayAdapter.createFromResource(getActivity(),
                            R.array.limited_periodicial_numbers, R.layout.list_layout);
                    periodicalNumSpinner.setAdapter(adapter);
                } else {
                    //Year period shows extra spinners but not check boxes
                    onGroup.setVisibility(View.VISIBLE);
                    checkBoxGroup1.setVisibility(View.GONE);
                    checkBoxGroup2.setVisibility(View.GONE);

                    //alows for user to pick month of the year ordinal only
                    adapter = ArrayAdapter.createFromResource(getActivity(),
                            R.array.of_the_year, R.layout.list_layout);
                    ordinalPeriodSpinner.setAdapter(adapter);

                    //user may only select every one year
                    adapter = ArrayAdapter.createFromResource(getActivity(),
                            R.array.limited_periodicial_numbers, R.layout.list_layout);
                    periodicalNumSpinner.setAdapter(adapter);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        //This spinner decides what ordinal number will be available
        ordinalPeriodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ArrayAdapter adapter;
                if(position == getResources().getInteger(R.integer.ordinal_period_day_or_month)) {
                    if (periodicalPeriodSpinner.getSelectedItemPosition() == getResources().getInteger(R.integer.periodical_period_month)) {
                        //If the periodical period is month and ordinal period is day, set the
                        //ordinal num to days of the month
                        adapter = ArrayAdapter.createFromResource(getActivity(),
                                R.array.day_of_month_ordinals, R.layout.list_layout);
                        ordinalNumSpinner.setAdapter(adapter);
                    } else {
                        //if the ordinal period is a day of the week set the ordinal num to weeks in
                        //the month
                        adapter = ArrayAdapter.createFromResource(getActivity(),
                                R.array.month_of_year_ordinals, R.layout.list_layout);
                        ordinalNumSpinner.setAdapter(adapter);
                    }
                } else {
                    //if using the year periodical, set the ordinal num to months in the year
                    adapter = ArrayAdapter.createFromResource(getActivity(),
                            R.array.week_of_month_ordinals, R.layout.list_layout);
                    ordinalNumSpinner.setAdapter(adapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        return alert;
    }

    public interface RepeatingCreateTaskListener {
        public void onRepeatingDialogPositiveClick(Bundle args);
        public void onRepeatingDialogNegativeClick();
    }

    // Use this instance of the interface to deliver action events
    RepeatingCreateTaskListener mListener;

    //can be ui tested in the fragment
    public void dateSet(Calendar cal, int fromField){
        //the date picker will only be in charge of fixing the date in the text view
        //as that is the string used for the update
        String dateString = dateConverter.calendarToString(cal);
        //pick the target text view based on the fromField argument
        //make sure the end date is never before the start date
        if(fromField == 0) {
            ((TextView) getDialog().findViewById(R.id.textView12)).setText(dateString);
            String otherDate  = ((TextView) getDialog().findViewById(R.id.textView23)).getText().toString();
            Calendar otherCal = dateConverter.stringToCalendar(otherDate);
            if(otherCal.before(cal)) {
                ((TextView) getDialog().findViewById(R.id.textView23)).setText(dateString);
            }
        } else {
            ((TextView) getDialog().findViewById(R.id.textView23)).setText(dateString);
            String otherDate  = ((TextView) getDialog().findViewById(R.id.textView12)).getText().toString();
            Calendar otherCal = dateConverter.stringToCalendar(otherDate);
            if(otherCal.after(cal)) {
                ((TextView) getDialog().findViewById(R.id.textView12)).setText(dateString);
            }
        }
    }

    //method for gathering the data from all of the fields and adding to a bundle
    //can be ui tested through the edit activity and checking expected changes
    public Bundle gatherFields(){
        Bundle args = new Bundle();
        if(getActivity() instanceof MainToDo) {
            String taskName = ((EditText) getDialog().findViewById(R.id.task_name_reepeating_dialog)).getText().toString();
            args.putString(simpleCreateTaskDialog.BUNDLE_TASK_NAME,taskName);
        }
        int periodicalNum = ((Spinner) getDialog().findViewById(R.id.spinner5)).getSelectedItemPosition() + 1;
        int periodicalPeriod = ((Spinner) getDialog().findViewById(R.id.spinner2)).getSelectedItemPosition();
        int ordinalNum = -1;
        int ordinalPeriod = -1;
        int[] dayOfWeek = {0,0,0,0,0,0,0};
        if(periodicalPeriod == getResources().getInteger(R.integer.periodical_period_week)){
            if(((CheckBox)getDialog().findViewById(R.id.Sun)).isChecked()){
                dayOfWeek[0] = 1;
            }
            if(((CheckBox)getDialog().findViewById(R.id.Mon)).isChecked()){
                dayOfWeek[1] = 1;
            }
            if(((CheckBox)getDialog().findViewById(R.id.Tue)).isChecked()){
                dayOfWeek[2] = 1;
            }
            if(((CheckBox)getDialog().findViewById(R.id.Wed)).isChecked()){
                dayOfWeek[3] = 1;
            }
            if(((CheckBox)getDialog().findViewById(R.id.Thu)).isChecked()){
                dayOfWeek[4] = 1;
            }
            if(((CheckBox)getDialog().findViewById(R.id.Fri)).isChecked()){
                dayOfWeek[5] = 1;
            }
            if(((CheckBox)getDialog().findViewById(R.id.Sat)).isChecked()){
                dayOfWeek[6] = 1;
            }
        } else if(periodicalPeriod > getResources().getInteger(R.integer.periodical_period_week)){
            ordinalNum = ((Spinner)getDialog().findViewById(R.id.spinner4)).getSelectedItemPosition();
            ordinalPeriod = ((Spinner)getDialog().findViewById(R.id.spinner3)).getSelectedItemPosition();
        } else {
            //all other fields stay the same
        }
        //make sure they are good for sql
        String startDate = ((TextView)getDialog().findViewById(R.id.textView12)).getText().toString();
        startDate = dateConverter.stringToSql(startDate);
        String endDate = ((TextView)getDialog().findViewById(R.id.textView23)).getText().toString();
        endDate = dateConverter.stringToSql(endDate);

        args.putInt(BUNDLE_PERIODICAL_NUM, periodicalNum);
        args.putInt(BUNDLE_PERIODICAL_PERIOD, periodicalPeriod);
        args.putInt(BUNDLE_ORDINAL_NUM, ordinalNum);
        args.putInt(BUNDLE_ORDINAL_PERIOD, ordinalPeriod);
        args.putIntArray(BUNDLE_DAY_OF_WEEK, dayOfWeek);
        args.putString(BUNDLE_START_DATE, startDate);
        args.putString(BUNDLE_END_DATE, endDate);
        return args;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (RepeatingCreateTaskListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}
