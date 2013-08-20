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

public class HaloColorSettings extends SettingsPreferenceFragment
        implements Preference.OnPreferenceChangeListener {

    private static final String KEY_HALO_COLORS = "halo_colors";
    private static final String KEY_HALO_CIRCLE_COLOR = "halo_circle_color";
    private static final String KEY_HALO_EFFECT_COLOR = "halo_effect_color";
    private static final String KEY_HALO_BUBBLE_COLOR = "halo_bubble_color";
    private static final String KEY_HALO_BUBBLE_TEXT_COLOR = "halo_bubble_text_color";

    private CheckBoxPreference mHaloColors;
    private ColorPickerPreference mHaloCircleColor;
    private ColorPickerPreference mHaloEffectColor;
    private ColorPickerPreference mHaloBubbleColor;
    private ColorPickerPreference mHaloBubbleTextColor;

    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.prefs_halo_color);

        mHaloColors = (CheckBoxPreference) findPreference(KEY_HALO_COLORS);
        mHaloColors.setChecked(Settings.System.getInt(mContentAppRes,
                Settings.System.HALO_COLORS, 0) == 1);

        mHaloEffectColor = (ColorPickerPreference) findPreference(KEY_HALO_EFFECT_COLOR);
        mHaloEffectColor.setOnPreferenceChangeListener(this);

        mHaloCircleColor = (ColorPickerPreference) findPreference(KEY_HALO_CIRCLE_COLOR);
        mHaloCircleColor.setOnPreferenceChangeListener(this);

        mHaloBubbleColor = (ColorPickerPreference) findPreference(KEY_HALO_BUBBLE_COLOR);
        mHaloBubbleColor.setOnPreferenceChangeListener(this);

        mHaloBubbleTextColor = (ColorPickerPreference) findPreference(KEY_HALO_BUBBLE_TEXT_COLOR);
        mHaloBubbleTextColor.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mHaloColors) {
            Settings.System.putInt(mContentAppRes,
                    Settings.System.HALO_COLORS, mHaloColors.isChecked()
                    ? 1 : 0);
            Helpers.restartSystemUI();
        }   
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    public boolean onPreferenceChange(Preference preference, Object Value) {
        if (preference == mHaloCircleColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(Value)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mContentAppRes,
                    Settings.System.HALO_CIRCLE_COLOR, intHex);
            return true;
        } else if (preference == mHaloEffectColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(Value)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mContentAppRes,
                    Settings.System.HALO_EFFECT_COLOR, intHex);
            Helpers.restartSystemUI();
            return true;
        } else if (preference == mHaloBubbleColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(Value)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mContentAppRes,
                    Settings.System.HALO_BUBBLE_COLOR, intHex);
            return true;
        } else if (preference == mHaloBubbleTextColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(Value)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mContentAppRes,
                    Settings.System.HALO_BUBBLE_TEXT_COLOR, intHex);
            return true;
        }
        return false;
    }
}
