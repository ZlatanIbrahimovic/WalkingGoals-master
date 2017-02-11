package com.android_examples.slidingtab_android_examplescom;

/**
 * Created by liamdiamond on 31/01/2017.
 */

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class HistoryActivity extends AppCompatActivity{

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

        setContentView(R.layout.history_activity);

        toolbar = (Toolbar) findViewById(R.id.toolbar1);

        setSupportActionBar(toolbar);

        populateSpinnerOptions();


    }

    /*Populate the select view Spinner and select units Spinner with options*/
    private void populateSpinnerOptions() {
        // Populate Select View Spinner
        Spinner viewSpinner = (Spinner) findViewById(R.id.historyViewSelector);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.viewArray, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        viewSpinner.setAdapter(adapter);

        // Populate Select Unit Spinner
        Spinner unitsSpinner = (Spinner) findViewById(R.id.historyUnitSelector);
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this,
                R.array.units_array, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unitsSpinner.setAdapter(arrayAdapter);
    }

}
