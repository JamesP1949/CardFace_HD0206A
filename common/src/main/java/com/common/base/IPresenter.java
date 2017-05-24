package com.common.base;

/**
 * Created by JamesP949 on 2017/3/27.
 * Function:
 */

public interface IPresenter<T extends IView> {
    void attachView(T view);
    void detachView();
}
