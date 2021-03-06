package com.android_examples.slidingtab_android_examplescom;

/**
 * Created by liamdiamond on 31/01/2017.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class EditGoalActivity extends AppCompatActivity implements View.OnClickListener{

    Toolbar toolbar ;
    private Button btnDone;
    private EditText addGoalTitle;
    private EditText addGoalDistance;
    private Spinner spinner;
    private CustomTextInputLayout helperTextTitle;
    private CustomTextInputLayout helperTextDistance;
    private boolean titleValid;
    private boolean distanceValid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.edit_goal_activity);

        toolbar = (Toolbar) findViewById(R.id.toolbar1);

        setSupportActionBar(toolbar);

        titleValid = false;
        distanceValid = false;

        spinner = (Spinner) findViewById(R.id.addGoalUnitSelector);
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

        btnDone = (Button) findViewById(R.id.save);
        btnDone.setOnClickListener(this);
        
        setXMLValues();
    }

    private void setXMLValues() {
        addGoalTitle.setText(getIntent().getExtras().getString("title"), TextView.BufferType.EDITABLE);
        addGoalDistance.setText(getIntent().getExtras().getString("distance"), TextView.BufferType.EDITABLE);
        spinner.setSelection(((ArrayAdapter<String>)spinner.getAdapter()).getPosition(getIntent().getExtras().getString("units")));
    }


    @Override
    public void onClick(View v) {

        Context context = getApplicationContext();
        GoalContract.GoalDbHelper mDbHelper = new GoalContract.GoalDbHelper(context);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        if (titleValid && distanceValid){
            CharSequence text = "Hello toast!";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();

            String title = addGoalTitle.getText().toString().trim();
            Double distance = Double.parseDouble(addGoalDistance.getText().toString().trim());
            Spinner addGoalUnitSelector = (Spinner) findViewById(R.id.addGoalUnitSelector);
            String units = addGoalUnitSelector.getSelectedItem().toString();

            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(GoalContract.Goal.COLUMN_NAME_TITLE, title);
            values.put(GoalContract.Goal.COLUMN_NAME_DISTANCE, distance);
            values.put(GoalContract.Goal.COLUMN_NAME_UNITS, units);
            values.put(GoalContract.Goal.COLUMN_NAME_PROGRESS, 50.0);
            values.put(GoalContract.Goal.COLUMN_NAME_DATE, -1);

            // Insert the new row, returning the primary key value of the new row
            ContentValues cv = new ContentValues();
            cv.put(GoalContract.Goal.COLUMN_NAME_TITLE,title);
            cv.put(GoalContract.Goal.COLUMN_NAME_DISTANCE,distance);
            cv.put(GoalContract.Goal.COLUMN_NAME_UNITS,units);
            db.update(GoalContract.Goal.TABLE_NAME, cv, "_id="+getIntent().getIntExtra("id", 0), null);

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
    };

    private final TextWatcher distanceWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
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
    };

}
