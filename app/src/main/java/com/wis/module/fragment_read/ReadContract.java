package com.wis.module.fragment_read;

import com.common.base.IPresenter;
import com.common.base.IView;

/**
 * Created by JamesP949 on 2017/4/27.
 * Function:
 */

public interface ReadContract {
    interface View extends IView {
        void register();
        void reRead();
        void clearUI();

    }
    interface Presenter extends IPresenter<View> {
        /*void display(IDCard idCard);
        void open();
        void close();*/
    }
}
