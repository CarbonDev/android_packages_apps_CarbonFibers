/*
 * Copyright (C) 2014 The CarbonRom project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.carbon.fibers.fragments.ui;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.provider.Settings;

import android.text.TextUtils;
import com.carbon.fibers.chameleonos.AppMultiSelectListPreference;
import com.android.internal.util.omni.DeviceUtils;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.carbon.fibers.R;
import com.carbon.fibers.chameleonos.SeekBarPreference;
import com.carbon.fibers.preference.SettingsPreferenceFragment;

public class AppCircleSideBar extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {
    private static final String TAG = "AppCircleSideBar";
    private static final String KEY_TRIGGER_WIDTH = "trigger_width";
    private static final String KEY_TRIGGER_TOP = "trigger_top";
    private static final String KEY_TRIGGER_BOTTOM = "trigger_bottom";
    private static final String PREF_INCLUDE_APP_CIRCLE_BAR_KEY = "app_circle_bar_included_apps";
    private AppMultiSelectListPreference mIncludedAppCircleBar;
    private SeekBarPreference mTriggerWidthPref;
    private SeekBarPreference mTriggerTopPref;
    private SeekBarPreference mTriggerBottomPref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.ui_app_circle);
        PreferenceScreen prefScreen = getPreferenceScreen();

        mIncludedAppCircleBar = (AppMultiSelectListPreference) prefScreen.findPreference(PREF_INCLUDE_APP_CIRCLE_BAR_KEY);
        Set<String> includedApps = getIncludedApps();
        if (includedApps != null) mIncludedAppCircleBar.setValues(includedApps);
        mIncludedAppCircleBar.setOnPreferenceChangeListener(this);
        mTriggerWidthPref = (SeekBarPreference) findPreference(KEY_TRIGGER_WIDTH);
        mTriggerWidthPref.setValue(Settings.System.getInt(getContentResolver(),
                Settings.System.APP_CIRCLE_BAR_TRIGGER_WIDTH, 10));
        mTriggerWidthPref.setOnPreferenceChangeListener(this);
        mTriggerTopPref = (SeekBarPreference) findPreference(KEY_TRIGGER_TOP);
        mTriggerTopPref.setValue(Settings.System.getInt(getContentResolver(),
                Settings.System.APP_CIRCLE_BAR_TRIGGER_TOP, 0));
        mTriggerTopPref.setOnPreferenceChangeListener(this);
        mTriggerBottomPref = (SeekBarPreference) findPreference(KEY_TRIGGER_BOTTOM);
        mTriggerBottomPref.setValue(Settings.System.getInt(getContentResolver(),
                Settings.System.APP_CIRCLE_BAR_TRIGGER_HEIGHT, 100));
        mTriggerBottomPref.setOnPreferenceChangeListener(this);
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        if (preference == mIncludedAppCircleBar) {
            storeIncludedApps((Set<String>) objValue);
            return true;
        } else if (preference == mTriggerWidthPref) {
            int width = ((Integer)objValue).intValue();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.APP_CIRCLE_BAR_TRIGGER_WIDTH, width);
            return true;
        } else if (preference == mTriggerTopPref) {
            int top = ((Integer)objValue).intValue();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.APP_CIRCLE_BAR_TRIGGER_TOP, top);
            return true;
        } else if (preference == mTriggerBottomPref) {
            int bottom = ((Integer)objValue).intValue();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.APP_CIRCLE_BAR_TRIGGER_HEIGHT, bottom);
            return true;
        }
        return false;
    }
    private Set<String> getIncludedApps() {
        String included = Settings.System.getString(getActivity().getContentResolver(),
                Settings.System.WHITELIST_APP_CIRCLE_BAR);
        if (TextUtils.isEmpty(included)) {
            return null;
        }
        return new HashSet<String>(Arrays.asList(included.split("\\|")));
    }

    private void storeIncludedApps(Set<String> values) {
        StringBuilder builder = new StringBuilder();
        String delimiter = "";
        for (String value : values) {
            builder.append(delimiter);
            builder.append(value);
            delimiter = "|";
        }
        Settings.System.putString(getActivity().getContentResolver(),
                Settings.System.WHITELIST_APP_CIRCLE_BAR, builder.toString());
    }

    @Override
    public void onPause() {
        super.onPause();
        Settings.System.putInt(getContentResolver(),
                Settings.System.APP_CIRCLE_BAR_SHOW_TRIGGER, 0);
    }

    @Override
    public void onResume() {
        super.onResume();
        Settings.System.putInt(getContentResolver(),
                Settings.System.APP_CIRCLE_BAR_SHOW_TRIGGER, 1);
    }
}
