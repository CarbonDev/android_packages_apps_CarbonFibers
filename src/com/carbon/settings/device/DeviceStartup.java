/*
 * Copyright (C) 2012-2013 The CyanogenMod Project
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

package com.carbon.settings.device;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.carbon.settings.device.htc.ButtonLightFragmentActivity;
import com.carbon.settings.device.htc.Logo2MenuSwitch;
import com.carbon.settings.device.htc.WakeMethod;
import com.carbon.settings.device.htc.LongTapLogoSleepSwitch;
import com.carbon.settings.device.htc.ButtonLightNotificationSwitch;
import com.carbon.settings.device.htc.PocketDetectionMethod;
import com.carbon.settings.device.htc.SensorsFragmentActivity;
import com.carbon.settings.device.htc.TouchscreenFragmentActivity;

public class DeviceStartup extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent bootintent) {
        // HTC One
        SensorsFragmentActivity.restore(context);
        TouchscreenFragmentActivity.restore(context);
        Logo2MenuSwitch.restore(context);
        WakeMethod.restore(context);
        LongTapLogoSleepSwitch.restore(context);
        ButtonLightFragmentActivity.restore(context);
        ButtonLightNotificationSwitch.restore(context);
        PocketDetectionMethod.restore(context);

        // Samsung
          // nothing for now
    }
}
