package com.android_examples.slidingtab_android_examplescom;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

public class CustomPreference extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        final DatePreference dp= (DatePreference) findPreference("keyname");
        dp.setText("2014-08-02");
        dp.setSummary("2014-08-02");
        dp.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference,Object newValue) {
                //your code to change values.
                dp.setSummary((String) newValue);
                return true;
            }
        });

    }


}
