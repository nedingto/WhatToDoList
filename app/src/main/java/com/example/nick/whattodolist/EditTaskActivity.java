package com.example.nick.whattodolist;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

public class EditTaskActivity extends AppCompatActivity
        implements cascadeChangesDialog.cascadeChangesDialogListener,
        createRepeatingDialog.RepeatingCreateTaskListener,
        discardChangesDialog.discardChangesDialogListener,
        confirmBasisChangeDialog.confirmationBasisChangeDialogListener,
        cascadeDeleteDialog.cascadeDeleteDialogListener{

    //id of the current task
    int taskId;

    //repeating basis bundle, may be empty if there is none
    Bundle basisBundle = new Bundle();
    boolean isRepeating = false;

    //editors to preform task operations
    TaskEditor taskEditor;
    RepeatingBasisEditor repeatingBasisEditor;

    spinnerPopulater fragment;

    //fields to be gathered
    int repeatingId;
    String taskName;
    String dueDate;
    int priority;
    int checked;
    int estimation;
    ArrayList<String> added;
    ArrayList<String> deleted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        //initialize the editors
        taskEditor = new TaskEditor(getApplicationContext());
        repeatingBasisEditor = new RepeatingBasisEditor(getApplicationContext());

        Intent intent = getIntent();
        String value = intent.getStringExtra("taskId");

        //fill all the fields
        populateEdit(value);

        //initialize field values
        gatherFields();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_task, menu);
        return true;
    }

    public void populateEdit(String id) {
        Bundle taskBundle = taskEditor.getTask(id);

        //set taskId
        taskId = Integer.parseInt(id);

        //set the checked value
        CheckBox checkBox = ((CheckBox)findViewById(R.id.checkBox11));
        checkBox.setChecked(taskBundle.getInt(TaskEditor.BUNDLE_CHECKED) != 0);

        //set title
        EditText title = ((EditText) findViewById(R.id.editText));
        title.setText(taskBundle.getString(TaskEditor.BUNDLE_TASK_NAME));

        //set due date, and format it
        TextView dueDate = ((TextView) findViewById(R.id.textView21));
        String dueDateText = taskBundle.getString(TaskEditor.BUNDLE_DUE_DATE);
        dueDate.setText(dateConverter.sqlToString(dueDateText));

        //set priority
        RatingBar priority = ((RatingBar) findViewById(R.id.ratingBar));
        priority.setRating(taskBundle.getInt(TaskEditor.BUNDLE_PRIORITY));

        //set estimated mins
        EditText estimatedMin = ((EditText) findViewById(R.id.editText2));
        estimatedMin.setText(String.valueOf(taskBundle.getInt(TaskEditor.BUNDLE_ESTIMATION)));

        //make the repeating bundle in case the basis button is clicked
        repeatingId = taskBundle.getInt(TaskEditor.BUNDLE_BASIS_ID);
        basisBundle = repeatingBasisEditor.getBasis(repeatingId);

        //decide whether or not there are others with the same basis
        if(repeatingId != -1){
            isRepeating = true;
            TextView recurringIndicator = ((TextView) findViewById((R.id.textView26)) );
            recurringIndicator.setText("Yes");
        }

        fragment = (spinnerPopulater) getFragmentManager().findFragmentById(R.id.spinner_fragment);
        fragment.setSelected(taskEditor.getCategories(id));
    }

    /*save changes handling*/
    public void onSaveClicked(View v) {
        //if it is repeating, ask if the user wants the changes to go through the entire series
        if(isRepeating) {
            DialogFragment newFragment = new cascadeChangesDialog();
            newFragment.show(getFragmentManager(), "cascade changes");
        } else {
            //if it is not repeating, just follow through as usual
            gatherFields();
            taskEditor.updateFields(taskName, dateConverter.stringToSql(dueDate), priority,
                    estimation, checked, taskId);
            taskEditor.updateCategories(added, deleted, taskId);
            finish();
        }
    }

    @Override
    public void onCascadeChangesPositiveClick() {
        //user wants to cascade to the following tasks
        gatherFields();
        ArrayList<Integer> taskIds;
        taskIds = repeatingBasisEditor.getBasisTaskIds(repeatingId,
                dateConverter.stringToSql(dueDate));
        for (int id:taskIds){
            //don't cascade changes made to the completion or to the due date
            taskEditor.updateFields(taskName, null, priority, estimation, -1, id);
            taskEditor.updateCategories(added, deleted, id);
        }
        finish();
    }

    @Override
    public void onCascadeChangesNegativeClick() {
        //user does not want to cascade the changes
        gatherFields();
        taskEditor.updateFields(taskName, dateConverter.stringToSql(dueDate),
                priority, estimation, checked, taskId);
        taskEditor.updateCategories(added, deleted, taskId);
        finish();
    }

    @Override
    public void onCascadeChangesNeutralClick() {
        //this will apply to all tasks from the beginning of time
        gatherFields();
        ArrayList<Integer> taskIds;
        taskIds = repeatingBasisEditor.getBasisTaskIds(repeatingId, dateConverter.firstDateSql);
        for (int id:taskIds){
            taskEditor.updateFields(taskName, null, priority, estimation, -1, id);

            taskEditor.updateCategories(added, deleted, id);
        }
        finish();
    }


    /*delete handling*/
    public void onDeleteClicked(View v){
        //should check if the user wants this to cascade
        if(isRepeating) {
            DialogFragment newFragment = new cascadeDeleteDialog();
            newFragment.show(getFragmentManager(), "delete changes");
        } else {
            //if it is not repeating, just delete it
            taskEditor.deleteTask(taskId);
            finish();
        }
    }

    @Override
    public void onCascadeDeletePositiveClick() {
        ArrayList<Integer> taskIds;
        taskIds = repeatingBasisEditor.getBasisTaskIds(repeatingId, dateConverter.stringToSql(dueDate));
        for (int id:taskIds) {
            taskEditor.deleteTask(id);
        }
        finish();
    }

    @Override
    public void onCascadeDeleteNegativeClick() {
        taskEditor.deleteTask(taskId);
        finish();
    }

    @Override
    public void onCascadeDeleteNeutralClick() {
        //this will apply the changes to all tasks in the series
        ArrayList<Integer> taskIds;
        taskIds = repeatingBasisEditor.getBasisTaskIds(repeatingId, dateConverter.firstDateSql);
        for (int id:taskIds) {
            taskEditor.deleteTask(id);
        }
        finish();
    }

    public void dueDateClicked(View v){
        //get date ints from the string to set the starting date
        Calendar cal = dateConverter.stringToCalendar(dueDate);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePicker = new DatePickerDialog(v.getContext(), new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar cal = Calendar.getInstance();
                cal.set(year, monthOfYear, dayOfMonth);
                dateSet(cal);
            }
        }, year, month, day);
        datePicker.show();
    }

    //handling or cahnging the due date
    public void dateSet(Calendar cal){
        String dateString = dateConverter.calendarToString(cal);
        ((TextView) findViewById(R.id.textView21)).setText(dateString);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void gatherFields(){
        taskName = ((EditText)findViewById(R.id.editText)).getText().toString();
        dueDate = ((TextView) findViewById(R.id.textView21)).getText().toString();
        priority = (int)((RatingBar) findViewById(R.id.ratingBar)).getRating();
        checked = ((CheckBox) findViewById(R.id.checkBox11)).isChecked()? 1 : 0;
        estimation = Integer.parseInt(((EditText) findViewById(R.id.editText2)).getText().toString());
        added = fragment.getAdded();
        deleted = fragment.getDeleted();
    }

    /*All of the methods handling the repeating clicks*/
    public void repeatingClicked(View v){
        //first, warn the user that their changes will be discarded
        DialogFragment saveFragment = new discardChangesDialog();
        saveFragment.show(getFragmentManager(), "discard changes");
    }

    @Override
    public void onDiscardChangesPositiveClick() {
        //should show the repeating dialog, populate it if needs it
        DialogFragment newFragment = new createRepeatingDialog();
        newFragment.setArguments(basisBundle);
        newFragment.show(getFragmentManager(), "edit basis");
    }

    @Override
    public void onRepeatingDialogPositiveClick(Bundle repeatingBundle) {
        //should give the user a final warning
        DialogFragment commitFragment = new confirmBasisChangeDialog();
        commitFragment.setArguments(repeatingBundle);
        commitFragment.show(getFragmentManager(), "confirm change");
    }

    @Override
    public void onRepeatingDialogNegativeClick() {
        //user canceled the repeating dialog, keep good on promise to discard the changes
        finish();
    }

    @Override
    public void onConfirmationBasisChangePositiveClick(Bundle repeatingBundle) {
        //should delete all of the tasks with this basis that come after the start date
        String startDateSql = createRepeatingDialog.BUNDLE_START_DATE;
        ArrayList<Integer> basisTaskIds = repeatingBasisEditor.getBasisTaskIds(repeatingId,
                repeatingBundle.getString(startDateSql));
        for(Integer id:basisTaskIds){
            taskEditor.deleteTask(id);
        }
        //then delete this basis (only deletes if there are no other tasks related to it)
        repeatingBasisEditor.deleteBasis(repeatingId);

        //create the new basis
        float basisId = repeatingBasisEditor.createBasis(
                repeatingBundle.getInt(createRepeatingDialog.BUNDLE_PERIODICAL_NUM),
                repeatingBundle.getInt(createRepeatingDialog.BUNDLE_PERIODICAL_PERIOD),
                repeatingBundle.getInt(createRepeatingDialog.BUNDLE_ORDINAL_NUM),
                repeatingBundle.getInt(createRepeatingDialog.BUNDLE_ORDINAL_PERIOD),
                repeatingBundle.getIntArray(createRepeatingDialog.BUNDLE_DAY_OF_WEEK),
                repeatingBundle.getString(createRepeatingDialog.BUNDLE_START_DATE),
                repeatingBundle.getString(createRepeatingDialog.BUNDLE_END_DATE));
        ArrayList<String> dates = repeatingBasisEditor.getBasisDates(basisId);
        //lastly create the new tasks
        for(String date: dates){
            taskEditor.createTask(taskName, date, priority, estimation, (int) basisId);
        }
        finish();
    }

    @Override
    public void onConfirmationBasisChangeNegativeClick() {
        //user canceled the repeating dialog, keep good on promise to discard the changes
        finish();
    }
}

