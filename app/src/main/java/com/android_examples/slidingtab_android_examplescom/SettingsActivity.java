package com.android_examples.slidingtab_android_examplescom;

import android.annotation.TargetApi;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

public class SettingsActivity extends PreferenceActivity implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    private static int prefs=R.xml.preferences;

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        try {
            getClass().getMethod("getFragmentManager");
            AddResourceApi11AndGreater();

        } catch (NoSuchMethodException e) { //Api < 11
            AddResourceApiLessThan11();
        }
        setupTestModeDate();
    }

    private void setupTestModeDate() {
    }

    @SuppressWarnings("deprecation")
    protected void AddResourceApiLessThan11()
    {
        addPreferencesFromResource(prefs);
    }

    @TargetApi(11)
    protected void AddResourceApi11AndGreater()
    {
        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new PF()).commit();
    }




    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int i, int i1, int i2) {

    }

    @Override
    public void onTimeSet(TimePickerDialog timePickerDialog, int i, int i1, int i2) {

    }


    @TargetApi(11)
    public static class PF extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(SettingsActivity.prefs); //outer class
            // private members seem to be visible for inner class, and
            // making it static made things so much easier
        }
    }

}