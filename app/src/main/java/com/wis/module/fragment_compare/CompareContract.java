package com.wis.module.fragment_compare;

import android.graphics.drawable.BitmapDrawable;

import com.common.base.IPresenter;
import com.common.base.IView;
import com.wis.bean.Compare;

/**
 * Created by JamesP949 on 2017/4/27.
 * Function:
 */

public interface CompareContract {
    interface View extends IView {
        void registerBroadcast();
        void getCountdownInterval();
        void getC_threshold();
        void start();
//        void reStart();
        void updateCD(int arg1);  // 刷新计时数据
        void updateUI(boolean isSucceed, Compare cropBitmap);
        void clearUI();
        BitmapDrawable takePicture_();
        // 转换人脸在图片中的坐标
        void convertCoordinate(int ret_x, int ret_y, int ret_width, int ret_height, int
                bitmapWidth, int bitmapHeight);
        // 闪光灯控制
        void doWriteFile_led(boolean openFlashLight);
    }

    interface Presenter extends IPresenter<CompareContract.View> {
        void compare(float threshold);
        void startCountDown(int countInterval);
        void saveInDB(Compare compare);
    }
}
