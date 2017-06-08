package com.wis.module.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.common.base.BaseToolBarActivity;
import com.wis.R;
import com.wis.application.AppCore;
import com.wis.module.query.QueryActivity;
import com.wis.module.settings.SettingsActivity;
import com.wis.utils.GlobalConstant;

import javax.inject.Inject;

import butterknife.Bind;

public class MainActivity extends BaseToolBarActivity<MainPresenter> implements MainContract.View {

    @Bind(R.id.tv_tips)
    TextView mTvTips;
    @Bind(R.id.progress_rl)
    RelativeLayout mProgressRl;

    @Inject
    MainPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        setToolbarIndicator(false);
        setToolbarTitle(R.string.app_name);
        menuId = R.menu.menu_main;
//        KLog.e(DisplayUtils.getScreenRatio(this));
        mPresenter.init();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.activity_main_;
    }

    @Override
    protected void injectDagger() {
        DaggerMainComponent.builder()
                .appComponent(AppCore.getAppComponent())
                .mainModule(new MainModule())
                .build()
                .inject(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_query) {
            Intent intent = new Intent(this, QueryActivity.class);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.pauseReader();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.reStartReader();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.unbindService();
    }

    @Override
    public void showProgressView() {
        mProgressRl.setVisibility(View.VISIBLE);
        mTvTips.setText("初始化比对模型,请稍后...");
    }

    @Override
    public void closeProgressView() {
        mProgressRl.setVisibility(View.GONE);
    }

    @Override
    public void update(boolean isInit) {
        if (isInit) {
            Intent intent = new Intent(GlobalConstant.Action_Module_Init);
            intent.putExtra(GlobalConstant.Action_Init_MSg, true);
            LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast
                    (intent);
            mPresenter.bindService();
        } else {
            showToast("比对模型初始化失败");
            finish();
        }
    }
}
