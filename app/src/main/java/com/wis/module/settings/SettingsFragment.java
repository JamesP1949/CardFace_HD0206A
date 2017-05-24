package com.wis.module.settings;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

import com.wis.R;
import com.wis.application.App;
import com.wis.utils.GlobalConstant;

/**
 * Created by JamesP949 on 2017/5/10.
 * Function:
 */

public class SettingsFragment extends PreferenceFragment implements Preference
        .OnPreferenceChangeListener, Preference.OnPreferenceClickListener {
    String readKey, timeKey, thresholdKey;
    ListPreference mReadPreference;
    ListPreference mTimePreference; // 比对时长
    ListPreference mThresholdPreference; // 比对阈值

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference_settings);
        readKey = getString(R.string.pref_key_read);
        timeKey = getString(R.string.pref_key_comparison_time);
        thresholdKey = getString(R.string.pref_key_comparison_threshold);
        mReadPreference = (ListPreference) findPreference(readKey);
        mTimePreference = (ListPreference) findPreference(timeKey);
        mThresholdPreference = (ListPreference) findPreference(thresholdKey);
        mReadPreference.setOnPreferenceChangeListener(this);
        mReadPreference.setOnPreferenceClickListener(this);
        mTimePreference.setOnPreferenceChangeListener(this);
        mTimePreference.setOnPreferenceClickListener(this);
        mThresholdPreference.setOnPreferenceChangeListener(this);
        mThresholdPreference.setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference.getKey().endsWith(readKey)) {
            App.getInstance().setTimeIntervalChanged();
            return true;
        } else if (preference.getKey().endsWith(timeKey)) {
            GlobalConstant.countdownFlag = true;
            return true;
        } else if (preference.getKey().endsWith(thresholdKey)) {
            GlobalConstant.thresholdFlag = true;
            return true; // 保存更改后的值
        }
        return false; // 不保存更改后的值
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
       /* if (preference.getKey().endsWith(readKey)) {
            KLog.e("card");
            return true;
        } else if (preference.getKey().endsWith(timeKey)) {
            return true;
        } else if (preference.getKey().endsWith(thresholdKey)) {
            return true;
        }
        return false;*/
        return true;
    }
}
