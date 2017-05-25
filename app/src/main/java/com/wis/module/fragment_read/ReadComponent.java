package com.wis.module.fragment_read;

import com.common.scope.FragmentScope;
import com.wis.application.AppComponent;

import dagger.Component;

/**
 * Created by JamesP949 on 2017/5/24.
 * Function:
 */
@FragmentScope
@Component(modules = ReadModule.class, dependencies = AppComponent.class)
public interface ReadComponent {
    void inject(ReadFragment readFragment);
}
