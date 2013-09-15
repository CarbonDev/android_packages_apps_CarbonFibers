package com.carbon.settings.fragments.navbar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.StateSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.android.internal.util.carbon.AwesomeConstants;
import com.android.internal.util.carbon.AwesomeConstants.AwesomeConstant;
import com.android.internal.util.carbon.BackgroundAlphaColorDrawable;
import com.android.internal.util.carbon.NavBarHelpers;
import com.carbon.settings.SettingsPreferenceFragment;
import com.carbon.settings.R;
import com.carbon.settings.util.Helpers;
import com.carbon.settings.util.ShortcutPickerHelper;
import com.carbon.settings.widgets.SeekBarPreference;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class NavigationAdvanced extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {

    private static final String NAVIGATION_BAR_COLOR = "nav_bar_color";
    private static final String PREF_NAV_COLOR = "nav_button_color";
    private static final String PREF_NAV_GLOW_COLOR = "nav_button_glow_color";
    private static final String PREF_GLOW_TIMES = "glow_times";
    private static final String NAVIGATION_BAR_HEIGHT = "navigation_bar_height";
    private static final String NAVIGATION_BAR_HEIGHT_LANDSCAPE = "navigation_bar_height_landscape";
    private static final String NAVIGATION_BAR_WIDTH = "navigation_bar_width";

    ColorPickerPreference mNavigationColor;
    ColorPickerPreference mNavigationBarColor;
    ColorPickerPreference mNavigationBarGlowColor;
    ListPreference mGlowTimes;
    SeekBarPreference mNavigationBarHeight;
    SeekBarPreference mNavigationBarHeightLandscape;
    SeekBarPreference mNavigationBarWidth;
    SeekBarPreference mButtonAlpha;

    private static final String TAG = "Navbar";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.nav_bar_advanced);

        PreferenceScreen prefs = getPreferenceScreen();

        mNavigationColor = (ColorPickerPreference) findPreference(NAVIGATION_BAR_COLOR);
        mNavigationColor.setOnPreferenceChangeListener(this);

        mNavigationBarColor = (ColorPickerPreference) findPreference(PREF_NAV_COLOR);
        mNavigationBarColor.setOnPreferenceChangeListener(this);

        mNavigationBarGlowColor = (ColorPickerPreference) findPreference(PREF_NAV_GLOW_COLOR);
        mNavigationBarGlowColor.setOnPreferenceChangeListener(this);

        mGlowTimes = (ListPreference) findPreference(PREF_GLOW_TIMES);
        mGlowTimes.setOnPreferenceChangeListener(this);

        final float defaultButtonAlpha = Settings.System.getFloat(mContentRes,
                Settings.System.NAVIGATION_BAR_BUTTON_ALPHA,0.6f);
        mButtonAlpha = (SeekBarPreference) findPreference("button_transparency");
        mButtonAlpha.setInitValue((int) (defaultButtonAlpha * 100));
        mButtonAlpha.setOnPreferenceChangeListener(this);

        int defNavBarSize = getResources().getDimensionPixelSize(R.dimen.navigation_bar_48);
        int navBarSize = Settings.System.getInt(mContentRes, Settings.System.NAVIGATION_BAR_HEIGHT, defNavBarSize);
        mNavigationBarHeight = (SeekBarPreference) findPreference("navigation_bar_height");
        mNavigationBarHeight.setInitValue((int)((float)navBarSize / (float)defNavBarSize * 100.0f));
        mNavigationBarHeight.setOnPreferenceChangeListener(this);
 
        navBarSize = Settings.System.getInt(mContentRes, Settings.System.NAVIGATION_BAR_HEIGHT_LANDSCAPE, defNavBarSize);
        mNavigationBarHeightLandscape = (SeekBarPreference) findPreference("navigation_bar_height_landscape");
        mNavigationBarHeightLandscape.setInitValue((int)((float)navBarSize / (float)defNavBarSize * 100.0f));
        mNavigationBarHeightLandscape.setOnPreferenceChangeListener(this);

        
        navBarSize = Settings.System.getInt(mContentRes, Settings.System.NAVIGATION_BAR_WIDTH, defNavBarSize);
        mNavigationBarWidth = (SeekBarPreference) findPreference("navigation_bar_width");
        mNavigationBarWidth.setInitValue((int)((float)navBarSize / (float)defNavBarSize * 100.0f));
        mNavigationBarWidth.setOnPreferenceChangeListener(this);

        setHasOptionsMenu(true);
        updateGlowTimesSummary();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.nav_bar, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.reset:
                Settings.System.putInt(mContentRes,
                        Settings.System.NAVIGATION_BAR_COLOR, -1);
                Settings.System.putInt(mContentRes,
                        Settings.System.NAVIGATION_BAR_TINT, -1);
                Settings.System.putInt(mContentRes,
                        Settings.System.NAVIGATION_BAR_GLOW_TINT, -1);
                Settings.System.putInt(mContentRes,
                        Settings.System.NAVIGATION_BAR_BUTTONS_QTY, 3);

                Settings.System.putString(mContentRes,
                        Settings.System.NAVIGATION_CUSTOM_ACTIVITIES[0], "**back**");
                Settings.System.putString(mContentRes,
                        Settings.System.NAVIGATION_CUSTOM_ACTIVITIES[1], "**home**");
                Settings.System.putString(mContentRes,
                        Settings.System.NAVIGATION_CUSTOM_ACTIVITIES[2], "**recents**");

                Settings.System.putString(mContentRes,
                        Settings.System.NAVIGATION_LONGPRESS_ACTIVITIES[0], "**null**");
                Settings.System.putString(mContentRes,
                        Settings.System.NAVIGATION_LONGPRESS_ACTIVITIES[1], "**null**");
                Settings.System.putString(mContentRes,
                        Settings.System.NAVIGATION_LONGPRESS_ACTIVITIES[2], "**null**");

                Settings.System.putString(mContentRes,
                        Settings.System.NAVIGATION_CUSTOM_APP_ICONS[0], "");
                Settings.System.putString(mContentRes,
                        Settings.System.NAVIGATION_CUSTOM_APP_ICONS[1], "");
                Settings.System.putString(mContentRes,
                        Settings.System.NAVIGATION_CUSTOM_APP_ICONS[2], "");
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {

        if (preference == mNavigationBarWidth) {
            String newVal = (String) newValue;
            int p = Integer.parseInt(newVal);
            int width = percentToPixels(p);
            Settings.System.putInt(mContentRes, Settings.System.NAVIGATION_BAR_WIDTH,
                    width);
            return true;
        } else if (preference == mNavigationBarHeight) {
            String newVal = (String) newValue;
            int dp = Integer.parseInt(newVal);
            int p = Integer.parseInt(newVal);
            int height = percentToPixels(p);
            Settings.System.putInt(mContentRes, Settings.System.NAVIGATION_BAR_HEIGHT,
                    height);
            return true;
        } else if (preference == mNavigationBarHeightLandscape) {
            String newVal = (String) newValue;
            int dp = Integer.parseInt(newVal);
            int p = Integer.parseInt(newVal);
            int height = percentToPixels(p);
            Settings.System.putInt(mContentRes,
                    Settings.System.NAVIGATION_BAR_HEIGHT_LANDSCAPE,
                    height);
            return true;
        } else if (preference == mNavigationColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex) & 0x00FFFFFF;
            Settings.System.putInt(mContentRes,
                    Settings.System.NAVIGATION_BAR_COLOR, intHex);
            return true;
        } else if (preference == mNavigationBarColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mContentRes,
                    Settings.System.NAVIGATION_BAR_TINT, intHex);
            return true;
        } else if (preference == mNavigationBarGlowColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mContentRes,
                    Settings.System.NAVIGATION_BAR_GLOW_TINT, intHex);
            return true;
        } else if (preference == mGlowTimes) {
            // format is (on|off) both in MS
            String value = (String) newValue;
            String[] breakIndex = value.split("\\|");
            int onTime = Integer.valueOf(breakIndex[0]);
            int offTime = Integer.valueOf(breakIndex[1]);

            Settings.System.putInt(mContentRes,
                    Settings.System.NAVIGATION_BAR_GLOW_DURATION[0], offTime);
            Settings.System.putInt(mContentRes,
                    Settings.System.NAVIGATION_BAR_GLOW_DURATION[1], onTime);
            updateGlowTimesSummary();
            return true;
        } else if (preference == mButtonAlpha) {
            float val = Float.parseFloat((String) newValue);
            Settings.System.putFloat(mContentRes,
                    Settings.System.NAVIGATION_BAR_BUTTON_ALPHA,
                    val * 0.01f);
            return true;
        }
        return false;
    }

    private void updateGlowTimesSummary() {
        int resId;
        String combinedTime = Settings.System.getString(mContentRes,
                Settings.System.NAVIGATION_BAR_GLOW_DURATION[1]) + "|" +
                Settings.System.getString(mContentRes,
                        Settings.System.NAVIGATION_BAR_GLOW_DURATION[0]);

        String[] glowArray = getResources().getStringArray(R.array.glow_times_values);

        if (glowArray[0].equals(combinedTime)) {
            resId = R.string.glow_times_off;
            mGlowTimes.setValueIndex(0);
        } else if (glowArray[1].equals(combinedTime)) {
            resId = R.string.glow_times_superquick;
            mGlowTimes.setValueIndex(1);
        } else if (glowArray[2].equals(combinedTime)) {
            resId = R.string.glow_times_quick;
            mGlowTimes.setValueIndex(2);
        } else {
            resId = R.string.glow_times_normal;
            mGlowTimes.setValueIndex(3);
        }
        mGlowTimes.setSummary(getResources().getString(resId));
    }

    public int percentToPixels(int percent) {   
        int defNavBarSize = getResources().getDimensionPixelSize(R.dimen.navigation_bar_48);
        return (int)((float)defNavBarSize * ((float) percent * 0.01f));
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
