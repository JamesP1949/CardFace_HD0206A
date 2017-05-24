package com.wis.application;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.common.cache.LruMemoryCache;
import com.common.cache.WeakMemoryCache;
import com.common.safety_crash.SafetyCrashHandler;
import com.socks.library.KLog;
import com.wis.BuildConfig;
import com.wis.bean.DaoManager;
import com.wis.config.AppConfig;
import com.wis.config.SettingConfig;
import com.wis.config.UserConfig;
import com.wis.face.WisMobile;
import com.wis.service.WorkService;

/**
 * Created by JamesP949 on 2017/4/28.
 * Function:
 */

public class App extends Application {
    private static App mInstance;
    public static boolean isConnect;
    private static boolean flag;
    private WisMobile wisMobile;
    private UserConfig mUserConfig;
    private SettingConfig mSettingConfig;
    private DaoManager mDaoManager;
    private LruMemoryCache mLruMemoryCache;
    private WeakMemoryCache mWeakMemoryCache;
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        SafetyCrashHandler.getInstance().init(this);
        mUserConfig = UserConfig.getInstance(this);
        mSettingConfig = SettingConfig.getInstance(this);
        mDaoManager = DaoManager.getInstance(this);
        mLruMemoryCache = new LruMemoryCache(16 * 1024 * 1024);
        mWeakMemoryCache = new WeakMemoryCache();
        KLog.init(BuildConfig.LOG_DEBUG, "JamesP1949");
    }

    public static App getInstance() {
        return mInstance;
    }

    public UserConfig getUserConfig() {
        return mUserConfig;
    }

    public SettingConfig getSettingConfig() {
        return mSettingConfig;
    }

    public DaoManager getDaoManager() {
        return mDaoManager;
    }

    public LruMemoryCache getLruMemoryCache() {
        return mLruMemoryCache;
    }

    public WeakMemoryCache getWeakMemoryCache() {
        return mWeakMemoryCache;
    }

    public void loadWisMobile() {
        wisMobile = new WisMobile();
        wisMobile.setNumThreads(2);
        wisMobile.loadModel(AppConfig.ModelPath);
        // 机器码 每个机器一个
        String SerialNo = wisMobile.getSerialNo();
        KLog.e("SerialNo:" + SerialNo);
    }

    public WisMobile getWisMobile() {
        return wisMobile;
    }

    private WorkService.MyBinder mBinder;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinder = (WorkService.MyBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBinder = null;
        }
    };

    // 绑定服务
    public void bindService() {
        isConnect = true;
        flag = true;
        Intent intent = new Intent(mInstance, WorkService.class);
        mInstance.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        KLog.e("调用了bindService方法");
    }

    // 解绑服务
    public void unBindService() {
        if (flag == true) {
            flag = false;
            isConnect = false;
            mInstance.unbindService(mServiceConnection);
            KLog.e("调用了unBind方法");
        }
    }

    public void setReStartReader() {
        if (mBinder != null) {
            KLog.e("setReStartReader=---1");
            mBinder.setReStart();
        }
    }

    public void setPauseReader() {
        if (mBinder != null) {
            mBinder.setPause();
        }
    }

    public boolean isReaderPaused() {
        if (mBinder != null) {
            return mBinder.isPaused();
        }
        return false;
    }

    public void setTimeIntervalChanged() {
        if (mBinder != null)
            mBinder.setReadTaskIntervalChanged();
    }
}
