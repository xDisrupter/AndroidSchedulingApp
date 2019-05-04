package com.group5.atoms;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragmentCompat {

    ListPreference datePreference;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        //set the preferences from the resource
        setPreferencesFromResource(R.xml.preferences, rootKey);

        //get a reference to the list preference
        datePreference = (ListPreference) findPreference("date_list_preference");

        //declare what our entries are
        CharSequence[] entries = {"Classic ", "European"};
        CharSequence[] entryValues = {"MM/dd/yyyy", "dd/MM/yyyy"};

        //set the entries and entry values
        datePreference.setEntries(entries);
        datePreference.setEntryValues(entryValues);
    }




}
