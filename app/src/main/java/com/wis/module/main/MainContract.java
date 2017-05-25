package com.wis.module.main;

import com.common.base.IPresenter;
import com.common.base.IView;

/**
 * Created by JamesP949 on 2017/5/24.
 * Function:
 */

public interface MainContract {
    interface View extends IView {
        void showProgressView();
        void closeProgressView();
        void update(boolean isInit);
    }

    interface Presenter extends IPresenter<View> {
        void init();
        void bindService();
        void unbindService();
        void pauseReader();
        void reStartReader();
    }
}
