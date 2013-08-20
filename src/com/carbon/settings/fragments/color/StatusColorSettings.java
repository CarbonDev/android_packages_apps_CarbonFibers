/*
 * Copyright (C) 2013 ParanoidAndroid Project
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

package com.carbon.settings.fragments.color;

import android.app.Activity;
import android.content.Context;
import android.content.ContentResolver;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

import com.carbon.settings.R;
import com.carbon.settings.SettingsPreferenceFragment;
import com.carbon.settings.Utils;
import com.carbon.settings.util.Helpers;
import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class StatusColorSettings extends SettingsPreferenceFragment
        implements Preference.OnPreferenceChangeListener {

    private static final String PREF_ICON_COLOR_BEHAVIOR = "icon_color_behavior";
    private static final String PREF_STATUS_ICON_COLOR = "status_icon_color";

    private CheckBoxPreference mIconBehavior;
    private ColorPickerPreference mIconColor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.prefs_status_color);

        PreferenceScreen prefSet = getPreferenceScreen();

        mIconBehavior = (CheckBoxPreference) prefSet.findPreference(PREF_ICON_COLOR_BEHAVIOR);
        mIconBehavior.setChecked(Settings.System.getInt(mContentAppRes,
                Settings.System.ICON_COLOR_BEHAVIOR, 0) == 1);

        mIconColor = (ColorPickerPreference) findPreference(PREF_STATUS_ICON_COLOR);
        mIconColor.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mIconBehavior) {
            Settings.System.putInt(mContentAppRes,
                    Settings.System.ICON_COLOR_BEHAVIOR,
            mIconBehavior.isChecked() ? 1 : 0);
            Helpers.restartSystemUI();
        }   
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    public boolean onPreferenceChange(Preference preference, Object Value) {
        if (preference == mIconColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(Value)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mContentAppRes,
                    Settings.System.STATUS_ICON_COLOR, intHex);
            Helpers.restartSystemUI();
            return true;
        }
        return false;
    }
}
