package com.common.base;

import android.support.annotation.StringRes;

/**
 * Created by JamesP949 on 2017/3/27.
 * Function:
 */

public interface IView {
    void showToast(String msg);
    void showToast(@StringRes int resId);
    void showLongToast(String msg);
    void showLongToast(@StringRes int resId);
}
