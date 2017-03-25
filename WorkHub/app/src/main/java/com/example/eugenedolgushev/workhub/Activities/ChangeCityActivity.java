package com.example.eugenedolgushev.workhub.Activities;

import android.content.Context;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;

import com.example.eugenedolgushev.workhub.AsyncTasks.GetCities;
import com.example.eugenedolgushev.workhub.R;

import static com.example.eugenedolgushev.workhub.Utils.getStringFromSharedPreferences;
import static com.example.eugenedolgushev.workhub.Utils.hasConnection;
import static com.example.eugenedolgushev.workhub.Utils.setStringToSharedPreferences;
import static com.example.eugenedolgushev.workhub.Utils.showAlertDialog;

public class ChangeCityActivity extends PreferenceActivity {

    private ListPreference listPreference;
    private Context m_context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref);
        setTitle("Изменить город");

        m_context = this;

        listPreference = (ListPreference) findPreference("cities");
        listPreference.setValue(getStringFromSharedPreferences("cities", getApplicationContext()));

        GetCities getCities = new GetCities(new GetCities.AsyncResponse() {
            @Override
            public void processFinish(CharSequence[] cities) {
                listPreference.setEntries(cities);
                listPreference.setEntryValues(cities);
            }
        }, m_context);

        if (hasConnection(m_context)) {
            getCities.execute();
        } else {
            showAlertDialog("Нет подключения к интернету", m_context);
        }

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
