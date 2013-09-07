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

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Resources;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.util.Log;

import com.carbon.settings.R;

// import htc one stuffs
import com.carbon.settings.device.htc.*;
// import the other stuffs

import java.util.ArrayList;

public class DeviceTools extends FragmentActivity {

    private static final String TAG = "DeviceTools";

    public static final String SHARED_PREFERENCES_BASENAME = "com.carbon.settings.device";
    public static final String ACTION_UPDATE_PREFERENCES = "com.carbon.settings.device.UPDATE";

    PagerTabStrip mPagerTabStrip;
    ViewPager mViewPager;

    boolean isHtcOne;
    String titleString[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pager_tab);

        isHtcOne = getResources().getBoolean(R.bool.is_htc_one);

        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mPagerTabStrip = (PagerTabStrip) findViewById(R.id.pagerTabStrip);
        TitleAdapter titleAdapter = new TitleAdapter(getFragmentManager());
        mViewPager.setAdapter(titleAdapter);
        mViewPager.setCurrentItem(0);
        mPagerTabStrip.setDrawFullUnderline(true);
        mPagerTabStrip.setTabIndicatorColor(getResources().getColor(android.R.color.holo_blue_light));

        final ActionBar bar = getActionBar();
        bar.setDisplayShowTitleEnabled(false);

    }

    class TitleAdapter extends FragmentPagerAdapter {
        String titles[] = getTitles();
        private Fragment frags[] = new Fragment[titles.length];

        public TitleAdapter(FragmentManager fm) {
            super(fm);
            // Display for certain devices only
            if (isHtcOne) {
                frags[0] = new ButtonLightFragmentActivity();
                frags[1] = new SensorsFragmentActivity();
                frags[2] = new TouchscreenFragmentActivity();
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public Fragment getItem(int position) {
            return frags[position];
        }

        @Override
        public int getCount() {
            return frags.length;
        }
    }

    private String[] getTitles() {
        if (isHtcOne) {
            titleString = new String[]{
                    getResources().getString(R.string.category_buttonlight_title),
                    getResources().getString(R.string.category_sensors_title),
                    getResources().getString(R.string.category_touchscreen_title)};
        }
        return titleString;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            DeviceTools.this.onBackPressed();
        default:
            return super.onOptionsItemSelected(item);
        }
    }
}
