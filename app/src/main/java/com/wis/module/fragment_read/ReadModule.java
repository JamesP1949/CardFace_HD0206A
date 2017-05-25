package com.wis.module.fragment_read;

import com.common.scope.FragmentScope;

import dagger.Module;
import dagger.Provides;

/**
 * Created by JamesP949 on 2017/5/24.
 * Function:
 */
@Module
public class ReadModule {
    @FragmentScope
    @Provides
    public ReadPresenter providePresenter() {
        return new ReadPresenter();
    }
}
