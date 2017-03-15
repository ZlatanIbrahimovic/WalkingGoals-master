package com.android_examples.slidingtab_android_examplescom;

/**
 * Created by liamdiamond on 31/01/2017.
 */

import android.content.Context;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class AddProgressActivity extends AppCompatActivity{

    private EditText addGoalDistance;
    private CustomTextInputLayout helperTextDistance;
    private boolean distanceValid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        distanceValid = false;
        setContentView(R.layout.add_progress_activity);

        setupActionBar();
        initialiseSpinner();
        initialiseDistanceInput();
    }

    private void initialiseDistanceInput() {
        addGoalDistance = (EditText)findViewById(R.id.addGoalDistance);
        helperTextDistance = (CustomTextInputLayout)findViewById(R.id.tilCustom3);
        addGoalDistance.addTextChangedListener(distanceWatcher);
    }

    private void initialiseSpinner() {
        Spinner spinner = (Spinner) findViewById(R.id.addGoalUnitSelector);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.units_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }


    public void doneClick() {
        Context context = getApplicationContext();
        GoalContract.GoalDbHelper mDbHelper = new GoalContract.GoalDbHelper(context);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();


        if (distanceValid){
            Double distance = Double.parseDouble(addGoalDistance.getText().toString().trim());
            Spinner addGoalUnitSelector = (Spinner) findViewById(R.id.addGoalUnitSelector);
            String selectedUnits = addGoalUnitSelector.getSelectedItem().toString();
            String goalUnits = getIntent().getExtras().getString("units");

            Double convertedDistance = new DistanceConversion(distance, selectedUnits, goalUnits, getApplicationContext()).convert();

//            System.out.println("Converting " + distance + " " + selectedUnits + " to " + goalUnits + " : " + convertedDistance);

            mDbHelper.increaseProgress(db, getIntent().getIntExtra("id", 0), convertedDistance);

            String id = String.valueOf(getIntent().getIntExtra("id", 0));
            Intent returnIntent = new Intent();
            returnIntent.putExtra("progressAdded", String.valueOf(convertedDistance));
            returnIntent.putExtra("id", id);
            setResult(RESULT_OK,returnIntent);
            finish();
        }
    }

    public static boolean isNumeric(String str)
    {
        return str.matches("[0-9.]*");
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
        toolbarText.setText("Add progress");

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
