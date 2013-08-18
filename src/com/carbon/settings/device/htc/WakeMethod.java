package com.carbon.settings.device.htc;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceManager;

import com.carbon.settings.device.DeviceUtils;

public class WakeMethod implements OnPreferenceChangeListener {

    private static final String FILE_H2W = "/sys/android_touch/home2wake";
    private static final String FILE_S2W = "/sys/android_touch/sweep2wake";
    private static final String FILE_DT2W = "/sys/android_touch/doubletap2wake";

    private static final String METHOD_NONE = "0";
    private static final String METHOD_HOME = "1";
    private static final String METHOD_LOGO = "2";
    private static final String METHOD_SWEEP = "3";
    private static final String METHOD_DOUBLETAP = "4";

    public static boolean isSupported() {
        return DeviceUtils.fileExists(FILE_H2W) && DeviceUtils.fileExists(FILE_S2W) && DeviceUtils.fileExists(FILE_DT2W);
    }

    private static void setSysFsForMethod(String method)
    {
        if (method.equals(METHOD_NONE))
        {
             DeviceUtils.writeValue(FILE_H2W, "0\n");
             DeviceUtils.writeValue(FILE_S2W, "0\n");
             DeviceUtils.writeValue(FILE_DT2W, "0\n");
        } else
        if (method.equals(METHOD_HOME))
        {
             DeviceUtils.writeValue(FILE_H2W, "2\n");
             DeviceUtils.writeValue(FILE_S2W, "0\n");
             DeviceUtils.writeValue(FILE_DT2W, "0\n");
        } else
        if (method.equals(METHOD_LOGO))
        {
             DeviceUtils.writeValue(FILE_H2W, "3\n");
             DeviceUtils.writeValue(FILE_S2W, "0\n");
             DeviceUtils.writeValue(FILE_DT2W, "0\n");
        } else
        if (method.equals(METHOD_SWEEP))
        {
             DeviceUtils.writeValue(FILE_H2W, "2\n");
             DeviceUtils.writeValue(FILE_S2W, "1\n");
             DeviceUtils.writeValue(FILE_DT2W, "0\n");
        }
        if (method.equals(METHOD_DOUBLETAP))
        {
             DeviceUtils.writeValue(FILE_H2W, "0\n");
             DeviceUtils.writeValue(FILE_S2W, "0\n");
             DeviceUtils.writeValue(FILE_DT2W, "1\n");
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
        String method = sharedPrefs.getString(TouchscreenFragmentActivity.KEY_WAKE_METHOD, "0");
        setSysFsForMethod(method);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        setSysFsForMethod((String)newValue);
        return true;
    }

}
