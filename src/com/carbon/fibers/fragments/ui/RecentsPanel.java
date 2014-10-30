/*
 * Copyright (C) 2012 The CarbonRom project
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

import com.carbon.fibers.R;
import com.carbon.fibers.preference.SettingsPreferenceFragment;

public class RecentsPanel extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {
    private static final String TAG = "RecentsPanel";

    private static final String CLEAR_RECENTS_BUTTON = "clear_recents_button";
    private static final String RAM_CIRCLE = "ram_circle";
    private static final String RECENTS_SWIPE_FLOATING = "recents_swipe_floating";

    private ListPreference mClearAllButton;
    private ListPreference mRamCircle;
    private ListPreference mRecentsSwipeFloating;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.ui_recents_panel);
        PreferenceScreen prefScreen = getPreferenceScreen();

        // clear recents position
        mClearAllButton = (ListPreference) findPreference(CLEAR_RECENTS_BUTTON);
        int clearStatus = Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.CLEAR_RECENTS_BUTTON, 4);
        mClearAllButton.setValue(String.valueOf(clearStatus));
        mClearAllButton.setSummary(mClearAllButton.getEntry());
        mClearAllButton.setOnPreferenceChangeListener(this);

        mRamCircle = (ListPreference) findPreference(RAM_CIRCLE);
        int circleStatus = Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.RAM_CIRCLE, 0);
        mRamCircle.setValue(String.valueOf(circleStatus));
        mRamCircle.setSummary(mRamCircle.getEntry());
        mRamCircle.setOnPreferenceChangeListener(this);

        mRecentsSwipeFloating = (ListPreference) findPreference(RECENTS_SWIPE_FLOATING);
        int RecentsSwipeFloatingStatus = Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.RECENTS_SWIPE_FLOATING, 0);
        mRecentsSwipeFloating.setValue(String.valueOf(RecentsSwipeFloatingStatus));
        mRecentsSwipeFloating.setSummary(mRecentsSwipeFloating.getEntry());
        mRecentsSwipeFloating.setOnPreferenceChangeListener(this);
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        if (preference == mClearAllButton) {
            int value = Integer.valueOf((String) objValue);
            int index = mClearAllButton.findIndexOfValue((String) objValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.CLEAR_RECENTS_BUTTON, value);
            mClearAllButton.setSummary(mClearAllButton.getEntries()[index]);
            return true;
        } else if (preference == mRamCircle) {
            int value = Integer.valueOf((String) objValue);
            int index = mRamCircle.findIndexOfValue((String) objValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.RAM_CIRCLE, value);
            mRamCircle.setSummary(mRamCircle.getEntries()[index]);
            return true;
        } else if (preference == mRecentsSwipeFloating) {
            int value = Integer.valueOf((String) objValue);
            int index = mRecentsSwipeFloating.findIndexOfValue((String) objValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.RECENTS_SWIPE_FLOATING, value);
            mRecentsSwipeFloating.setSummary(mRecentsSwipeFloating.getEntries()[index]);
            return true;
        }
        return false;
    }
}
