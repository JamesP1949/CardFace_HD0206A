package com.wis.module.fragment_compare;

import com.common.scope.FragmentScope;

import dagger.Module;
import dagger.Provides;

/**
 * Created by JamesP949 on 2017/5/24.
 * Function:
 */
@Module
public class CompareModule {
    private CompareFragment mFragment;

    public CompareModule(CompareFragment fragment) {
        mFragment = fragment;
    }

    @FragmentScope
    @Provides
    public ComparePresenter_ providePresenter() {
        return new ComparePresenter_();
    }

}
