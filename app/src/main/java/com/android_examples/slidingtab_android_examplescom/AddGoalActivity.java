package com.android_examples.slidingtab_android_examplescom;

/**
 * Created by liamdiamond on 31/01/2017.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class AddGoalActivity extends AppCompatActivity{

    Toolbar toolbar ;
    private Button btnDone;
    private EditText addGoalTitle;
    private EditText addGoalDistance;
    private CustomTextInputLayout helperTextTitle;
    private CustomTextInputLayout helperTextDistance;
    private boolean titleValid;
    private boolean distanceValid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.add_goal_activity);
        setupActionBar();

        titleValid = false;
        distanceValid = false;


        Spinner spinner = (Spinner) findViewById(R.id.addGoalUnitSelector);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.units_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        addGoalTitle = (EditText)findViewById(R.id.addGoalTitle);
        helperTextTitle = (CustomTextInputLayout)findViewById(R.id.tilCustom4);
        addGoalTitle.addTextChangedListener(titleWatcher);

        addGoalDistance = (EditText)findViewById(R.id.addGoalDistance);
        helperTextDistance = (CustomTextInputLayout)findViewById(R.id.tilCustom3);
        addGoalDistance.addTextChangedListener(distanceWatcher);

    }

    public void doneClick() {

        checkTitle();
        checkDistance();

        Context context = getApplicationContext();
        GoalContract.GoalDbHelper mDbHelper = new GoalContract.GoalDbHelper(context);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        if (titleValid && distanceValid){
            String title = addGoalTitle.getText().toString().trim();
            Double distance = Double.parseDouble(addGoalDistance.getText().toString().trim());
            Spinner addGoalUnitSelector = (Spinner) findViewById(R.id.addGoalUnitSelector);
            String units = addGoalUnitSelector.getSelectedItem().toString();

            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(GoalContract.Goal.COLUMN_NAME_TITLE, title);
            values.put(GoalContract.Goal.COLUMN_NAME_DISTANCE, distance);
            values.put(GoalContract.Goal.COLUMN_NAME_UNITS, units);
            values.put(GoalContract.Goal.COLUMN_NAME_PROGRESS, 0.0);
            values.put(GoalContract.Goal.COLUMN_NAME_DATE, -1);

            // Insert the new row, returning the primary key value of the new row
            db.insert(GoalContract.Goal.TABLE_NAME, null, values);

            Toast toast = Toast.makeText(context, "Goal added", Toast.LENGTH_SHORT);
            toast.show();

            finish();
        }
    }

    public static boolean isNumeric(String str)
    {
        return str.matches("[0-9.]*");
    }


    private final TextWatcher titleWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            checkTitle();
        }
    };

    public void checkTitle(){
        if (addGoalTitle.getText().toString().trim().length() >= 20) {
            helperTextTitle.setHelperText("Title must be less than 20 characters long");
            titleValid = false;
            return;
        }
        else if (addGoalTitle.getText().toString().trim().length() == 0) {
            helperTextTitle.setHelperText("Please enter a goal title");
            titleValid = false;
            return;
        }
        else{
            helperTextTitle.setHelperText("");
            titleValid = true;
        }
    }

    private final TextWatcher distanceWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            checkDistance();
        }
    };

    public void checkDistance(){
        if (addGoalDistance.getText().toString().length() == 0){
            helperTextDistance.setHelperText("Enter a numeric value");
            distanceValid = false;
            return;
        }
        else if (!isNumeric(addGoalDistance.getText().toString())) {
            helperTextDistance.setHelperText("Distance must be numeric");
            distanceValid = false;
            return;
        }
        else{
            helperTextDistance.setHelperText("");
            distanceValid = true;
        }
    }

    private void setupActionBar()
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setContentInsetsRelative(0, 0);
        setSupportActionBar(toolbar);

        ActionBar bar = getSupportActionBar();

        // custom view
        LayoutInflater inflater = (LayoutInflater) bar.getThemedContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.ab_donebar, null);

        TextView toolbarText = (TextView) customView.findViewById(R.id.toolbarText);
        toolbarText.setText("Add goal");

        customView.findViewById(R.id.ab_donebar_done).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                doneClick();
            }
        });
        customView.findViewById(R.id.ab_donebar_cancel).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });

        // setup action bar
        bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM, ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        bar.setCustomView(customView, new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

}
