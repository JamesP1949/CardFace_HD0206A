package com.common.core;

import android.app.Application;

import com.common.scope.CoreScope;

import dagger.Module;
import dagger.Provides;

/**
 * Created by JamesP949 on 2017/5/24.
 * Function:
 */
@CoreScope
@Module
public class CoreModule {
    private Application appContext;

    public CoreModule(Application appContext) {
        this.appContext = appContext;
    }

    @CoreScope
    @Provides
    public Application provideApplication() {
        return appContext;
    }
}
