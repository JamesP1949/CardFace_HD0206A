package com.wis.module.settings;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

import com.socks.library.KLog;
import com.wis.R;
import com.wis.application.App;
import com.wis.application.AppCore;
import com.wis.config.UserConfig;
import com.wis.face.WisMobile;
import com.wis.utils.GlobalConstant;

import javax.inject.Inject;

import io.reactivex.schedulers.Schedulers;

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
    @Inject
    App mApp;
    /* ***测试用****/
    @Inject
    WisMobile mWisMobile;
    @Inject
    UserConfig mUserConfig;
    /* ***测试用****/

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCore.getAppComponent().inject(this);
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
        init();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference.getKey().endsWith(readKey)) {
            mApp.setTimeIntervalChanged();
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

    // 测试
    private void init() {
        Schedulers.newThread().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                String dirPath = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                        .getAbsolutePath();
                Bitmap bitmap = BitmapFactory.decodeFile(dirPath + "/0.jpg");
                float[] floats = mWisMobile.detectFace(bitmap);
                float[] idFloat = mUserConfig.getFaceFeature();
                float feature = mWisMobile.compare2Feature(floats, idFloat);
                KLog.e("比对结果：" + feature);
            }
        });
    }
}
