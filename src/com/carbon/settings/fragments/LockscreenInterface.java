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

package com.carbon.settings.fragments;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.Display;
import android.view.Window;
import android.widget.Toast;

import com.carbon.settings.R;
import com.carbon.settings.SettingsPreferenceFragment;
import com.carbon.settings.widgets.ColorPickerView;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class LockscreenInterface extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String TAG = "LockscreenInterface";

    private static final int REQUEST_CODE_BG_WALLPAPER = 1024;

    private static final int LOCKSCREEN_BACKGROUND_COLOR_FILL = 0;
    private static final int LOCKSCREEN_BACKGROUND_CUSTOM_IMAGE = 1;
    private static final int LOCKSCREEN_BACKGROUND_DEFAULT_WALLPAPER = 2;

    private static final String KEY_ALWAYS_BATTERY = "lockscreen_battery_status";
    private static final String KEY_LOCKSCREEN_BUTTONS = "lockscreen_buttons";
    private static final String KEY_LOCKSCREEN_MUSIC_CONTROLS = "lockscreen_music_controls";
    private static final String PREF_LOCKSCREEN_HIDE_INITIAL_PAGE_HINTS = "lockscreen_hide_initial_page_hints";
    private static final String KEY_BACKGROUND_PREF = "lockscreen_background";
    private static final String PREF_QUICK_UNLOCK = "lockscreen_quick_unlock_control";
    private static final String KEY_SEE_TRHOUGH = "see_through";
    private static final String PREF_LOCKSCREEN_ALL_WIDGETS = "lockscreen_all_widgets";
    private static final String KEY_LOCKSCREEN_CAMERA_WIDGET = "lockscreen_camera_widget";

    private PreferenceScreen mLockscreenButtons;
    private ListPreference mBatteryStatus;
    private CheckBoxPreference mMusicControls;
    private CheckBoxPreference mLockscreenHideInitialPageHints;
    private ListPreference mCustomBackground;
    private CheckBoxPreference mQuickUnlock;
    private CheckBoxPreference mSeeThrough;
    private CheckBoxPreference mLockscreenAllWidgets;
    private CheckBoxPreference mCameraWidget;

    private File mWallpaperImage;
    private File mWallpaperTemporary;

    public boolean hasButtons() {
        return !getResources().getBoolean(com.android.internal.R.bool.config_showNavigationBar);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ContentResolver resolver = getContentResolver();
        mContext = getActivity();

        addPreferencesFromResource(R.xml.lockscreen_interface_settings);

        // Battery status
        mBatteryStatus = (ListPreference) findPreference(KEY_ALWAYS_BATTERY);
        if (mBatteryStatus != null) {
            int batteryStatus = Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.LOCKSCREEN_ALWAYS_SHOW_BATTERY, 0);
            mBatteryStatus.setValueIndex(batteryStatus);
            mBatteryStatus.setSummary(mBatteryStatus.getEntries()[batteryStatus]);
            mBatteryStatus.setOnPreferenceChangeListener(this);
        }

        mSeeThrough = (CheckBoxPreference) findPreference(KEY_SEE_TRHOUGH);
        mSeeThrough.setChecked(Settings.System.getInt(resolver,
                Settings.System.LOCKSCREEN_SEE_THROUGH, 0) == 1);

        mMusicControls = (CheckBoxPreference) findPreference(KEY_LOCKSCREEN_MUSIC_CONTROLS);
        mMusicControls.setOnPreferenceChangeListener(this);

        mLockscreenButtons = (PreferenceScreen) findPreference(KEY_LOCKSCREEN_BUTTONS);
        if (!hasButtons()) {
            getPreferenceScreen().removePreference(mLockscreenButtons);
        }

        mLockscreenHideInitialPageHints = (CheckBoxPreference)findPreference(PREF_LOCKSCREEN_HIDE_INITIAL_PAGE_HINTS);
        mLockscreenHideInitialPageHints.setChecked(Settings.System.getBoolean(getActivity().getContentResolver(),
                Settings.System.LOCKSCREEN_HIDE_INITIAL_PAGE_HINTS, false));

        mQuickUnlock = (CheckBoxPreference) findPreference(PREF_QUICK_UNLOCK);
        mQuickUnlock.setChecked(Settings.System.getBoolean(mContext.getContentResolver(),
                Settings.System.LOCKSCREEN_QUICK_UNLOCK_CONTROL, false));

        mLockscreenAllWidgets = (CheckBoxPreference)findPreference(PREF_LOCKSCREEN_ALL_WIDGETS);
        mLockscreenAllWidgets.setChecked(Settings.System.getBoolean(getActivity().getContentResolver(),
                Settings.System.LOCKSCREEN_ALL_WIDGETS, false));

        mCustomBackground = (ListPreference) findPreference(KEY_BACKGROUND_PREF);
        mCustomBackground.setOnPreferenceChangeListener(this);
        updateCustomBackgroundSummary();

        mCameraWidget = (CheckBoxPreference) findPreference(KEY_LOCKSCREEN_CAMERA_WIDGET);
        mCameraWidget.setChecked(Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.KG_CAMERA_WIDGET, 0) == 1);

        mWallpaperImage = new File(getActivity().getFilesDir() + "/lockwallpaper");
        mWallpaperTemporary = new File(getActivity().getCacheDir() + "/lockwallpaper.tmp");
    }

    private void updateCustomBackgroundSummary() {
        int resId;
        String value = Settings.System.getString(getContentResolver(),
                Settings.System.LOCKSCREEN_BACKGROUND);
        if (value == null) {
            resId = R.string.lockscreen_background_default_wallpaper;
            mCustomBackground.setValueIndex(LOCKSCREEN_BACKGROUND_DEFAULT_WALLPAPER);
        } else if (value.isEmpty()) {
            resId = R.string.lockscreen_background_custom_image;
            mCustomBackground.setValueIndex(LOCKSCREEN_BACKGROUND_CUSTOM_IMAGE);
        } else {
            resId = R.string.lockscreen_background_color_fill;
            mCustomBackground.setValueIndex(LOCKSCREEN_BACKGROUND_COLOR_FILL);
        }
        mCustomBackground.setSummary(getResources().getString(resId));
    }

    @Override
    public void onResume() {
        super.onResume();

        ContentResolver cr = getActivity().getContentResolver();
        if (mMusicControls != null) {
            mMusicControls.setChecked(Settings.System.getInt(cr,
                    Settings.System.LOCKSCREEN_MUSIC_CONTROLS, 1) == 1);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_BG_WALLPAPER) {
            int hintId;

            if (resultCode == Activity.RESULT_OK) {
                if (mWallpaperTemporary.exists()) {
                    mWallpaperTemporary.renameTo(mWallpaperImage);
                }
                mWallpaperImage.setReadOnly();
                hintId = R.string.lockscreen_background_result_successful;
                Settings.System.putString(getContentResolver(),
                        Settings.System.LOCKSCREEN_BACKGROUND, "");
                updateCustomBackgroundSummary();
            } else {
                if (mWallpaperTemporary.exists()) {
                    mWallpaperTemporary.delete();
                }
                hintId = R.string.lockscreen_background_result_not_successful;
            }
            Toast.makeText(getActivity(),
                    getResources().getString(hintId), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mLockscreenHideInitialPageHints) {
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.LOCKSCREEN_HIDE_INITIAL_PAGE_HINTS,
                    ((CheckBoxPreference)preference).isChecked() ? 1 : 0);
            return true;
        } else if (preference == mQuickUnlock) {
            Settings.System.putBoolean(mContext.getContentResolver(),
                    Settings.System.LOCKSCREEN_QUICK_UNLOCK_CONTROL,
                    ((CheckBoxPreference) preference).isChecked());
            return true;
         } else if (preference == mSeeThrough) {
            Settings.System.putInt(mContext.getContentResolver(), Settings.System.LOCKSCREEN_SEE_THROUGH, 
                    mSeeThrough.isChecked() ? 1 : 0);
            return true;
        } else if (preference == mLockscreenAllWidgets) {
            Settings.System.putBoolean(mContext.getContentResolver(),
                    Settings.System.LOCKSCREEN_ALL_WIDGETS,
                    ((CheckBoxPreference) preference).isChecked());
            return true;
        } else if (preference == mCameraWidget) {
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.KG_CAMERA_WIDGET, mCameraWidget.isChecked() ? 1 : 0);
            return true;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        ContentResolver cr = getActivity().getContentResolver();

        if (preference == mBatteryStatus) {
            int value = Integer.valueOf((String) objValue);
            int index = mBatteryStatus.findIndexOfValue((String) objValue);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.LOCKSCREEN_ALWAYS_SHOW_BATTERY, value);
            mBatteryStatus.setSummary(mBatteryStatus.getEntries()[index]);
            return true;
        } else if (preference == mMusicControls) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(cr, Settings.System.LOCKSCREEN_MUSIC_CONTROLS, value ? 1 : 0);
            return true;
        } else if (preference == mCustomBackground) {
            int selection = mCustomBackground.findIndexOfValue(objValue.toString());
            return handleBackgroundSelection(selection);
        }
        return false;
    }

    private boolean handleBackgroundSelection(int selection) {
        if (selection == LOCKSCREEN_BACKGROUND_COLOR_FILL) {
            final ColorPickerView colorView = new ColorPickerView(getActivity());
            int currentColor = Settings.System.getInt(getContentResolver(),
                    Settings.System.LOCKSCREEN_BACKGROUND, -1);

            if (currentColor != -1) {
                colorView.setColor(currentColor);
            }
            colorView.setAlphaSliderVisible(true);

            new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.lockscreen_custom_background_dialog_title)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getContentResolver(),
                                    Settings.System.LOCKSCREEN_BACKGROUND, colorView.getColor());
                            updateCustomBackgroundSummary();
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setView(colorView)
                    .show();
        } else if (selection == LOCKSCREEN_BACKGROUND_CUSTOM_IMAGE) {
            final Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
            intent.setType("image/*");
            intent.putExtra("crop", "true");
            intent.putExtra("scale", true);
            intent.putExtra("scaleUpIfNeeded", false);
            intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());

            final Display display = getActivity().getWindowManager().getDefaultDisplay();
            final Rect rect = new Rect();
            final Window window = getActivity().getWindow();

            window.getDecorView().getWindowVisibleDisplayFrame(rect);

            int statusBarHeight = rect.top;
            int contentViewTop = window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
            int titleBarHeight = contentViewTop - statusBarHeight;
            boolean isPortrait = getResources().getConfiguration().orientation ==
                    Configuration.ORIENTATION_PORTRAIT;

            int width = display.getWidth();
            int height = display.getHeight() - titleBarHeight;

            intent.putExtra("aspectX", isPortrait ? width : height);
            intent.putExtra("aspectY", isPortrait ? height : width);

            try {
                mWallpaperTemporary.createNewFile();
                mWallpaperTemporary.setWritable(true, false);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mWallpaperTemporary));
                intent.putExtra("return-data", false);
                getActivity().startActivityFromFragment(this, intent, REQUEST_CODE_BG_WALLPAPER);
            } catch (IOException e) {
                // Do nothing here
            } catch (ActivityNotFoundException e) {
                // Do nothing here
            }
        } else if (selection == LOCKSCREEN_BACKGROUND_DEFAULT_WALLPAPER) {
            Settings.System.putString(getContentResolver(),
                    Settings.System.LOCKSCREEN_BACKGROUND, null);
            updateCustomBackgroundSummary();
            return true;
        }

        return false;
    }
}
