package de.nsvb.android.auto.widget;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import androidx.annotation.Nullable;

/**
 * Created by ns130291 on 12.02.2018.
 */

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getPreferenceManager().setSharedPreferencesName(ConfigurationActivity.PREFS_NAME);

        addPreferencesFromResource(R.xml.pref_general);
    }
}
