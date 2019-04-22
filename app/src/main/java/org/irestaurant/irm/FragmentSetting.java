package org.irestaurant.irm;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

public class FragmentSetting extends PreferenceFragment {
    CheckBoxPreference cbPrinter;


    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        cbPrinter       = (CheckBoxPreference) findPreference("cb_printer");
    }
}
