package com.common.core;

import android.app.Application;

/**
 * Created by JamesP949 on 2017/5/24.
 * Function:
 */

public final class CommonCore {
    private static CoreComponent coreComponent;

    public CommonCore() throws IllegalAccessException {
        throw new IllegalAccessException("can not new instance!");
    }

    public static void init(Application application) {
        if (coreComponent == null) {
            coreComponent = DaggerCoreComponent
                    .builder()
                    .coreModule(new CoreModule(application))
                    .build();
        }
    }

    public static CoreComponent getCoreComponent() {
        if (coreComponent == null) {
            throw new IllegalStateException("Common core need init at application");
        }
        return coreComponent;
    }
}
