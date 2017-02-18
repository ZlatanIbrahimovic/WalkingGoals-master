package com.android_examples.slidingtab_android_examplescom;

/**
 * Created by liamdiamond on 31/01/2017.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.android_examples.slidingtab_android_examplescom.R.id.fromDate;
import static com.android_examples.slidingtab_android_examplescom.R.id.toDate;

public class Tab_2_Activity extends Fragment {

    private View view;
    private Spinner viewSpinner;
    private Spinner unitsSpinner;
    private RangeSeekBar rangeSeekBar;
    private ArrayList<GoalsListDisplay> historyResults;
    private long systemTime;
    private String selectedUnits;
    private ViewGroup container;
    private LayoutInflater inflater;
    private EditText dateFrom;
    private EditText dateTo;
    private Calendar fromCal;
    private Calendar toCal;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.container = container;
        this.inflater = inflater;

        final View view =  inflater.inflate(R.layout.activity_tab_2, container, false);
        this.view = view;

        populateSpinnerOptions();
        setupSpinnerListeners();
        initiateEditTextListeners();
        setSelectedUnits();
        setupSeekbar();
        retrieveGoals();
        initialiseList();

        return view;
    }

    /* Initiate listeners for the date editors, displays date picker when selected*/
    private void initiateEditTextListeners() {

        dateFrom = (EditText) view.findViewById(fromDate);
        dateFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FromDateListener fromDateListener = new FromDateListener(view, (EditText) view.findViewById(fromDate));
                fromCal = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        fromDateListener,
                        fromCal.get(Calendar.YEAR),
                        fromCal.get(Calendar.MONTH),
                        fromCal.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getActivity().getFragmentManager(), "Datepickerdialog");
            }
        });
        dateFrom.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                retrieveGoals();
                initialiseList();
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        dateTo = (EditText) view.findViewById(R.id.toDate);
        dateTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FromDateListener fromDateListener = new FromDateListener(view, (EditText) view.findViewById(toDate));
                toCal = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        fromDateListener,
                        toCal.get(Calendar.YEAR),
                        toCal.get(Calendar.MONTH),
                        toCal.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getActivity().getFragmentManager(), "Datepickerdialog");
            }
        });
        dateTo.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                retrieveGoals();
                initialiseList();
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

    }

    private void setSelectedUnits() {
        selectedUnits = unitsSpinner.getSelectedItem().toString();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            setSelectedUnits();
            retrieveGoals();
            initialiseList();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setSelectedUnits();
        retrieveGoals();
        initialiseList();
    }


    private void initialiseList() {
        ListView listView = (ListView) view.findViewById(R.id.historyList);
        ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();

        if(historyResults != null){
            for (int i = 0; i < historyResults.size(); i++) {
                Map<String, String> datum = new HashMap<String, String>(2);
                String goalUnits = historyResults.get(i).getUnits();
                double distance = Double.parseDouble(historyResults.get(i).getDistance());
                double convertedDistance = new DistanceConversion(distance, goalUnits, selectedUnits, view.getContext()).convert();
                double progress = historyResults.get(i).getProgress();
                double convertedProgress = new DistanceConversion(progress, goalUnits, selectedUnits, view.getContext()).convert();

                datum.put("title", historyResults.get(i).getTitle());
                datum.put("distance", convertedDistance + " " + selectedUnits);
                datum.put("progress", convertedProgress + " " + selectedUnits);
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
                initialiseList();
            }
        });
    }

    /*Set up the view and units spinners with listeners*/
    private void setupSpinnerListeners() {
        viewSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedView = viewSpinner.getSelectedItem().toString();
                if (selectedView.equals("Custom range")){
                    dateFrom.setFocusable(true);
                    dateFrom.setEnabled(true);
                    dateFrom.setCursorVisible(true);
                    dateTo.setFocusable(true);
                    dateTo.setEnabled(true);
                    dateTo.setCursorVisible(true);
                }
                else{
                    dateFrom.setFocusable(false);
                    dateFrom.setEnabled(false);
                    dateFrom.setCursorVisible(false);
                    dateTo.setFocusable(false);
                    dateTo.setEnabled(false);
                    dateTo.setCursorVisible(false);
//                    dateFrom.setBackgroundColor(Color.TRANSPARENT);

                }
                retrieveGoals();
                initialiseList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

        unitsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedUnits = unitsSpinner.getSelectedItem().toString();
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
    public void retrieveGoals() {
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

        setSystemTime();

        for (GoalsListDisplay goal : goals){
            if (goal.getPercentage() >= minPercent && goal.getPercentage() <= maxPercent && goal.getDate() > 0){
                if (historyView.equals("Show all")){
                    results.add(goal);
                }
                else if (historyView.equals("Week view")){
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(systemTime);
                    cal.add(Calendar.DATE, -7);
                    if (DateUtils.isAfterDay(goal.getCalendarDate(), cal)){
                        results.add(goal);
                    }
                }
                else if (historyView.equals("Month view")){
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(systemTime);
                    cal.add(Calendar.MONTH, -1);
                    if (DateUtils.isAfterDay(goal.getCalendarDate(), cal)){
                        results.add(goal);
                    }
                }
                else if (historyView.equals("Custom range")){
                    try {
                        System.out.println("HELLO");
                        String fromDate = dateFrom.getText().toString().trim();
                        Calendar calendarFrom = getCalendarFromDateString(fromDate);
                        String toDate = dateTo.getText().toString().trim();
                        Calendar calendarTo = getCalendarFromDateString(toDate);

                        System.out.println("Start " + calendarFrom.getTimeInMillis());
                        System.out.println("End " + calendarTo.getTimeInMillis());

                        if (DateUtils.isAfterDay(goal.getCalendarDate(), calendarFrom)
                                && DateUtils.isBeforeDay(goal.getCalendarDate(), calendarTo)){
                            results.add(goal);
                        }

                    }
                    catch(Exception e){
                        System.out.println(e);
                    }
                }
            }
        }
        historyResults = results;
    }

    public Calendar getCalendarFromDateString(String date){
        String[] dateSplit = date.split("-");
        int year = Integer.parseInt(dateSplit[2]);
        int month = Integer.parseInt(dateSplit[1]) - 1;
        int day = Integer.parseInt(dateSplit[0]);
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        return cal;
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


    private void setSystemTime() {
        systemTime = -2;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        if (prefs.getBoolean("testMode", false)){
            Calendar cal = Calendar.getInstance();
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date dates = formatter.parse(prefs.getString("keyname",null));
                cal.setTime(dates);
                cal.add(Calendar.MONTH, 1);
                systemTime = cal.getTimeInMillis();
            } catch (ParseException e) {e.printStackTrace();}

        }
        else{
            systemTime = System.currentTimeMillis();
        }
    }


}

class FromDateListener implements DatePickerDialog.OnDateSetListener {

    private View view;
    private EditText editText;

    public FromDateListener(View view, EditText editText){
        this.view = view;
        this.editText = editText;
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        dateFormat.format(cal.getTime());
        String displayDate = dateFormat.format(cal.getTime());
        editText.setText(displayDate);
    }
}