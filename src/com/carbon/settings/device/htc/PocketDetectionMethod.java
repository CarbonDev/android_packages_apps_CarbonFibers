package com.carbon.settings.device.htc;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceManager;

import com.carbon.settings.device.DeviceUtils;

public class PocketDetectionMethod implements OnPreferenceChangeListener {

    private static final String FILE = "/sys/android_touch/pocket_detect";

    private static final String METHOD_NONE = "0";
    private static final String METHOD_DARK = "1";
    private static final String METHOD_NO_DARK = "2";

    public static boolean isSupported() {
        return DeviceUtils.fileExists(FILE);
    }

    private static void setSysFsForMethod(String method)
    {
        if (method.equals(METHOD_NONE))
        {
             DeviceUtils.writeValue(FILE, "0\n");
        } else
        if (method.equals(METHOD_DARK))
        {
             DeviceUtils.writeValue(FILE, "1\n");
        } else
        if (method.equals(METHOD_NO_DARK))
        {
             DeviceUtils.writeValue(FILE, "2\n");
        }
    }

    /**
     * Restore WakeMethod setting from SharedPreferences. (Write to kernel.)
     * @param context       The context to read the SharedPreferences from
     */
    public static void restore(Context context) {
        if (!isSupported()) {
            return;
        }

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        String method = sharedPrefs.getString(SensorsFragmentActivity.KEY_POCKETDETECTION_METHOD, "1");
        setSysFsForMethod(method);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        setSysFsForMethod((String)newValue);
        return true;
    }

}
