package com.common.core;

import android.app.Application;

import com.common.scope.CoreScope;

import dagger.Component;

/**
 * Created by JamesP949 on 2017/5/24.
 * Function:
 */
@CoreScope
@Component(modules = CoreModule.class)
public interface CoreComponent {
    Application application();
}
