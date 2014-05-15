/*
 * Copyright (C) 2012 Slimroms Project
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

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.carbon.fibers.R;
import com.carbon.fibers.preference.SettingsPreferenceFragment;
import com.carbon.fibers.Utils;
import net.margaritov.preference.colorpicker.ColorPickerPreference;

import java.util.Date;

public class BatterySettings extends SettingsPreferenceFragment
    implements OnPreferenceChangeListener {

    private static final String TAG = "BatteryIconStyle";

    private static final String PREF_STATUS_BAR_BATTERY = "battery_icon";
    private static final String PREF_STATUS_BAR_BATTERY_COLOR = "battery_color";
    private static final String PREF_STATUS_BAR_BATTERY_TEXT_COLOR = "battery_text_color";
    private static final String PREF_STATUS_BAR_BATTERY_TEXT_CHARGING_COLOR = "battery_text_charging_color";
    private static final String PREF_STATUS_BAR_CIRCLE_BATTERY_ANIMATIONSPEED = "circle_battery_animation_speed";
    private static final String PREF_BATT_BAR = "battery_bar_list";
    private static final String PREF_BATT_BAR_STYLE = "battery_bar_style";
    private static final String PREF_BATT_BAR_COLOR = "battery_bar_color";
    private static final String PREF_BATT_BAR_WIDTH = "battery_bar_thickness";
    private static final String PREF_BATT_ANIMATE = "battery_bar_animate";

    private static final int MENU_RESET = Menu.FIRST;

    private ListPreference mStatusBarBattery;
    private ColorPickerPreference mBatteryColor;
    private ColorPickerPreference mBatteryTextColor;
    private ColorPickerPreference mBatteryTextChargingColor;
    private ListPreference mCircleAnimSpeed;
    private ListPreference mBatteryBar;
    private ListPreference mBatteryBarStyle;
    private ListPreference mBatteryBarThickness;
    private CheckBoxPreference mBatteryBarChargingAnimation;
    private ColorPickerPreference mBatteryBarColor;

    private boolean mCheckPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createCustomView();
    }

    private PreferenceScreen createCustomView() {
        mCheckPreferences = false;
        PreferenceScreen prefSet = getPreferenceScreen();
        if (prefSet != null) {
            prefSet.removeAll();
        }

        addPreferencesFromResource(R.xml.sb_battery_settings);
        prefSet = getPreferenceScreen();

        ContentResolver resolver = getActivity().getContentResolver();

        int intColor;
        String hexColor;

        PackageManager pm = getPackageManager();
        Resources systemUiResources;
        try {
            systemUiResources = pm.getResourcesForApplication("com.android.systemui");
        } catch (Exception e) {
            Log.e(TAG, "can't access systemui resources",e);
            return null;
        }

        mStatusBarBattery = (ListPreference) prefSet.findPreference(PREF_STATUS_BAR_BATTERY);
        mStatusBarBattery.setOnPreferenceChangeListener(this);
        int statusBarBattery = Settings.System.getInt(resolver,
                Settings.System.STATUS_BAR_BATTERY, 0);
        mStatusBarBattery.setValue(String.valueOf(statusBarBattery));
        mStatusBarBattery.setSummary(mStatusBarBattery.getEntry());

        mBatteryColor = (ColorPickerPreference) findPreference(PREF_STATUS_BAR_BATTERY_COLOR);
        mBatteryColor.setOnPreferenceChangeListener(this);
        intColor = Settings.System.getInt(resolver,
                    Settings.System.STATUS_BAR_BATTERY_COLOR, -2);
        if (intColor == -2) {
            intColor = systemUiResources.getColor(systemUiResources.getIdentifier(
                    "com.android.systemui:color/batterymeter_charge_color", null, null));
            mBatteryColor.setSummary(getResources().getString(R.string.default_string));
        } else {
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mBatteryColor.setSummary(hexColor);
        }
        mBatteryColor.setNewPreviewColor(intColor);

        mBatteryTextColor =
            (ColorPickerPreference) findPreference(PREF_STATUS_BAR_BATTERY_TEXT_COLOR);
        mBatteryTextColor.setOnPreferenceChangeListener(this);
        intColor = Settings.System.getInt(resolver,
                    Settings.System.STATUS_BAR_BATTERY_TEXT_COLOR, -2);
        mBatteryTextColor.setSummary(getResources().getString(R.string.default_string));
        if (intColor == -2 && statusBarBattery == 2) {
            intColor = systemUiResources.getColor(systemUiResources.getIdentifier(
                    "com.android.systemui:color/batterymeter_bolt_color", null, null));
        } else if (intColor == -2) {
            intColor = systemUiResources.getColor(systemUiResources.getIdentifier(
                    "com.android.systemui:color/batterymeter_charge_color", null, null));
        } else {
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mBatteryTextColor.setSummary(hexColor);
        }
        mBatteryTextColor.setNewPreviewColor(intColor);

        mBatteryTextChargingColor = (ColorPickerPreference)
            findPreference(PREF_STATUS_BAR_BATTERY_TEXT_CHARGING_COLOR);
        mBatteryTextChargingColor.setOnPreferenceChangeListener(this);
        intColor = Settings.System.getInt(resolver,
                    Settings.System.STATUS_BAR_BATTERY_TEXT_CHARGING_COLOR, -2);
        mBatteryTextChargingColor.setSummary(getResources().getString(R.string.default_string));
        if (intColor == -2 && (statusBarBattery == 2 || statusBarBattery == 0)) {
            intColor = systemUiResources.getColor(systemUiResources.getIdentifier(
                    "com.android.systemui:color/batterymeter_bolt_color", null, null));
        } else if (intColor == -2) {
            intColor = systemUiResources.getColor(systemUiResources.getIdentifier(
                    "com.android.systemui:color/batterymeter_charge_color", null, null));
        } else {
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mBatteryTextChargingColor.setSummary(hexColor);
        }
        mBatteryTextChargingColor.setNewPreviewColor(intColor);

        mCircleAnimSpeed =
            (ListPreference) findPreference(PREF_STATUS_BAR_CIRCLE_BATTERY_ANIMATIONSPEED);
        mCircleAnimSpeed.setOnPreferenceChangeListener(this);
        mCircleAnimSpeed.setValue((Settings.System
                .getInt(resolver, Settings.System.STATUS_BAR_CIRCLE_BATTERY_ANIMATIONSPEED, 3))
                + "");
        mCircleAnimSpeed.setSummary(mCircleAnimSpeed.getEntry());

        mBatteryBar = (ListPreference) findPreference(PREF_BATT_BAR);
        mBatteryBar.setOnPreferenceChangeListener(this);
        mBatteryBar.setValue((Settings.System.getInt(resolver, Settings.System.STATUSBAR_BATTERY_BAR, 0)) + "");
        mBatteryBar.setSummary(mBatteryBar.getEntry());

        mBatteryBarStyle = (ListPreference) findPreference(PREF_BATT_BAR_STYLE);
        mBatteryBarStyle.setOnPreferenceChangeListener(this);
        mBatteryBarStyle.setValue((Settings.System.getInt(resolver,
                Settings.System.STATUSBAR_BATTERY_BAR_STYLE, 0)) + "");
        mBatteryBarStyle.setSummary(mBatteryBarStyle.getEntry());

        mBatteryBarColor = (ColorPickerPreference) findPreference(PREF_BATT_BAR_COLOR);
        mBatteryBarColor.setOnPreferenceChangeListener(this);
        int defaultColor = 0xffffffff;
        intColor = Settings.System.getInt(resolver, Settings.System.STATUSBAR_BATTERY_BAR_COLOR, defaultColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mBatteryBarColor.setSummary(hexColor);

        mBatteryBarChargingAnimation = (CheckBoxPreference) findPreference(PREF_BATT_ANIMATE);
        mBatteryBarChargingAnimation.setChecked(Settings.System.getInt(resolver,
                Settings.System.STATUSBAR_BATTERY_BAR_ANIMATE, 0) == 1);

        mBatteryBarThickness = (ListPreference) findPreference(PREF_BATT_BAR_WIDTH);
        mBatteryBarThickness.setOnPreferenceChangeListener(this);
        mBatteryBarThickness.setValue((Settings.System.getInt(resolver,
                Settings.System.STATUSBAR_BATTERY_BAR_THICKNESS, 1)) + "");
        mBatteryBarThickness.setSummary(mBatteryBarThickness.getEntry());

        updateBatteryBarOptions();
        updateBatteryIconOptions(statusBarBattery);

        setHasOptionsMenu(true);
        mCheckPreferences = true;
        return prefSet;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add(0, MENU_RESET, 0, R.string.reset)
                .setIcon(R.drawable.ic_action_backup) // use the backup icon
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_RESET:
                resetToDefault();
                return true;
             default:
                return super.onContextItemSelected(item);
        }
    }

    private void resetToDefault() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle(R.string.reset);
        alertDialog.setMessage(R.string.battery_style_reset_message);
        alertDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                iconColorReset();
                createCustomView();
            }
        });
        alertDialog.setNegativeButton(R.string.cancel, null);
        alertDialog.create().show();
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();

        if (!mCheckPreferences) {
            return false;
        }
        if (preference == mStatusBarBattery) {
            int statusBarBattery = Integer.valueOf((String) newValue);
            int index = mStatusBarBattery.findIndexOfValue((String) newValue);
            Settings.System.putInt(resolver,
                    Settings.System.STATUS_BAR_BATTERY, statusBarBattery);
            mStatusBarBattery.setSummary(mStatusBarBattery.getEntries()[index]);
            createCustomView();
            return true;
        } else if (preference == mBatteryColor) {
            String hex = ColorPickerPreference.convertToARGB(Integer
                    .valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(resolver,
                    Settings.System.STATUS_BAR_BATTERY_COLOR, intHex);
            return true;
        } else if (preference == mBatteryTextColor) {
            String hex = ColorPickerPreference.convertToARGB(Integer
                    .valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(resolver,
                    Settings.System.STATUS_BAR_BATTERY_TEXT_COLOR, intHex);
            return true;
        } else if (preference == mBatteryTextChargingColor) {
            String hex = ColorPickerPreference.convertToARGB(Integer
                    .valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(resolver,
                    Settings.System.STATUS_BAR_BATTERY_TEXT_CHARGING_COLOR, intHex);
            return true;
        } else if (preference == mCircleAnimSpeed) {
            int val = Integer.parseInt((String) newValue);
            int index = mCircleAnimSpeed.findIndexOfValue((String) newValue);
            Settings.System.putInt(resolver,
                    Settings.System.STATUS_BAR_CIRCLE_BATTERY_ANIMATIONSPEED, val);
            mCircleAnimSpeed.setSummary(mCircleAnimSpeed.getEntries()[index]);
            return true;
        } else if (preference == mBatteryBarColor) {
            String hex = ColorPickerPreference.convertToARGB(Integer
                    .valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(resolver,
                    Settings.System.STATUSBAR_BATTERY_BAR_COLOR, intHex);
            return true;
        } else if (preference == mBatteryBar) {
            int val = Integer.valueOf((String) newValue);
            int index = mBatteryBar.findIndexOfValue((String) newValue);
            Settings.System.putInt(resolver, Settings.System.STATUSBAR_BATTERY_BAR, val);
            mBatteryBar.setSummary(mBatteryBar.getEntries()[index]);
            updateBatteryBarOptions();
            return true;
        } else if (preference == mBatteryBarStyle) {
            int val = Integer.valueOf((String) newValue);
            int index = mBatteryBarStyle.findIndexOfValue((String) newValue);
            Settings.System.putInt(resolver, Settings.System.STATUSBAR_BATTERY_BAR_STYLE, val);
            mBatteryBarStyle.setSummary(mBatteryBarStyle.getEntries()[index]);
            return true;
        } else if (preference == mBatteryBarThickness) {
            int val = Integer.valueOf((String) newValue);
            int index = mBatteryBarThickness.findIndexOfValue((String) newValue);
            Settings.System.putInt(resolver, Settings.System.STATUSBAR_BATTERY_BAR_THICKNESS, val);
            mBatteryBarThickness.setSummary(mBatteryBarThickness.getEntries()[index]);
            return true;
        }
        return false;
    }

   public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        ContentResolver resolver = getActivity().getContentResolver();
        boolean value;
        if (preference == mBatteryBarChargingAnimation) {
            value = mBatteryBarChargingAnimation.isChecked();
            Settings.System.putInt(resolver, Settings.System.STATUSBAR_BATTERY_BAR_ANIMATE, value ? 1 : 0);
            return true;
        }
        return false;
    }

    private void iconColorReset() {
        Settings.System.putInt(getActivity().getContentResolver(),
                Settings.System.STATUS_BAR_BATTERY_COLOR, -2);
        Settings.System.putInt(getActivity().getContentResolver(),
                Settings.System.STATUS_BAR_BATTERY_TEXT_COLOR, -2);
        Settings.System.putInt(getActivity().getContentResolver(),
                Settings.System.STATUS_BAR_BATTERY_TEXT_CHARGING_COLOR, -2);
    }

    private void updateBatteryIconOptions(int batteryIconStat) {
        mBatteryTextChargingColor.setTitle(R.string.battery_bolt_color);
        if (batteryIconStat == 0) {
            mBatteryColor.setEnabled(true);
            mBatteryTextColor.setEnabled(false);
            mBatteryTextChargingColor.setEnabled(true);
            mCircleAnimSpeed.setEnabled(false);
        } else if (batteryIconStat == 2 || batteryIconStat == 7) {
            mBatteryColor.setEnabled(true);
            mBatteryTextColor.setEnabled(true);
            mBatteryTextChargingColor.setEnabled(true);
            mCircleAnimSpeed.setEnabled(false);
        } else if (batteryIconStat == 3 || batteryIconStat == 5) {
            mBatteryColor.setEnabled(true);
            mBatteryTextColor.setEnabled(false);
            mBatteryTextChargingColor.setEnabled(true);
            mCircleAnimSpeed.setEnabled(true);
            mBatteryTextChargingColor.setTitle(R.string.battery_circle_charging_color);
        } else if (batteryIconStat == 4 || batteryIconStat == 6) {
            mBatteryColor.setEnabled(true);
            mBatteryTextColor.setEnabled(true);
            mBatteryTextChargingColor.setEnabled(true);
            mCircleAnimSpeed.setEnabled(true);
            mBatteryTextChargingColor.setTitle(R.string.battery_circle_charging_color);
        } else if (batteryIconStat == 8) {
            mBatteryColor.setEnabled(false);
            mBatteryTextColor.setEnabled(false);
            mBatteryTextChargingColor.setEnabled(false);
            mCircleAnimSpeed.setEnabled(false);
        } else {
            mBatteryColor.setEnabled(false);
            mBatteryTextColor.setEnabled(true);
            mBatteryTextChargingColor.setEnabled(true);
            mBatteryTextChargingColor.setTitle(R.string.battery_text_charging_color);
            mCircleAnimSpeed.setEnabled(false);
        }
    }

    private void updateBatteryBarOptions() {
        if (Settings.System.getInt(getActivity().getContentResolver(),
            Settings.System.STATUSBAR_BATTERY_BAR, 0) == 0) {
            mBatteryBarStyle.setEnabled(false);
            mBatteryBarThickness.setEnabled(false);
            mBatteryBarChargingAnimation.setEnabled(false);
            mBatteryBarColor.setEnabled(false);
        } else {
            mBatteryBarStyle.setEnabled(true);
            mBatteryBarThickness.setEnabled(true);
            mBatteryBarChargingAnimation.setEnabled(true);
            mBatteryBarColor.setEnabled(true);
        }
    }

}
