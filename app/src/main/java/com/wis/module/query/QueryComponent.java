package com.wis.module.query;

import com.common.scope.ActivityScope;
import com.wis.application.AppComponent;

import dagger.Component;

/**
 * Created by JamesP949 on 2017/5/24.
 * Function:
 */
@ActivityScope
@Component(dependencies = AppComponent.class, modules = QueryModule.class)
public interface QueryComponent {
    void inject(QueryActivity activity);
}
