package com.android_examples.slidingtab_android_examplescom;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by liamdiamond on 14/03/2017.
 */

public class PrefsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }


}