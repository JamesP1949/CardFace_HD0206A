package com.wis.module.settings;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.common.base.BaseToolBarActivity;
import com.wis.R;

/**
 * Created by JamesP949 on 2017/5/10.
 * Function:
 */

public class SettingsActivity extends BaseToolBarActivity {
    @Override
    public int getLayoutResId() {
        return R.layout.activity_settings;
    }

    @Override
    protected void injectDagger() {
        // do nothing...

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setToolbarTitle(R.string.action_settings);
        setToolbarIndicator(true);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction beginTransaction = fragmentManager.beginTransaction();
        beginTransaction.replace(R.id.frame_content, new SettingsFragment());
        beginTransaction.commit();

    }
}
