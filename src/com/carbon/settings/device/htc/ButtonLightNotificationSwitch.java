package com.carbon.settings.device.htc;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceManager;

import com.carbon.settings.device.DeviceUtils;

public class ButtonLightNotificationSwitch implements OnPreferenceChangeListener {

    private static final String FILE = "/sys/class/leds/button-backlight/blink_buttons";

    public static boolean isSupported() {
        return DeviceUtils.fileExists(FILE);
    }

    /**
     * Restore ButtonLightNotification setting from SharedPreferences. (Write to kernel.)
     * @param context       The context to read the SharedPreferences from
     */
    public static void restore(Context context) {
        if (!isSupported()) {
            return;
        }

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean enabled = sharedPrefs.getBoolean(ButtonLightFragmentActivity.KEY_BUTTONLIGHTNOTIFICATION_SWITCH, false);
        if(enabled)
            DeviceUtils.writeValue(FILE, "1\n");
        else
            DeviceUtils.writeValue(FILE, "0\n");
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Boolean enabled = (Boolean) newValue;
        if(enabled)
            DeviceUtils.writeValue(FILE, "1\n");
        else
            DeviceUtils.writeValue(FILE, "0\n");
        return true;
    }

}
