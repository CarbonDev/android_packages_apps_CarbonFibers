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

package com.carbon.settings.fragments.statusbar;

import android.content.ContentResolver;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.PhoneConstants;

import com.carbon.settings.R;
import com.carbon.settings.SettingsPreferenceFragment;
import com.carbon.settings.Utils;
import com.carbon.settings.util.Helpers;
import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class StatusBarGeneral extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String TAG = "StatusBar";

    private static final String STATUS_BAR_BRIGHTNESS = "statusbar_brightness_slider";
    private static final String STATUS_BAR_SIGNAL = "status_bar_signal";
    private static final String STATUS_BAR_CATEGORY_STYLE = "status_bar_style";
    private static final CharSequence STATUS_BAR_BEHAVIOR = "status_bar_behavior";
    private static final String STATUS_BAR_QUICK_PEEK = "status_bar_quick_peek";
    private static final String STATUS_ICON_COLOR_BEHAVIOR = "status_icon_color_behavior";
    private static final String STATUS_ICON_COLOR = "status_icon_color";
    private static final String STATUS_BAR_NETWORK_STATS = "status_bar_show_network_stats";
    private static final String STATUS_BAR_NETWORK_STATS_UPDATE = "status_bar_network_status_update";
    private static final String KEY_SHOW_LTE_OR_FOURGEE = "show_lte_or_fourgee";

    private ListPreference mStatusBarCmSignal;
    private CheckBoxPreference mStatusbarSliderPreference;
    private CheckBoxPreference mStatusIconBehavior;
    private ColorPickerPreference mIconColor;
    private PreferenceCategory mPrefCategoryStyle;
    private ListPreference mStatusBarBeh;
    private CheckBoxPreference mStatusBarQuickPeek;
    private ListPreference mStatusBarNetStatsUpdate;
    private CheckBoxPreference mStatusBarNetworkStats;
    private CheckBoxPreference mShowLTEorFourGee;

    private static int mBarBehavior;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.status_bar_general);

        PreferenceScreen prefSet = getPreferenceScreen();

        mStatusBarCmSignal = (ListPreference) prefSet.findPreference(STATUS_BAR_SIGNAL);

        int signalStyle = Settings.System.getInt(mContentAppRes,
                Settings.System.STATUS_BAR_SIGNAL_TEXT, 0);
        mStatusBarCmSignal.setValue(String.valueOf(signalStyle));
        mStatusBarCmSignal.setSummary(mStatusBarCmSignal.getEntry());
        mStatusBarCmSignal.setOnPreferenceChangeListener(this);

        mStatusbarSliderPreference = (CheckBoxPreference) findPreference(STATUS_BAR_BRIGHTNESS);
        mStatusbarSliderPreference.setChecked((Settings.System.getInt(mContentAppRes,
                Settings.System.STATUSBAR_BRIGHTNESS_SLIDER, 0) == 1));

        mStatusBarNetworkStats = (CheckBoxPreference) findPreference(STATUS_BAR_NETWORK_STATS);
        mStatusBarNetworkStats.setChecked(Settings.System.getInt(mContentAppRes,
                Settings.System.STATUS_BAR_NETWORK_STATS, 0) == 1);

        mStatusBarBeh = (ListPreference) findPreference(STATUS_BAR_BEHAVIOR);
        int mBarBehavior = Settings.System.getInt(mContentRes,
                Settings.System.HIDE_STATUSBAR, 0);
        mStatusBarBeh.setValue(Integer.toString(Settings.System.getInt(mContentRes,
                Settings.System.HIDE_STATUSBAR, mBarBehavior)));
        updateStatusBarBehaviorSummary(mBarBehavior);
        mStatusBarBeh.setOnPreferenceChangeListener(this);

        mStatusBarQuickPeek = (CheckBoxPreference) prefSet.findPreference(STATUS_BAR_QUICK_PEEK);
        mStatusBarQuickPeek.setChecked((Settings.System.getInt(mContentAppRes,
                Settings.System.STATUSBAR_PEEK, 0) == 1));

        mStatusIconBehavior = (CheckBoxPreference) prefSet.findPreference(STATUS_ICON_COLOR_BEHAVIOR);
        mStatusIconBehavior.setChecked(Settings.System.getInt(mContentAppRes,
                Settings.System.ICON_COLOR_BEHAVIOR, 0) == 1);

        mIconColor = (ColorPickerPreference) findPreference(STATUS_ICON_COLOR);
        mIconColor.setOnPreferenceChangeListener(this);

        mPrefCategoryStyle = (PreferenceCategory) findPreference(STATUS_BAR_CATEGORY_STYLE);

        if (Utils.isWifiOnly(getActivity())) {
            mPrefCategoryStyle.removePreference(mStatusBarCmSignal);
        }

        mShowLTEorFourGee = (CheckBoxPreference) findPreference(KEY_SHOW_LTE_OR_FOURGEE);
        mShowLTEorFourGee.setChecked(Settings.System.getBoolean(getActivity().
                getApplicationContext().getContentResolver(),
                    Settings.System.SHOW_LTE_OR_FOURGEE, false));
        if (!deviceSupportsLTE()) {
            getPreferenceScreen().removePreference(mShowLTEorFourGee);
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        boolean result = false;

        if (preference == mStatusBarCmSignal) {
            int signalStyle = Integer.valueOf((String) newValue);
            int index = mStatusBarCmSignal.findIndexOfValue((String) newValue);
            Settings.System.putInt(mContentAppRes,
                    Settings.System.STATUS_BAR_SIGNAL_TEXT, signalStyle);
            mStatusBarCmSignal.setSummary(mStatusBarCmSignal.getEntries()[index]);
            return true;
        } else if (preference == mStatusBarBeh) {
            int mBarBehavior = Integer.valueOf((String) newValue);
            int index = mStatusBarBeh.findIndexOfValue((String) newValue);
            Settings.System.putInt(mContentRes,
                    Settings.System.HIDE_STATUSBAR, mBarBehavior);
            mStatusBarBeh.setSummary(mStatusBarBeh.getEntries()[index]);
            updateStatusBarBehaviorSummary(mBarBehavior);
            Helpers.restartSystemUI();
            return true;
        } else if (preference == mIconColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mContentAppRes,
                    Settings.System.STATUS_ICON_COLOR, intHex);
            Helpers.restartSystemUI();
            return true;
        }
        return false;
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        boolean value;

        if (preference == mStatusIconBehavior) {
            Settings.System.putInt(mContentAppRes,
                    Settings.System.ICON_COLOR_BEHAVIOR,
            mStatusIconBehavior.isChecked() ? 1 : 0);
            Helpers.restartSystemUI();
        } else if (preference == mStatusbarSliderPreference) {
            value = mStatusbarSliderPreference.isChecked();
            Settings.System.putInt(mContentAppRes,
                    Settings.System.STATUSBAR_BRIGHTNESS_SLIDER, value ? 1 : 0);
            return true;
        } else if (preference == mStatusBarQuickPeek) {
            value = mStatusBarQuickPeek.isChecked();
            Settings.System.putInt(mContentAppRes,
                    Settings.System.STATUSBAR_PEEK, value ? 1 : 0);
            return true;
        } else if (preference == mStatusBarNetworkStats) {
            Settings.System.putInt(mContentRes,
                    Settings.System.STATUS_BAR_NETWORK_STATS,
                    ((CheckBoxPreference)preference).isChecked() ? 1 : 0);
            return true;
        } else if (preference == mShowLTEorFourGee) {
            Settings.System.putBoolean(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.SHOW_LTE_OR_FOURGEE,
                    mShowLTEorFourGee.isChecked());
            Helpers.restartSystemUI();
            return true;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }


    private void updateStatusBarBehaviorSummary(int value) {
        switch (value) {
            case 0:
                mStatusBarBeh.setSummary(getResources().getString(R.string.statusbar_show_summary));
                break;
            case 1:
                mStatusBarBeh.setSummary(getResources().getString(R.string.statusbar_hide_summary));
                break;
            case 2:
                mStatusBarBeh.setSummary(getResources().getString(R.string.statusbar_auto_rem_summary));
                break;
            case 3:
                mStatusBarBeh.setSummary(getResources().getString(R.string.statusbar_auto_all_summary));
                break;
        }
    }

    private boolean deviceSupportsLTE() {
        return (TelephonyManager.getLteOnCdmaModeStatic() == PhoneConstants.LTE_ON_CDMA_TRUE
                    || TelephonyManager.getLteOnGsmModeStatic() != 0);
    }
}
