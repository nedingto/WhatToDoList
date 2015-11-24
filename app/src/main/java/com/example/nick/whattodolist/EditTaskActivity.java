package com.example.nick.whattodolist;

import android.app.DatePickerDialog;
import android.support.v4.app.DialogFragment;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.support.v4.app.FragmentManager;

import java.util.ArrayList;
import java.util.Calendar;

public class EditTaskActivity extends AppCompatActivity
        implements cascadeChangesDialog.cascadeChangesDialogListener,
        createRepeatingDialog.RepeatingCreateTaskListener,
        discardChangesDialog.discardChangesDialogListener,
        confirmBasisChangeDialog.confirmationBasisChangeDialogListener,
        cascadeDeleteDialog.cascadeDeleteDialogListener,
        View.OnClickListener,
        View.OnLongClickListener,
        simpleCreateTaskDialog.SimpleCreateTaskListener,
        advancedCreateTaskDialog.AdvancedCreateTaskListener,
        addExistingSubTaskDialog.addSubTaskDialogListener {

    //id of the current task
    int taskId;

    //repeating basis bundle, may be empty if there is none
    Bundle basisBundle = new Bundle();
    boolean isRepeating = false;

    //editors to preform task operations
    TaskEditor taskEditor;
    com.example.nick.whattodolist.repeatingBasisEditor repeatingBasisEditor;

    //custom spinner for the category selecton
    customSpinnerFragment fragment;

    //fields to be gathered
    int repeatingId;
    String taskName;
    String dueDate;
    int priority;
    int checked;
    int estimation;
    ArrayList<String> addedCategories;
    ArrayList<String> deletedCategories;

    //fields for task relations
    TaskRelationManager relationManager;
    ArrayList<Integer> originalSubIds;
    ArrayList<Integer>newSubIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        //initialize the editors
        taskEditor = new TaskEditor(getApplicationContext());
        repeatingBasisEditor = new repeatingBasisEditor(getApplicationContext());
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
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        //set due date, and format it
        TextView dueDate = ((TextView) findViewById(R.id.textView21));
        String dueDateText = taskBundle.getString(TaskEditor.BUNDLE_DUE_DATE);
        dueDate.setText(dateConverter.sqlToString(dueDateText));

        //set priority
        RatingBar priority = ((RatingBar) findViewById(R.id.ratingBar2));
        priority.setRating(taskBundle.getInt(TaskEditor.BUNDLE_PRIORITY));

        //set estimated mins
        EditText estimatedMin = ((EditText) findViewById(R.id.editText10));
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

        //initialize the sub lists, and get all of the sub tasks
        originalSubIds = new ArrayList<>();
        newSubIds = new ArrayList<>();
        relationManager = new TaskRelationManager(getApplicationContext(), taskId);
        ArrayList<Bundle> subTasks = taskEditor.getSubTasks(taskId);
        for(Bundle task: subTasks){
            //pt eac sub task in the sub task view
            originalSubIds.add(task.getInt(taskEditor.BUNDLE_TASK_ID));
            addSubTask(task);
        }

        fragment = (customSpinnerFragment) getSupportFragmentManager().findFragmentById(R.id.spinner_fragment);
        fragment.setSelected(taskEditor.getCategories(id));
    }

    /*save changes handling*/
    public void onSaveClicked(View v) {
        //if it is repeating, ask if the user wants the changes to go through the entire series
        if(isRepeating) {
            DialogFragment newFragment = new cascadeChangesDialog();
            newFragment.show(getSupportFragmentManager(), "cascade changes");
        } else {
            //if it is not repeating, just follow through as usual
            gatherFields();
            taskEditor.updateFields(taskName, dateConverter.stringToSql(dueDate), priority,
                    estimation, checked, taskId);
            taskEditor.updateCategories(addedCategories, deletedCategories, taskId);
            saveRelations();
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
            taskEditor.updateCategories(addedCategories, deletedCategories, id);
        }
        finish();
    }

    @Override
    public void onCascadeChangesNegativeClick() {
        //user does not want to cascade the changes
        gatherFields();
        taskEditor.updateFields(taskName, dateConverter.stringToSql(dueDate),
                priority, estimation, checked, taskId);
        taskEditor.updateCategories(addedCategories, deletedCategories, taskId);
        saveRelations();
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

            taskEditor.updateCategories(addedCategories, deletedCategories, id);
        }
        finish();
    }


    /*delete handling*/
    public void onDeleteClicked(View v){
        //should check if the user wants this to cascade
        if(isRepeating) {
            DialogFragment newFragment = new cascadeDeleteDialog();
            newFragment.show(getSupportFragmentManager(), "delete changes");
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

    //handling or changes the due date
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
        priority = (int)((RatingBar) findViewById(R.id.ratingBar2)).getRating();
        checked = ((CheckBox) findViewById(R.id.checkBox11)).isChecked()? 1 : 0;
        String estimationText = ((EditText) findViewById(R.id.editText10)).getText().toString();
        if(estimationText.equals(""))estimation = 0;
        else estimation = Integer.parseInt(estimationText);
        addedCategories = fragment.getAdded();
        deletedCategories = fragment.getDeleted();
    }

    /*All of the methods handling the repeating clicks*/
    public void repeatingClicked(View v){
        //first, warn the user that their changes will be discarded
        DialogFragment saveFragment = new discardChangesDialog();
        Bundle args = new Bundle();
        saveFragment.setArguments(args);
        saveFragment.show(getSupportFragmentManager(), "discard changes");
    }

    @Override
    public void onDiscardChangesPositiveClick() {
        //should show the repeating dialog, populate it if needs it
        DialogFragment newFragment = new createRepeatingDialog();
        newFragment.setArguments(basisBundle);
        newFragment.show(getSupportFragmentManager(), "edit basis");
    }

    @Override
    public void onRepeatingDialogPositiveClick(Bundle repeatingBundle) {
        //should give the user a final warning
        DialogFragment commitFragment = new confirmBasisChangeDialog();
        commitFragment.setArguments(repeatingBundle);
        commitFragment.show(getSupportFragmentManager(), "confirm change");
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

    //listener for deleting a sub task
    @Override
    public void onClick(View v) {
        //remove the relation, based on the id tag on the parent layout and delete the field
        View parent = (View)v.getParent();
        int subId = (Integer)parent.getTag();
        if(subId >= 0)relationManager.removeTmpRelations(taskId, subId);
        else relationManager.removeTmpTask(subId);

        //update the sub tasks
        newSubIds.remove((Integer)subId);
        ((ViewGroup)parent.getParent()).removeView(parent);
    }

    //listener for going to a sub task
    @Override
    public boolean onLongClick(View v) {
        DialogFragment saveFragment = new discardChangesDialog();
        //will get task id from the parent layout
        View parent = (View)v.getParent();
        int taskId = (Integer)parent.getTag();
        //nothing will happen if this is a new task that hasn't been saved yet
        if(taskId >= 0) {
            Bundle taskIdBundle = new Bundle();
            taskIdBundle.putInt(taskEditor.BUNDLE_TASK_ID, taskId);
            //give the dialog an argument of the selected task id
            saveFragment.setArguments(taskIdBundle);
            saveFragment.show(getSupportFragmentManager(), "discard changes");
        }
        return false;
    }

    @Override
    public void onDiscardChangesPositiveClick(int taskId) {
        //if the user is ok with discarding changes, continue to opoen edit task for given task
        Intent myIntent = new Intent(EditTaskActivity.this, EditTaskActivity.class);
        myIntent.putExtra("taskId", String.valueOf(taskId));
        EditTaskActivity.this.startActivity(myIntent);
        this.finish();
    }

    private void saveRelations(){
        //when save is clicked, will save all relation changes
        newSubIds = new ArrayList<>();

        //keeps track of incomplete stub tasks
        int incompleteSub = 0;

        LinearLayout subLayout = (LinearLayout)findViewById(R.id.sub_layout);
        for(int i = 0; i<subLayout.getChildCount();i++){
            View child = subLayout.getChildAt(i);
            int subId = (Integer)child.getTag();

            //check box will be at index 0 always, if it is unchecked ad to incomplete sub
            CheckBox cb = (CheckBox)((ViewGroup) child).getChildAt(0);
            int checked = 1;
            if(!cb.isChecked()){
                checked = 0;
                incompleteSub++;
            }
            //if the id does not already exists it will be below 0 and it should be created
            if(subId < 0){
                Bundle taskBundle = relationManager.getTmpTask(subId);
                int newTaskId = taskEditor.createTask(
                        taskBundle.getString(taskEditor.BUNDLE_TASK_NAME),
                        taskBundle.getString(taskEditor.BUNDLE_DUE_DATE),
                        taskBundle.getInt(taskEditor.BUNDLE_PRIORITY),
                        taskBundle.getInt(taskEditor.BUNDLE_ESTIMATION), -1);
                subId = newTaskId;
            }
            //update the checked value and add it to the list
            taskEditor.updateChecked(subId, checked);
            newSubIds.add(subId);
        }

        //added tasks will be all those in the new list but not in the old one
        ArrayList<Integer> addedSub = new ArrayList<>();
        addedSub.addAll(newSubIds);
        addedSub.removeAll(originalSubIds);
        //this will be the same as the deleted tasks, since it is all the original ids sans the
        //new tasks
        originalSubIds.removeAll(newSubIds);
        taskEditor.updateRelations(addedSub, originalSubIds, taskId, incompleteSub);
    }

    private void addSubTask(Bundle task){
        //will only add the task if it is not already listed
        int subTaskId = task.getInt(taskEditor.BUNDLE_TASK_ID);
        if(!newSubIds.contains(subTaskId)) {
            newSubIds.add(subTaskId);
            //adds the relation to the manager
            relationManager.createTmpRelation(taskId, subTaskId);

            //creates a layout, text view to put the task name, a check box and a delete button
            LinearLayout layout = new LinearLayout(getApplication());
            layout.setTag(subTaskId);
            CheckBox cb = new CheckBox(getApplication());
            TextView tv = new TextView(getApplication());
            Button btn = new Button(getApplication());
            btn.setText("Delete");

            tv.setText(task.getString(taskEditor.BUNDLE_TASK_NAME));
            if (task.getInt(taskEditor.BUNDLE_CHECKED) == 1) cb.setChecked(true);
            if (task.getInt(taskEditor.BUNDLE_INCOMPLETE_SUB) != 0) tv.setTextColor(Color.GRAY);

            //set up the listeners
            btn.setOnClickListener(this);
            tv.setOnLongClickListener(this);

            layout.addView(cb);
            layout.addView(tv);
            layout.addView(btn);
            ((LinearLayout) findViewById(R.id.sub_layout)).addView(layout);
        }
    }

    public void addNewSubClicked(View v){
        //this starts when the user wants to create a new task for a sub task
        DialogFragment newFragment = new simpleCreateTaskDialog();
        newFragment.show(getSupportFragmentManager(), "create task");
    }

    @Override
    public long onAdvancedDialogPositiveClick(String taskName, String dueDate, int priority, int estimatedMins, ArrayList<String> categories, Bundle repeatingBundle) {
        //creates a bundle for the task
        Bundle newTaskBundle = new Bundle();
        newTaskBundle.putString(taskEditor.BUNDLE_TASK_NAME, taskName);
        newTaskBundle.putString(taskEditor.BUNDLE_DUE_DATE, dueDate);
        newTaskBundle.putInt(taskEditor.BUNDLE_PRIORITY, priority);
        newTaskBundle.putInt(taskEditor.BUNDLE_ESTIMATION, estimatedMins);
        newTaskBundle.putStringArrayList(taskEditor.BUNDLE_CATEGORIES, categories);
        int tmpTaskId = relationManager.addTmpTask(newTaskBundle);
        newTaskBundle.putInt(taskEditor.BUNDLE_TASK_ID, tmpTaskId);

        //checked and incomplete sub will always be 0 to start
        newTaskBundle.putInt(taskEditor.BUNDLE_INCOMPLETE_SUB, 0);
        newTaskBundle.putInt(taskEditor.BUNDLE_CHECKED, 0);
        addSubTask(newTaskBundle);
        return 0;
    }

    @Override
    public void onAdvancedDialogNeutralClick(String taskName, String dueDate, int priority, int estimatedMins, ArrayList<String> categories, Bundle repeatingBundle) {
        //will not be possible
    }

    @Override
    public void onSimpleDialogPositiveClick(String taskName, String dueDate) {
        //uses almost the same code as onAdvancedDialogPositiveClick
        onAdvancedDialogPositiveClick(taskName, dueDate, 1, 0, new ArrayList<String>(), new Bundle());
    }

    public void addExistingClicked(View v){
        //brings up the existing task dialog
        DialogFragment newFragment = new addExistingSubTaskDialog();
        newFragment.setArguments(relationManager.getAllowedTaskBundle());
        newFragment.show(getSupportFragmentManager(), "create task");
    }

    @Override
    public void onAddSubTaskPositiveClick(int subTaskId) {
        //adds the sup task the user requested
        addSubTask(taskEditor.getTask(String.valueOf(subTaskId)));
    }
}

