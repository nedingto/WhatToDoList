package com.example.nick.whattodolist;

import android.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

import static android.widget.AdapterView.*;

/**
 * Created by Nick on 10/5/2015.
 */
public abstract class customSpinnerFragment extends Fragment
        implements OnItemSelectedListener{


    TaskDBContract dbContract;
    TaskDbHelper mDbHelper;
    SQLiteDatabase dbR;
    boolean freeForm = true;

    //Flags if the field had been updated
    boolean fieldChanged = false;

    View view;

    ArrayList<String> selectedOld = new ArrayList<>();
    ArrayList<String> selectedNew = new ArrayList<>();

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
    Bundle savedInstanceState){
       dbContract = new TaskDBContract();
       mDbHelper = new TaskDbHelper(getActivity().getApplicationContext());
       //set up reader
       dbR = mDbHelper.getReadableDatabase();

       //inflate view from layout
       view = inflater.inflate(R.layout.add_spinner, container, false);

       //set button to add to the category list
       Button btn = (Button)view.findViewById(R.id.button6);
       btn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               String toAdd = ((EditText) getView().findViewById(R.id.editText5)).getText().toString();
               addSelected(toAdd);
               fieldChanged = true;
           }
       });

               //add fields to the spinner list and set it's listener
       populateSpinnerList(view);
       Spinner spinner = (Spinner)view.findViewById(R.id.spinner);
       spinner.setOnItemSelectedListener(this);
       return view;
   }

    //this will specify whether or not the add field is editable,
    //i.e. if entries can be selected that are not already existent
    public void setFreeForm(boolean freeForm, View v) {
        this.freeForm = freeForm;
        getView().findViewById(R.id.editText5).setEnabled(freeForm);
    }


    //function that fills the spinner with categories from the DB by giving it an adapter
    private void populateSpinnerList( View v){


        //set query variables
        String[] projection = {
                giveIdName(),
                giveColumnName()
        };

        String[] fromColumn = {
                giveColumnName()
        };

        //get reader from the main activity
        Cursor cursor = dbR.query(
                giveTableName(),  // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );

        //give the adapter views to populate into and where it will access values and set it up with the spinner
        int[] toViewIds = {R.id.checkBox10};
        SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(v.getContext(), R.layout.list_layout,  cursor, fromColumn, toViewIds, 0);

        ((Spinner)v.findViewById(R.id.spinner)).setAdapter(simpleCursorAdapter);

    }

    //on button click for the add button, adds the button to the list view
    private void addSelected(String toAdd){
        if(selectedNew.contains(toAdd)) return;
        selectedNew.add(toAdd);
        TableLayout selectedLayout = (TableLayout)getView().findViewById(R.id.selectedLayout);
        TableRow newLayout = new TableRow(getActivity());
        TextView newTextView = new TextView(getActivity());
        Button newButton = new Button(getActivity());
        newTextView.setText(toAdd);
        newButton.setText("Delete");
        newButton.setTag(toAdd);
        newButton.setRight(0);
        newButton.setOnClickListener(new View.OnClickListener() {

            //button used to delete field
            @Override
            public void onClick(View v) {
                selectedNew.remove((String) v.getTag());
                ViewParent parent = v.getParent();
                ((ViewGroup) parent.getParent()).removeView((View) parent);
                fieldChanged = true;
            }
        });
        newLayout.addView(newTextView);
        newLayout.addView(newButton);
        selectedLayout.addView(newLayout);

    }

    //when an item is selected from the sspinner it populates the field next to it
    //should always assert that delete column is the same size as selected
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Cursor c = (Cursor) parent.getItemAtPosition(position);
        String text = c.getString(c.getColumnIndex(this.giveColumnName()));
        EditText editText = (EditText) getView().findViewById(R.id.editText5);
        editText.setText(text);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    //nothing happens
    }

    //returns all of the selected entries
    public ArrayList<String> getDeleted(){
        ArrayList<String> deleted = new ArrayList<>();
        deleted.addAll(selectedOld);
        deleted.removeAll(selectedNew);
        return deleted;
    }

    public ArrayList<String> getAdded(){
        ArrayList<String> added = new ArrayList();
        added.addAll(selectedNew);
        added.removeAll(selectedOld);
        return added;
    }

    //can populate the selected entries if some already exist
    public void setSelected(ArrayList<String> s){
        ((ViewGroup)getView().findViewById(R.id.selectedLayout)).removeAllViews();
        //the new start state is the entered state
        selectedOld = s;

        //add all from start state
        selectedNew.clear();
        for(String selection : selectedOld){
            addSelected(selection);
        }

    }

    public boolean fieldHasChanged(){
        return fieldChanged;
    }

    //the must be implemented to return db names
    //there probably is a way to make this more general
    abstract public String giveTableName();
    abstract public String giveColumnName();
    abstract public String giveIdName();


}
