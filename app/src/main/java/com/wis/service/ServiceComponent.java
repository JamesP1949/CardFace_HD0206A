package com.wis.service;

import com.common.scope.ServiceScope;
import com.wis.application.AppComponent;

import dagger.Component;

/**
 * Created by JamesP949 on 2017/5/25.
 * Function:
 */
@ServiceScope
@Component(dependencies = AppComponent.class, modules = ServiceModule.class)
public interface ServiceComponent {
    void inject(WorkService workService);
}
