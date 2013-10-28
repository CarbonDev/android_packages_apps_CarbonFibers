/*
 * Copyright (C) 2012 The CyanogenMod Project
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

package com.carbon.settings.fragments;

import android.content.res.Resources;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.preference.SwitchPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

import com.carbon.settings.R;
import com.carbon.settings.SettingsPreferenceFragment;
import com.android.internal.util.cm.QSUtils;

public class PowerMenu extends SettingsPreferenceFragment implements
                   OnPreferenceChangeListener {
    private static final String TAG = "PowerMenu";

    private static final String KEY_REBOOT = "power_menu_reboot";
    private static final String KEY_SCREENSHOT = "power_menu_screenshot";
    private static final String KEY_TORCH = "power_menu_torch";
    private static final String KEY_EXPANDED_DESKTOP = "power_menu_expanded_desktop";
    private static final String KEY_PROFILES = "power_menu_profiles";
    private static final String KEY_AIRPLANE = "power_menu_airplane";
    private static final String KEY_SYSTEMBAR = "power_menu_systembar";
    private static final String KEY_USER = "power_menu_user";
    private static final String KEY_SOUND = "power_menu_sound";

    private SwitchPreference mRebootPref;
    private SwitchPreference mScreenshotPref;
    private SwitchPreference mTorchPref;
    private ListPreference mExpandedDesktopPref;
    private SwitchPreference mProfilesPref;
    private SwitchPreference mAirplanePref;
    private SwitchPreference mSystembarPref;
    private SwitchPreference mUserPref;
    private SwitchPreference mSoundPref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.power_menu_settings);

        PreferenceScreen prefSet = getPreferenceScreen();

        mRebootPref = (SwitchPreference) findPreference(KEY_REBOOT);
        mRebootPref.setChecked(Settings.System.getBoolean(mContentRes,
                Settings.System.POWER_MENU_REBOOT_ENABLED, true));
        mRebootPref.setOnPreferenceChangeListener(this);

        mScreenshotPref = (SwitchPreference) findPreference(KEY_SCREENSHOT);
        mScreenshotPref.setChecked(Settings.System.getBoolean(mContentRes,
                Settings.System.POWER_MENU_SCREENSHOT_ENABLED, false));
        mScreenshotPref.setOnPreferenceChangeListener(this);

        mTorchPref = (SwitchPreference) findPreference(KEY_TORCH);
        mTorchPref.setChecked(Settings.System.getBoolean(mContentRes,
                Settings.System.POWER_MENU_TORCH_ENABLED, false));
        mTorchPref.setOnPreferenceChangeListener(this);

        mExpandedDesktopPref = (ListPreference) prefSet.findPreference(KEY_EXPANDED_DESKTOP);
        mExpandedDesktopPref.setOnPreferenceChangeListener(this);
        int expandedDesktopValue = Settings.System.getInt(getContentResolver(),
                        Settings.System.EXPANDED_DESKTOP_MODE, 0);
        mExpandedDesktopPref.setValue(String.valueOf(expandedDesktopValue));
        mExpandedDesktopPref.setSummary(mExpandedDesktopPref.getEntries()[expandedDesktopValue]);

        mProfilesPref = (SwitchPreference) findPreference(KEY_PROFILES);
        mProfilesPref.setChecked(Settings.System.getBoolean(mContentRes,
                Settings.System.POWER_MENU_PROFILES_ENABLED, false));
        mProfilesPref.setOnPreferenceChangeListener(this);

        // Only enable if System Profiles are also enabled
        boolean enabled = Settings.System.getInt(getContentResolver(),
                Settings.System.SYSTEM_PROFILES_ENABLED, 1) == 1;
        mProfilesPref.setEnabled(enabled);

        mAirplanePref = (SwitchPreference) findPreference(KEY_AIRPLANE);
        mAirplanePref.setChecked(Settings.System.getBoolean(mContentRes,
                Settings.System.POWER_MENU_AIRPLANE_ENABLED, true));
        mAirplanePref.setOnPreferenceChangeListener(this);

        mSystembarPref = (SwitchPreference) findPreference(KEY_SYSTEMBAR);
        mSystembarPref.setChecked(Settings.System.getBoolean(mContentRes,
                Settings.System.POWER_DIALOG_SHOW_NAVBAR_HIDE, false));
        mSystembarPref.setOnPreferenceChangeListener(this);

        mUserPref = (SwitchPreference) findPreference(KEY_USER);
        if (!UserHandle.MU_ENABLED
            || !UserManager.supportsMultipleUsers()) {
            getPreferenceScreen().removePreference(mUserPref);
        } else {
            mUserPref.setChecked(Settings.System.getBoolean(mContentRes,
                    Settings.System.POWER_MENU_USER_ENABLED, false));
        }
        mUserPref.setOnPreferenceChangeListener(this);

        mSoundPref = (SwitchPreference) findPreference(KEY_SOUND);
        mSoundPref.setChecked(Settings.System.getBoolean(mContentRes,
                Settings.System.POWER_MENU_SOUND_ENABLED, true));
        mSoundPref.setOnPreferenceChangeListener(this);

        if (!QSUtils.deviceSupportsTorch(getActivity())) {
            getPreferenceScreen().removePreference(mTorchPref);
        }
    }

    public boolean onPreferenceChange(Preference preference, Object value) {
        if (preference == mExpandedDesktopPref) {
            int expandedDesktopValue = Integer.valueOf((String) value);
            int index = mExpandedDesktopPref.findIndexOfValue((String) value);
            if (expandedDesktopValue == 0) {
                Settings.System.putInt(mContentRes,
                        Settings.System.POWER_MENU_EXPANDED_DESKTOP_ENABLED, 0);
                // Disable expanded desktop if enabled
                Settings.System.putInt(mContentRes,
                        Settings.System.EXPANDED_DESKTOP_STATE, 0);
            } else {
                Settings.System.putInt(mContentRes,
                        Settings.System.POWER_MENU_EXPANDED_DESKTOP_ENABLED, 1);
            }
            Settings.System.putInt(mContentRes,
                    Settings.System.EXPANDED_DESKTOP_MODE, expandedDesktopValue);
            mExpandedDesktopPref.setSummary(mExpandedDesktopPref.getEntries()[index]);
            return true;
        } else if (preference == mScreenshotPref) {
            Settings.System.putBoolean(mContentRes,
                    Settings.System.POWER_MENU_SCREENSHOT_ENABLED,
                    (Boolean) value);
            return true;
        } else if (preference == mTorchPref) {
            Settings.System.putBoolean(mContentRes,
                    Settings.System.POWER_MENU_TORCH_ENABLED,
                    (Boolean) value);
            return true;
        } else if (preference == mRebootPref) {
            Settings.System.putBoolean(mContentRes,
                    Settings.System.POWER_MENU_REBOOT_ENABLED,
                    (Boolean) value);
            return true;
        } else if (preference == mProfilesPref) {
            Settings.System.putBoolean(mContentRes,
                    Settings.System.POWER_MENU_PROFILES_ENABLED,
                    (Boolean) value);
            return true;
        } else if (preference == mAirplanePref) {
            Settings.System.putBoolean(mContentRes,
                    Settings.System.POWER_MENU_AIRPLANE_ENABLED,
                    (Boolean) value);
            return true;
        } else if (preference == mSystembarPref) {
            Settings.System.putBoolean(mContentRes,
                    Settings.System.POWER_DIALOG_SHOW_NAVBAR_HIDE,
                    (Boolean) value);
            return true;
        } else if (preference == mUserPref) {
            Settings.System.putBoolean(mContentRes,
                    Settings.System.POWER_MENU_USER_ENABLED,
                    (Boolean) value);
            return true;
        } else if (preference == mSoundPref) {
            Settings.System.putBoolean(mContentRes,
                    Settings.System.POWER_MENU_SOUND_ENABLED,
                    (Boolean) value);
            return true;
        }

        return false;
    }

}
