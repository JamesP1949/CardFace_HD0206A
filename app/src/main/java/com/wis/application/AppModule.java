package com.wis.application;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import com.common.cache.WeakMemoryCache;
import com.common.scope.AppScope;
import com.wis.bean.DaoManager;
import com.wis.config.UserConfig;
import com.wis.face.WisMobile;

import dagger.Module;
import dagger.Provides;

/**
 * Created by JamesP949 on 2017/5/24.
 * Function:
 */
@Module
public class AppModule {
    private App mApp;

    public AppModule(App app) {
        mApp = app;
    }

    @AppScope
    @Provides
    public App provideApplication() {
        return mApp;
    }

    @AppScope
    @Provides
    public DaoManager provideDaoManager() {
        return DaoManager.getInstance(mApp);
    }

    @AppScope
    @Provides
    public UserConfig provideUserConfig() {
        return UserConfig.getInstance(mApp);
    }

    @AppScope
    @Provides
    public SharedPreferences provideDefaultPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(mApp);
    }

    @AppScope
    @Provides
    public WisMobile provideWisMobile() {
        return new WisMobile();
    }

    @AppScope
    @Provides
    public WeakMemoryCache provideWeakMemoryCache() {
        return new WeakMemoryCache();
    }

    @AppScope
    @Provides
    public Resources provideResources() {
        return mApp.getResources();
    }
}
