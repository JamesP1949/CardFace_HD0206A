package com.wis.service;

import com.common.scope.ServiceScope;

import cn.com.aratek.idcard.IDCardReader;
import dagger.Module;
import dagger.Provides;

/**
 * Created by JamesP949 on 2017/5/25.
 * Function:
 */
@Module
public class ServiceModule {
    private WorkService mWorkService;

    public ServiceModule(WorkService workService) {
        mWorkService = workService;
    }

    @ServiceScope
    @Provides
    public IDCardReader provideIDCardReader() {
        return IDCardReader.getInstance();
    }
}
