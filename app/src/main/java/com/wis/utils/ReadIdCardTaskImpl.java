package com.wis.utils;

import com.socks.library.KLog;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by JamesP949 on 2017/2/23.
 * Function: 开启定时执行读取身份证信息的任务
 */

public class ReadIdCardTaskImpl extends TimerTask {
    private long m;
    private Timer mTimer;

    public ReadIdCardTaskImpl(long interval) {
        m = interval;
    }

    @Override
    public void run() {
        GlobalConstant.timeReadFlag = true;
        KLog.e("run烦烦烦：：" + System.currentTimeMillis());
    }

    public void start() {
        KLog.e("jishiqi即时开始---" + m);
        mTimer = new Timer();
        mTimer.schedule(this, 0, m);
    }

    public void stop() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
            mTimer = null;
            KLog.e("停止计时");
        }
    }

}
