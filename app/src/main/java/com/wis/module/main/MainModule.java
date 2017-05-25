package com.wis.module.main;

import com.common.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;

/**
 * Created by JamesP949 on 2017/5/24.
 * Function:
 */
@Module
public class MainModule {
    @ActivityScope
    @Provides
    public MainPresenter providePresenter() {
        return new MainPresenter();
    }
}
