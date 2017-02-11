package com.android_examples.slidingtab_android_examplescom;

/**
 * Created by liamdiamond on 31/01/2017.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Tab_2_Activity extends Fragment {

    private View view;
    private Spinner viewSpinner;
    private Spinner unitsSpinner;
    private RangeSeekBar rangeSeekBar;
    private ArrayList<GoalsListDisplay> historyResults;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view =  inflater.inflate(R.layout.activity_tab_2, container, false);
        this.view = view;

        populateSpinnerOptions();
        setupSpinnerListeners();
        setupSeekbar();
        retrieveGoals();
        initialiseList();

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            retrieveGoals();
            initialiseList();
        }
    }


    private void initialiseList() {
        ListView listView = (ListView) view.findViewById(R.id.historyList);
        ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();

        if(historyResults != null){
            for (int i = 0; i < historyResults.size(); i++) {
                Map<String, String> datum = new HashMap<String, String>(2);
                datum.put("title", historyResults.get(i).getTitle());
                datum.put("distance", historyResults.get(i).getDistance() + " " + historyResults.get(i).getUnits());
                datum.put("progress", Double.toString(historyResults.get(i).getProgress()));
                datum.put("percentage", String.format("%.1f", historyResults.get(i).getPercentage()) + "%");
                datum.put("date", historyResults.get(i).readableDateFormat());
                data.add(datum);
            }
        }
        else{
            Map<String, String> datum = new HashMap<String, String>(2);
            data.add(datum);
        }
        
        SimpleAdapter adapter = new SimpleAdapter(view.getContext(), data, R.layout.history_row_layout, new String[]{"title", "distance", "progress", "percentage", "date"}, new int[]{R.id.title, R.id.distance, R.id.progress, R.id.percent, R.id.date});
        listView.setAdapter(adapter);
    }

    private void setupSeekbar() {
        rangeSeekBar = (RangeSeekBar) view.findViewById(R.id.rangeSeekBar);
        rangeSeekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener() {

            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar bar, Object minValue, Object maxValue) {
                retrieveGoals();
                System.out.println(historyResults.size());
                initialiseList();
            }
        });
    }

    /*Set up the view and units spinners with listeners*/
    private void setupSpinnerListeners() {
        viewSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                retrieveGoals();
                initialiseList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });
    }


    /*Search database for goals using search criteria (view type and percentage of goal complete) */
    private void retrieveGoals() {
        Context context = getActivity();
        GoalContract.GoalDbHelper mDbHelper = new GoalContract.GoalDbHelper(context);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ArrayList<GoalsListDisplay> results = new ArrayList<GoalsListDisplay>();

        // Get view type selected
        Spinner historyViewSelector = (Spinner) view.findViewById(R.id.historyViewSelector);
        String historyView = historyViewSelector.getSelectedItem().toString();
        // Get minimum selected percentage
        int minPercent = (int) rangeSeekBar.getSelectedMinValue();
        // Get maximum selected percentage
        int maxPercent = (int) rangeSeekBar.getSelectedMaxValue();

//        System.out.println(historyView +  "   "  + minPercent +   "    "   + maxPercent);

        ArrayList<GoalsListDisplay> goals = mDbHelper.getAllGoals(db, GoalContract.Goal.TABLE_NAME);
        if (goals == null){
            return;
        }

        for (GoalsListDisplay goal : goals){
            if (goal.getPercentage() >= minPercent && goal.getPercentage() <= maxPercent && goal.getDate() > 0){
                if (historyView.equals("Show all")){
                    results.add(goal);
                }
                else if (historyView.equals("Week view")){
                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.DATE, -7);
                    if (DateUtils.isAfterDay(goal.getCalendarDate(), cal)){
                        results.add(goal);
                    }
                }
                else if (historyView.equals("Month view")){
                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.MONTH, -1);
                    if (DateUtils.isAfterDay(goal.getCalendarDate(), cal)){
                        results.add(goal);
                    }
                }
            }
        }
        historyResults = results;
    }

    /*Populate the select view Spinner and select units Spinner with options*/
    private void populateSpinnerOptions() {
        // Populate Select View Spinner
        viewSpinner = (Spinner) view.findViewById(R.id.historyViewSelector);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity().getBaseContext(),
                R.array.viewArray, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        viewSpinner.setAdapter(adapter);

        // Populate Select Unit Spinner
        unitsSpinner = (Spinner) view.findViewById(R.id.historyUnitSelector);
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(getActivity().getBaseContext(),
                R.array.units_array, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unitsSpinner.setAdapter(arrayAdapter);
    }

}
