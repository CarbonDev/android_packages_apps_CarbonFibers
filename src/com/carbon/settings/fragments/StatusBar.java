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
import android.util.Log;

import com.carbon.settings.R;
import com.carbon.settings.SettingsPreferenceFragment;
import com.carbon.settings.Utils;
import com.carbon.settings.util.Helpers;

public class StatusBar extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String TAG = "StatusBar";

    private static final String STATUS_BAR_BRIGHTNESS = "statusbar_brightness_slider";
    private static final String STATUS_BAR_SIGNAL = "status_bar_signal";
    private static final String STATUS_BAR_NOTIF_COUNT = "status_bar_notif_count";
    private static final String STATUS_BAR_CATEGORY_GENERAL = "status_bar_general";
    private static final String UI_COLLAPSE_BEHAVIOUR = "notification_drawer_collapse_on_dismiss";
    private static final CharSequence STATUS_BAR_BEHAVIOR = "status_bar_behavior";
    private static final String STATUS_BAR_QUICK_PEEK = "status_bar_quick_peek";

    private ListPreference mStatusBarCmSignal;
    private CheckBoxPreference mStatusBarNotifCount;
    private CheckBoxPreference mStatusbarSliderPreference;
    private PreferenceScreen mClockStyle;
    private PreferenceCategory mPrefCategoryGeneral;
    private ListPreference mCollapseOnDismiss;
    private ListPreference mStatusBarBeh;
    private CheckBoxPreference mStatusBarQuickPeek;

    private static int mBarBehavior;

    private static ContentResolver mContentResolver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.status_bar);

        ContentResolver resolver = getActivity().getContentResolver();
        PreferenceScreen prefSet = getPreferenceScreen();
        mContentResolver = getContentResolver();

        mStatusBarCmSignal = (ListPreference) prefSet.findPreference(STATUS_BAR_SIGNAL);

        int signalStyle = Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.STATUS_BAR_SIGNAL_TEXT, 0);
        mStatusBarCmSignal.setValue(String.valueOf(signalStyle));
        mStatusBarCmSignal.setSummary(mStatusBarCmSignal.getEntry());
        mStatusBarCmSignal.setOnPreferenceChangeListener(this);

        mStatusBarNotifCount = (CheckBoxPreference) prefSet.findPreference(STATUS_BAR_NOTIF_COUNT);
        mStatusBarNotifCount.setChecked((Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.STATUS_BAR_NOTIF_COUNT, 0) == 1));

        mStatusbarSliderPreference = (CheckBoxPreference) findPreference(STATUS_BAR_BRIGHTNESS);
        mStatusbarSliderPreference.setChecked((Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.STATUSBAR_BRIGHTNESS_SLIDER, 0) == 1));

        int collapseBehaviour = Settings.System.getInt(resolver,
                Settings.System.STATUS_BAR_COLLAPSE_ON_DISMISS,
                Settings.System.STATUS_BAR_COLLAPSE_IF_NO_CLEARABLE);
        mCollapseOnDismiss = (ListPreference) prefSet.findPreference(UI_COLLAPSE_BEHAVIOUR);
        mCollapseOnDismiss.setValue(String.valueOf(collapseBehaviour));
        mCollapseOnDismiss.setOnPreferenceChangeListener(this);
        updateCollapseBehaviourSummary(collapseBehaviour);

        mStatusBarBeh = (ListPreference) findPreference(STATUS_BAR_BEHAVIOR);
        int mBarBehavior = Settings.System.getInt(mContentResolver,
                Settings.System.HIDE_STATUSBAR, 0);
        mStatusBarBeh.setValue(Integer.toString(Settings.System.getInt(mContentResolver,
                Settings.System.HIDE_STATUSBAR, mBarBehavior)));
        updateStatusBarBehaviorSummary(mBarBehavior);
        mStatusBarBeh.setOnPreferenceChangeListener(this);

        mStatusBarQuickPeek = (CheckBoxPreference) prefSet.findPreference(STATUS_BAR_QUICK_PEEK);
        mStatusBarQuickPeek.setChecked((Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.STATUSBAR_PEEK, 0) == 1));

        mPrefCategoryGeneral = (PreferenceCategory) findPreference(STATUS_BAR_CATEGORY_GENERAL);

        if (Utils.isWifiOnly(getActivity())) {
            mPrefCategoryGeneral.removePreference(mStatusBarCmSignal);
        }

        mClockStyle = (PreferenceScreen) prefSet.findPreference("clock_style_pref");
        if (mClockStyle != null) {
            updateClockStyleDescription();
        }

    }

    private void updateCollapseBehaviourSummary(int setting) {
        String[] summaries = getResources().getStringArray(
                R.array.notification_drawer_collapse_on_dismiss_summaries);
        mCollapseOnDismiss.setSummary(summaries[setting]);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        boolean result = false;

        if (preference == mStatusBarCmSignal) {
            int signalStyle = Integer.valueOf((String) newValue);
            int index = mStatusBarCmSignal.findIndexOfValue((String) newValue);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.STATUS_BAR_SIGNAL_TEXT, signalStyle);
            mStatusBarCmSignal.setSummary(mStatusBarCmSignal.getEntries()[index]);
            return true;
        } else if (preference == mCollapseOnDismiss) {
            int value = Integer.valueOf((String) newValue);
            Settings.System.putInt(resolver,
                    Settings.System.STATUS_BAR_COLLAPSE_ON_DISMISS, value);
            updateCollapseBehaviourSummary(value);
            return true;
        } else if (preference == mStatusBarBeh) {
            int mBarBehavior = Integer.valueOf((String) newValue);
            int index = mStatusBarBeh.findIndexOfValue((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.HIDE_STATUSBAR, mBarBehavior);
            mStatusBarBeh.setSummary(mStatusBarBeh.getEntries()[index]);
            updateStatusBarBehaviorSummary(mBarBehavior);
            Helpers.restartSystemUI();
            return true;
        }

        return false;
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        boolean value;

        if (preference == mStatusBarNotifCount) {
            value = mStatusBarNotifCount.isChecked();
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.STATUS_BAR_NOTIF_COUNT, value ? 1 : 0);
            return true;
        } else if (preference == mStatusbarSliderPreference) {
            value = mStatusbarSliderPreference.isChecked();
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.STATUSBAR_BRIGHTNESS_SLIDER, value ? 1 : 0);
            return true;
        } else if (preference == mStatusBarQuickPeek) {
            value = mStatusBarQuickPeek.isChecked();
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.STATUSBAR_PEEK, value ? 1 : 0);
            return true;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    private void updateClockStyleDescription() {
        if (Settings.System.getInt(getActivity().getContentResolver(),
               Settings.System.STATUS_BAR_CLOCK, 1) == 1) {
            mClockStyle.setSummary(getString(R.string.clock_enabled));
        } else {
            mClockStyle.setSummary(getString(R.string.clock_disabled));
         }
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

    @Override
    public void onResume() {
        super.onResume();
        updateClockStyleDescription();
    }

}
