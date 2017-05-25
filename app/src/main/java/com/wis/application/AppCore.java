package com.wis.application;

import com.common.core.CommonCore;

/**
 * Created by JamesP949 on 2017/5/24.
 * Function:
 */

public class AppCore {
    private static AppComponent appComponent;

    public AppCore() throws IllegalAccessException {
        throw new IllegalAccessException("can not new instance!");
    }

    public static void init(App application) {
        if (appComponent == null) {
            CommonCore.init(application);
            appComponent = DaggerAppComponent
                    .builder()
                    .coreComponent(CommonCore.getCoreComponent())
                    .appModule(new AppModule(application))
                    .build();
            appComponent.inject(application);
        }
    }

    public static AppComponent getAppComponent() {
        if (appComponent == null) {
            throw new IllegalStateException("Common core need init at application");
        }
        return appComponent;
    }
}
