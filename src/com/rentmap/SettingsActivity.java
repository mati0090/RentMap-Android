package com.rentmap;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.rentmap.R;


public class SettingsActivity extends PreferenceActivity {
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.main_preferences);
    }
}
