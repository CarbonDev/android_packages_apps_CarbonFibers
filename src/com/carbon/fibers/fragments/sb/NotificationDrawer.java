/*
 * Copyright (C) 2012 The CyanogenMod project
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

package com.carbon.fibers.fragments.sb;

import android.content.res.Resources;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.provider.Settings;

import com.carbon.fibers.R;
import com.carbon.fibers.preference.SettingsPreferenceFragment;

public class NotificationDrawer extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {
    private static final String TAG = "NotificationDrawer";
    private static final String UI_COLLAPSE_BEHAVIOUR = "notification_drawer_collapse_on_dismiss";
    private static final String PREF_NOTIFICATION_HIDE_LABELS = "notification_hide_labels";

    private static final String STATUS_BAR_NOTIFICATION_SWIPE_FLOATING = "status_bar_notification_swipe_floating";

    private ListPreference mCollapseOnDismiss;
    private ListPreference mStatusBarNotificationSwipeFloating;

    ListPreference mHideLabels;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.sb_notification_drawer);
        PreferenceScreen prefScreen = getPreferenceScreen();
        mHideLabels = (ListPreference) findPreference(PREF_NOTIFICATION_HIDE_LABELS);
        int hideCarrier = Settings.System.getInt(getContentResolver(),
                Settings.System.NOTIFICATION_HIDE_LABELS, 0);
        mHideLabels.setValue(String.valueOf(hideCarrier));
        mHideLabels.setOnPreferenceChangeListener(this);
        updateHideNotificationLabelsSummary(hideCarrier);

        mStatusBarNotificationSwipeFloating = (ListPreference) findPreference(STATUS_BAR_NOTIFICATION_SWIPE_FLOATING);
        int StatusBarNotificationSwipeFloatingStatus = Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.STATUS_BAR_NOTIFICATION_SWIPE_FLOATING, 0);
        mStatusBarNotificationSwipeFloating.setValue(String.valueOf(StatusBarNotificationSwipeFloatingStatus));
        mStatusBarNotificationSwipeFloating.setSummary(mStatusBarNotificationSwipeFloating.getEntry());
        mStatusBarNotificationSwipeFloating.setOnPreferenceChangeListener(this);

        /* Tablet case in handled in PhoneStatusBar
          if (!DeviceUtils.isPhone(getActivity())
              || !DeviceUtils.deviceSupportsMobileData(getActivity())) {
              // Nothing for tablets, large screen devices and non mobile devices which doesn't show
              // information in notification drawer.....remove options
              prefs.removePreference(mHideCarrier);
         }*/

        // Notification drawer
        int collapseBehaviour = Settings.System.getInt(getContentResolver(),
                Settings.System.STATUS_BAR_COLLAPSE_ON_DISMISS,
                Settings.System.STATUS_BAR_COLLAPSE_IF_NO_CLEARABLE);
        mCollapseOnDismiss = (ListPreference) findPreference(UI_COLLAPSE_BEHAVIOUR);
        mCollapseOnDismiss.setValue(String.valueOf(collapseBehaviour));
        mCollapseOnDismiss.setOnPreferenceChangeListener(this);
        updateCollapseBehaviourSummary(collapseBehaviour);
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        if (preference == mCollapseOnDismiss) {
            int value = Integer.valueOf((String) objValue);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.STATUS_BAR_COLLAPSE_ON_DISMISS, value);
            updateCollapseBehaviourSummary(value);
            return true;
        } else if (preference == mHideLabels) {
            int hideLabels = Integer.valueOf((String) objValue);
            Settings.System.putInt(getContentResolver(), Settings.System.NOTIFICATION_HIDE_LABELS,
                    hideLabels);
            updateHideNotificationLabelsSummary(hideLabels);
            return true;
        } else if (preference == mStatusBarNotificationSwipeFloating) {
            int value = Integer.valueOf((String) objValue);
            int index = mStatusBarNotificationSwipeFloating.findIndexOfValue((String) objValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUS_BAR_NOTIFICATION_SWIPE_FLOATING, value);
            mStatusBarNotificationSwipeFloating.setSummary(mStatusBarNotificationSwipeFloating.getEntries()[index]);
            return true;
        }
        return false;
    }

    private void updateCollapseBehaviourSummary(int setting) {
        String[] summaries = getResources().getStringArray(
                R.array.notification_drawer_collapse_on_dismiss_summaries);
        mCollapseOnDismiss.setSummary(summaries[setting]);
    }

    private void updateHideNotificationLabelsSummary(int value) {
        Resources res = getResources();

        StringBuilder text = new StringBuilder();

        switch (value) {
        case 1  : text.append(res.getString(R.string.notification_hide_labels_carrier));
                break;
        case 2  : text.append(res.getString(R.string.notification_hide_labels_wifi));
                break;
        case 3  : text.append(res.getString(R.string.notification_hide_labels_all));
                break;
        default : text.append(res.getString(R.string.notification_hide_labels_disable));
                break;
        }

        text.append(" " + res.getString(R.string.notification_hide_labels_text));
        mHideLabels.setSummary(text.toString());

    }
}