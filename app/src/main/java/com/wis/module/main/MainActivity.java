package com.wis.module;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.common.base.BaseToolBarActivity;
import com.common.rx.SchedulersCompat;
import com.common.utils.DisplayUtils;
import com.socks.library.KLog;
import com.wis.R;
import com.wis.application.App;
import com.wis.application.AppCore;
import com.wis.module.query.QueryActivity;
import com.wis.module.settings.SettingsActivity;
import com.wis.sdcard.CopyFileToSD;
import com.wis.utils.GlobalConstant;

import javax.inject.Inject;

import butterknife.Bind;
import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public class MainActivity extends BaseToolBarActivity {

    @Bind(R.id.tv_tips)
    TextView mTvTips;
    @Bind(R.id.progress_rl)
    RelativeLayout mProgressRl;

    @Inject
    App mApp;

    CompositeDisposable mCompositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        setToolbarIndicator(false);
        setToolbarTitle(R.string.app_name);
        menuId = R.menu.menu_main;
        KLog.e(DisplayUtils.getScreenRatio(this));

        mCompositeDisposable = new CompositeDisposable();
        mProgressRl.setVisibility(View.VISIBLE);
        mTvTips.setText("初始化比对模型,请稍后...");
        Disposable disposable = Observable.just(new Object())
                .map(new Function<Object, Boolean>() {
                    @Override
                    public Boolean apply(@NonNull Object o) throws Exception {
                        return CopyFileToSD.initDB(MainActivity.this);
                    }
                })
                .compose(SchedulersCompat.<Boolean>applyObservable_IoSchedulers())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(@NonNull Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            mProgressRl.setVisibility(View.GONE);
                            Intent intent = new Intent(GlobalConstant.Action_Module_Init);
                            intent.putExtra(GlobalConstant.Action_Init_MSg, true);
                            LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast
                                    (intent);
                            mApp.bindService();
                        } else {
                            showToast("比对模型初始化失败");
                            finish();
                        }
                    }
                });
        mCompositeDisposable.add(disposable);
    }


    @Override
    public int getLayoutResId() {
        return R.layout.activity_main_;
    }

    @Override
    protected void injectDagger() {
        AppCore.getAppComponent().inject(this);
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
        mApp.setPauseReader();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mApp.isReaderPaused()) {
            mApp.setReStartReader();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCompositeDisposable.size() > 0 && !mCompositeDisposable.isDisposed())
            mCompositeDisposable.dispose();
        mApp.unBindService();
    }
}
