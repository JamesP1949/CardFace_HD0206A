package com.wis.application;

import android.content.SharedPreferences;

import com.common.core.CoreComponent;
import com.common.scope.AppScope;
import com.wis.bean.DaoManager;
import com.wis.config.UserConfig;
import com.wis.module.fragment_compare.ComparePresenter_;
import com.wis.module.main.MainPresenter;
import com.wis.module.query.QueryPresenter;
import com.wis.module.settings.SettingsFragment;
import com.wis.utils.CopyFileToSD;
import com.wis.widget.CameraPreview_;

import dagger.Component;

/**
 * Created by JamesP949 on 2017/5/24.
 * Function:
 */
@AppScope
@Component(dependencies = CoreComponent.class, modules = AppModule.class)
public interface AppComponent {
    void inject(App app);

    void inject(CopyFileToSD copyFileToSD);

    void inject(MainPresenter mainPresenter);

    void inject(ComparePresenter_ comparePresenter_);

    void inject(QueryPresenter queryPresenter);

    void inject(CameraPreview_ cameraPreview_);

    void inject(SettingsFragment settingsFragment);

    App getApplication();

    DaoManager getDaoManager();

    UserConfig getUserConfig();

    SharedPreferences getDefaultPreferences();
}
