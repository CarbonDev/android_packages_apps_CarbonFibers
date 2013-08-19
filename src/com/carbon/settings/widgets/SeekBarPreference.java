package com.carbon.settings.widgets;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.carbon.settings.R;

import android.provider.Settings;

public class SeekBarPreference extends Preference
        implements OnSeekBarChangeListener {

    public static int maximum = 100;
    public static int interval = 1;

    private String property;

    private TextView monitorBox;
    private SeekBar bar;

    int defaultValue = 60;
    int progress;
    boolean mDisablePercentageValue = false;

    private OnPreferenceChangeListener changer;

    public SeekBarPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected View onCreateView(ViewGroup parent) {

        View layout = View.inflate(getContext(), R.layout.slider_preference, null);

        monitorBox = (TextView) layout.findViewById(R.id.monitor_box);
        bar = (SeekBar) layout.findViewById(R.id.seek_bar);
        try{
         progress = (int) (Settings.System.getFloat(getContext().getContentResolver(), property) * 100);
    }
    catch (Exception e)
        {
        progress = defaultValue;
    }
        bar.setOnSeekBarChangeListener(this);
        bar.setProgress(progress);
        monitorBox.setText(progress + "%");
        return layout;
    }

    public void setInitValue(int Progress) {
        progress = Progress;
        defaultValue = progress;
        if (bar!=null)
        {
            bar.setProgress(progress);
            monitorBox.setText(progress + "%");
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        // TODO Auto-generated method stub
        return super.onGetDefaultValue(a, index);
    }

    @Override
    public void setOnPreferenceChangeListener(OnPreferenceChangeListener onPreferenceChangeListener) {
        changer = onPreferenceChangeListener;
        super.setOnPreferenceChangeListener(onPreferenceChangeListener);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int Progress, boolean fromUser) {
        progress = Progress;
        progress = Math.round(((float) progress) / interval) * interval;
        seekBar.setProgress(progress);

        monitorBox.setText(progress + "%");    }

    public void setValue(int Progress){
        progress = Progress;
        if (bar!=null)
        {
            bar.setProgress(progress);
            monitorBox.setText(progress + "%");
            changer.onPreferenceChange(this, Integer.toString(progress));
        }
    }

    public void disablePercentageValue(boolean disable) {
        mDisablePercentageValue = disable;
    }

    public void setProperty(String property){
        this.property = property;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {  
        changer.onPreferenceChange(this, Integer.toString(progress));
    }
}
