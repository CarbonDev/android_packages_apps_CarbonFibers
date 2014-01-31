/*
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

package com.carbon.fibers.fragments.ls;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.preference.PreferenceCategory;
import android.preference.SeekBarPreference;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;
import android.view.Window;
import android.widget.Toast;

import com.carbon.fibers.R;
import com.carbon.fibers.preference.SettingsPreferenceFragment;

import java.io.File;
import java.io.IOException;

public class LockscreenGeneral extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String TAG = "LockscreenGeneral";

    private static final int DLG_ENABLE_EIGHT_TARGETS = 0;

    private static final String KEY_BATTERY_STATUS = "lockscreen_battery_status";
    private static final String KEY_SEE_TRHOUGH = "see_through";
    private static final String KEY_BLUR_RADIUS = "blur_radius";
    private static final String PREF_LOCKSCREEN_EIGHT_TARGETS = "lockscreen_eight_targets";
    private static final String PREF_LOCKSCREEN_SHORTCUTS = "lockscreen_shortcuts";

    private ListPreference mBatteryStatus;
    private CheckBoxPreference mSeeThrough;
    private SeekBarPreference mBlurRadius;
    private CheckBoxPreference mLockscreenEightTargets;
    private Preference mShortcuts;

    private Activity mActivity;
    private ContentResolver mResolver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        mActivity = getActivity();
        mResolver = mActivity.getContentResolver();

        addPreferencesFromResource(R.xml.lockscreen_general);

        PreferenceScreen prefs = getPreferenceScreen();
        ContentResolver resolver = getContentResolver();

        mBatteryStatus = (ListPreference) findPreference(KEY_BATTERY_STATUS);
        int batteryStatus = Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.LOCKSCREEN_BATTERY_VISIBILITY, 0);
        mBatteryStatus.setValue(String.valueOf(batteryStatus));
        mBatteryStatus.setSummary(mBatteryStatus.getEntry());
        mBatteryStatus.setOnPreferenceChangeListener(this);

        // lockscreen see through
        mSeeThrough = (CheckBoxPreference) prefs.findPreference(KEY_SEE_TRHOUGH);
        mBlurRadius = (SeekBarPreference) prefs.findPreference(KEY_BLUR_RADIUS);
        mBlurRadius.setProgress(Settings.System.getInt(resolver,
            Settings.System.LOCKSCREEN_BLUR_RADIUS, 12));
        mBlurRadius.setOnPreferenceChangeListener(this);

        mLockscreenEightTargets = (CheckBoxPreference) findPreference(
                PREF_LOCKSCREEN_EIGHT_TARGETS);
        mLockscreenEightTargets.setChecked(Settings.System.getInt(
                getActivity().getApplicationContext().getContentResolver(),
                Settings.System.LOCKSCREEN_EIGHT_TARGETS, 0) == 1);
        mLockscreenEightTargets.setOnPreferenceChangeListener(this);

        mShortcuts = (Preference) findPreference(PREF_LOCKSCREEN_SHORTCUTS);
        mShortcuts.setEnabled(!mLockscreenEightTargets.isChecked());

    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        ContentResolver cr = getContentResolver();

        if (preference == mSeeThrough) {
              Settings.System.putInt(cr, Settings.System.LOCKSCREEN_SEE_THROUGH,
                      mSeeThrough.isChecked() ? 1 : 0);
            return true;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        ContentResolver cr = getActivity().getContentResolver();

        if (preference == mBlurRadius) {
            Settings.System.putInt(cr, Settings.System.LOCKSCREEN_BLUR_RADIUS, (Integer)objValue);
            return true;
        } else if (preference == mBatteryStatus) {
            int value = Integer.valueOf((String) objValue);
            int index = mBatteryStatus.findIndexOfValue((String) objValue);
            Settings.System.putInt(cr, Settings.System.LOCKSCREEN_BATTERY_VISIBILITY, value);
            mBatteryStatus.setSummary(mBatteryStatus.getEntries()[index]);
            return true;
        } else if (preference == mLockscreenEightTargets) {
            showDialogInner(DLG_ENABLE_EIGHT_TARGETS, (Boolean) objValue);
            return true;
        }

        return false;
    }

    private void showDialogInner(int id, boolean state) {
        DialogFragment newFragment = MyAlertDialogFragment.newInstance(id, state);
        newFragment.setTargetFragment(this, 0);
        newFragment.show(getFragmentManager(), "dialog " + id);
    }

    public static class MyAlertDialogFragment extends DialogFragment {

        public static MyAlertDialogFragment newInstance(int id, boolean state) {
            MyAlertDialogFragment frag = new MyAlertDialogFragment();
            Bundle args = new Bundle();
            args.putInt("id", id);
            args.putBoolean("state", state);
            frag.setArguments(args);
            return frag;
        }

        LockscreenGeneral getOwner() {
            return (LockscreenGeneral) getTargetFragment();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int id = getArguments().getInt("id");
            final boolean state = getArguments().getBoolean("state");
            switch (id) {
                case DLG_ENABLE_EIGHT_TARGETS:
                    String message = getOwner().getResources()
                                .getString(R.string.lockscreen_enable_eight_targets_dialog);
                    if (state) {
                        message = message + " " + getOwner().getResources().getString(
                                R.string.lockscreen_enable_eight_targets_enabled_dialog);
                    }
                    return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.attention)
                    .setMessage(message)
                    .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().getContentResolver(),
                                    Settings.System.LOCKSCREEN_EIGHT_TARGETS, state ? 1 : 0);
                            getOwner().mShortcuts.setEnabled(!state);
                            Settings.System.putString(getOwner().getContentResolver(),
                                    Settings.System.LOCKSCREEN_TARGETS, null);
                            for (File pic : getOwner().getActivity().getFilesDir().listFiles()) {
                                if (pic.getName().startsWith("lockscreen_")) {
                                    pic.delete();
                                }
                            }
                            if (state) {
                                Toast.makeText(getOwner().getActivity(),
                                        R.string.lockscreen_target_reset,
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    })
                    .create();
            }
            throw new IllegalArgumentException("unknown id " + id);
        }

        @Override
        public void onCancel(DialogInterface dialog) {
            int id = getArguments().getInt("id");
            boolean state = getArguments().getBoolean("state");
            switch (id) {
                case DLG_ENABLE_EIGHT_TARGETS:
                    getOwner().mLockscreenEightTargets.setChecked(!state);
                    break;
             }
        }
    }
}
