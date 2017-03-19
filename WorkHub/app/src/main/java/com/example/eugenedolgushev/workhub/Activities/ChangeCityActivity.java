package com.example.eugenedolgushev.workhub.Activities;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;

import com.example.eugenedolgushev.workhub.R;

import static com.example.eugenedolgushev.workhub.Utils.getStringFromSharedPreferences;
import static com.example.eugenedolgushev.workhub.Utils.setStringToSharedPreferences;

public class ChangeCityActivity extends PreferenceActivity {

    ListPreference listPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref);

        listPreference = (ListPreference) findPreference("cities");
        listPreference.setValue(getStringFromSharedPreferences("cities", getApplicationContext()));

        setTitle("Изменить город");

        listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                setStringToSharedPreferences("cities", (String) newValue, getApplicationContext());
                setResult(1);
                finish();
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        setResult(0);
    }
}
