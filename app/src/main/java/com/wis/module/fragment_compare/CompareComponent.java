package com.wis.module.fragment_compare;

import com.common.scope.FragmentScope;
import com.wis.application.AppComponent;

import dagger.Component;

/**
 * Created by JamesP949 on 2017/5/24.
 * Function:
 */
@FragmentScope
@Component(dependencies = AppComponent.class, modules = CompareModule.class)
public interface CompareComponent {
    void inject(CompareFragment fragment);
}
