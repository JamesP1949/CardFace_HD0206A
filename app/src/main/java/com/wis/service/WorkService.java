package com.wis.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.common.utils.FileUtils;
import com.socks.library.KLog;
import com.wis.R;
import com.wis.application.App;
import com.wis.application.AppCore;
import com.wis.config.UserConfig;
import com.wis.face.WisMobile;
import com.wis.utils.GlobalConstant;
import com.wis.utils.ReadIdCardTaskImpl;

import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.inject.Inject;

import cn.com.aratek.idcard.IDCard;
import cn.com.aratek.idcard.IDCardReader;
import cn.com.aratek.util.Result;

/**
 * Created by JamesP949 on 2017/4/27.
 * Function:
 */

public class WorkService extends Service {
    public MyBinder mBinder = new MyBinder();
    private WorkThread mThread;
    private ReadIdCardTaskImpl mTask;
    private Object mServiceLock = new Object();
    private boolean isOpen; // 读卡器设备是否打开
    private boolean isReadSuc;
    private boolean entranceFlag = true;
    private boolean readTaskFlag = false;
    private boolean isReadIntervalChanged = false;
    private long readInterval;

    @Inject
    IDCardReader mReader;
    @Inject
    SharedPreferences mDefaultSharedPreferences;
    @Inject
    WisMobile mWisMobile;
    @Inject
    UserConfig mUserConfig;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        injectDagger();
        String intervalStr = mDefaultSharedPreferences.getString(getString(R.string
                .pref_key_read), "2");
        readInterval = Long.parseLong(intervalStr) * 1000; // 转换为毫秒
        mThread = new WorkThread();
        mThread.start();
    }

    private void injectDagger() {
        DaggerServiceComponent.builder()
                .appComponent(AppCore.getAppComponent())
                .serviceModule(new ServiceModule(this))
                .build()
                .inject(this);
    }

    public class WorkThread extends Thread {
        int count = 0;

        @Override
        public void run() {
            super.run();
            synchronized (mServiceLock) {
                Intent intent = new Intent(GlobalConstant.Action_READ);
                while (App.isConnect && !isOpen) {
                    KLog.e("开始连接读卡器：" + count);
                    int open = mReader.open();
                    KLog.e("result：" + open);
                    if (count < 5) {
                        if (open == IDCardReader.RESULT_OK) {
                            isOpen = true;
                            KLog.e("读卡器连接成功！");
                            intent.putExtra(GlobalConstant.Action_MSG, GlobalConstant.OPEN_SUC);
                            LocalBroadcastManager.getInstance(WorkService.this).sendBroadcast
                                    (intent);
                        } else {
                            try {
                                Thread.sleep(200);
                                continue;
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    } else if (count == 5) {
                        App.isConnect = false;
                        intent.putExtra(GlobalConstant.Action_MSG, open);
                        LocalBroadcastManager.getInstance(WorkService.this).sendBroadcast(intent);
                    }
                    count++;
                }

                while (App.isConnect && isOpen) {
                    if (entranceFlag) {
                        if (isReadSuc) {
                            GlobalConstant.timeReadFlag = false;
                            entranceFlag = false;
                            readTaskFlag = false;
                            if (mTask != null)
                                mTask.stop();
                            continue;
                        }

                        if (!isReadSuc && !readTaskFlag) {
                            readTaskFlag = true;
                            mTask = new ReadIdCardTaskImpl(readInterval);
                            mTask.start();
                        }

                        if (GlobalConstant.timeReadFlag) {
                            Result read = mReader.read();
                            if (read.error == IDCardReader.RESULT_OK) {
                                isReadSuc = true;
                                transform2Person(read);
                            }
                            intent.putExtra(GlobalConstant.Action_MSG, read.error);
                            LocalBroadcastManager.getInstance(WorkService.this).sendBroadcast
                                    (intent);
                        }
                    } else continue;

                }
            }
        }
    }

    private void transform2Person(final Result result) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd", Locale.CHINA);
        IDCard card = (IDCard) result.data;
        String validate = df.format(card.getValidFrom()) + " - " + (card.getValidTo() == null ?
                getString(R.string.long_term) : df.format(card.getValidTo()));
        /**
         * 身份证号且有效期一样才能证明是同一个人且身份证没有重新办理过
         * 只要有一个不一样就覆盖UserConfig中的信息
         */
        if (!mUserConfig.getIdNum().equals(card.getNumber())
                || !mUserConfig.getValidDate().equals(validate)) {
            mUserConfig.setName(card.getName());
            mUserConfig.setSex(card.getSex().toString());
            mUserConfig.setNation(card.getNationality().toString());
            mUserConfig.setBirthday(df.format(card.getBirthday()));
            mUserConfig.setAddress(card.getAddress());
            mUserConfig.setIdNum(card.getNumber());
            mUserConfig.setOffice(card.getAuthority());
            mUserConfig.setValidDate(validate);
            if (card.getPhoto() != null) {
                String bitmap2File = FileUtils.saveBitmap2File(WorkService.this,
                        card.getName(), card.getPhoto());
                mUserConfig.setImagePath(bitmap2File);
                float[] detectFace = mWisMobile.detectFace(card.getPhoto());
                mUserConfig.setFaceFeature(detectFace);
                KLog.e("CardInfo：" + bitmap2File);
            }
        }

        Intent intent = new Intent(GlobalConstant.Action_READ_SUC);
        LocalBroadcastManager.getInstance(WorkService.this).sendBroadcast(intent);
        KLog.e("CardInfo：" + card.getName());
        KLog.e("CardInfo：" + card.getSex().toString());
        KLog.e("CardInfo：" + card.getNationality().toString());
        KLog.e("CardInfo：" + card.getBirthday());
        KLog.e("CardInfo：" + card.getAddress());
        KLog.e("CardInfo：" + card.getNumber());
        KLog.e("CardInfo：" + card.getAuthority());
        KLog.e("CardInfo：" + df.format(card.getValidFrom()) + " - " + (card.getValidTo() == null ?
                getString(R.string.long_term) : df.format(card.getValidTo())));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        int close = mReader.close();
        if (close != IDCardReader.RESULT_OK) {
            Intent intent = new Intent(GlobalConstant.Action_READ);
            intent.putExtra(GlobalConstant.Action_MSG, close);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }

    public class MyBinder extends Binder {
        public void setReStart() {
            if (isReadIntervalChanged) {
                String intervalStr = mDefaultSharedPreferences.getString(getString(R.string
                        .pref_key_read), "2");
                readInterval = Long.parseLong(intervalStr) * 1000; // 转换为毫秒
                isReadIntervalChanged = false;
            }
            isReadSuc = false;
            entranceFlag = true;
        }

        public void setPause() {
            isReadSuc = true;
        }

        public boolean isPaused() {
            return isOpen && !entranceFlag && isReadSuc;
        }

        public void setReadTaskIntervalChanged() {
            isReadIntervalChanged = true;
        }
    }
}
