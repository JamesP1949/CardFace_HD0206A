package com.wis.module.fragment_compare;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.common.base.BaseFragment;
import com.common.rx.SchedulersCompat;
import com.common.utils.ImageUtils;
import com.common.widget.DrawImageView_;
import com.socks.library.KLog;
import com.wis.R;
import com.wis.application.App;
import com.wis.application.AppCore;
import com.wis.bean.Compare;
import com.wis.config.UserConfig;
import com.wis.utils.GlobalConstant;
import com.wis.widget.CameraPreview_;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.ref.Reference;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by JamesP949 on 2017/4/27.
 * Function:
 */

public class CompareFragment extends BaseFragment<ComparePresenter_> implements
        CompareContract.View, SensorEventListener {
    @BindView(R.id.preFrame)
    FrameLayout mPreFrame;
    @BindView(R.id.iv_card)
    ImageView mIvCard;
    @BindView(R.id.iv_detect)
    ImageView mIvDetect;
    @BindView(R.id.re_compare)
    TextView mReCompare;
    @BindView(R.id.tv_counter)
    TextView mTvCounter;
    @BindView(R.id.countdown_rl)
    RelativeLayout mCountdownRl;
    @BindView(R.id.tv_result)
    TextView mTvResult;
    private MyReceiver mReceiver;
    private CameraPreview_ mCameraPreview;
    private DrawImageView_ mDrawImageView_;
    private int count = 0; // 记录失败重试次数 限制1次
    private int countInterval; //计时间隔
    private float c_threshold; //比对阈值

    @Inject
    ComparePresenter_ mPresenter;
    @Inject
    SensorManager mSensorManager;
    @Inject
    Sensor mSensor;
    @Inject
    SharedPreferences defaultPreferences;
    @Inject
    UserConfig mUserConfig;
    @Inject
    App mApp;
    private Disposable mDisposable;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_compare;
    }

    @Override
    protected void injectDagger() {
        DaggerCompareComponent.builder()
                .appComponent(AppCore.getAppComponent())
                .compareModule(new CompareModule(this))
                .build()
                .inject(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerBroadcast();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCameraPreview = new CameraPreview_(getActivity(), null);
        mDrawImageView_ = new DrawImageView_(getActivity(), null);
        mPreFrame.addView(mCameraPreview);
        mPreFrame.addView(mDrawImageView_);
        getCountdownInterval();
        getC_threshold();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mCameraPreview == null) {
            mCameraPreview = new CameraPreview_(getActivity(), null);
            mPreFrame.addView(mCameraPreview, 0);
        }
        if (GlobalConstant.countdownFlag) {
            getCountdownInterval();
            GlobalConstant.countdownFlag = false;
        }

        if (GlobalConstant.thresholdFlag) {
            getC_threshold();
            GlobalConstant.thresholdFlag = false;
        }

        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
        mSensorManager.unregisterListener(this, mSensor);
        mPresenter.stopCompare();
    }

    @Override
    public void onStop() {
        super.onStop();
        mCameraPreview.freeCameraResource();
        mPreFrame.removeView(mCameraPreview);
        mCameraPreview = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mReceiver);
    }

    @Override
    public void registerBroadcast() {
        mReceiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(GlobalConstant.Action_READ_SUC);
        filter.addAction(GlobalConstant.Action_Module_Init);
        filter.addAction(GlobalConstant.Action_Camera_Status);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver, filter);
    }

    @Override
    public void getCountdownInterval() {
        String countIntervalStr = defaultPreferences.getString(getString(R.string
                .pref_key_comparison_time), "10");
        countInterval = Integer.parseInt(countIntervalStr);
    }

    @Override
    public void getC_threshold() {
        String thresholdStr = defaultPreferences.getString(getString(R.string
                .pref_key_comparison_threshold), "70");
        c_threshold = Float.parseFloat(thresholdStr);
        KLog.e("比对阈值---" + c_threshold);
    }

    @Override
    public void start() {
        mPresenter.compare(c_threshold);
    }

    @Override
    public void updateCD(int arg1) {
        mTvCounter.setText(String.valueOf(arg1));
    }

    @Override
    public void updateUI(boolean isSucceed, final Compare compare) {
//        doWriteFile_led(false);
        KLog.e("比对结果：" + isSucceed);
        mIvCard.setImageBitmap(ImageUtils.readBitmapFromFile(mUserConfig.getImagePath(), 120, 160));
        mIvDetect.setImageBitmap(compare.getCropBitmap());
        if (isSucceed) {
            mTvResult.setText("成功");
            mTvResult.setTextColor(ContextCompat.getColor(getActivity(), R.color.teal_400));
            mCountdownRl.setVisibility(View.INVISIBLE);
        } else {
            mTvResult.setText("失败");
            mTvResult.setTextColor(ContextCompat.getColor(getActivity(), R.color.red_900));
            mCountdownRl.setVisibility(View.GONE);
            mReCompare.setVisibility(View.INVISIBLE);
        }

        mDisposable = Observable.timer(1500, TimeUnit.MILLISECONDS)
                .compose(SchedulersCompat.<Long>applyObservable_IoSchedulers())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {
                        clearUI();
                        KLog.e("执行倒计时------");
                        compare.clear();
                        mPresenter.compare(c_threshold);
                        mApp.setReStartReader();
                    }
                });
    }

    @Override
    public void clearUI() {
        count = 0;
        mReCompare.setVisibility(View.GONE);
        mCountdownRl.setVisibility(View.VISIBLE);
        mTvResult.setText("");
        mIvDetect.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.avart));
        mIvCard.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.avart));

    }

    @Override
    public Map.Entry<String, Reference<Bitmap>> takePicture_() {
        if (mCameraPreview == null) return null;
        return mCameraPreview.take();
    }

    /**
     * @param ret_x
     * @param ret_y
     * @param ret_width
     * @param ret_height
     * @param bitmapWidth  1280
     * @param bitmapHeight 720
     */
    @Override
    public void convertCoordinate(int ret_x, int ret_y, int ret_width, int ret_height, int
            bitmapWidth, int bitmapHeight) {
        // 预览界面的高度
        int preHeight = mCameraPreview.getHeight();
        int preWidth = mCameraPreview.getWidth();
        float x_ = (float) preHeight / bitmapHeight;
        float y_ = (float) preWidth / bitmapWidth;
        KLog.e("convertCoordinate--> x_:" + x_ + ", y_:" + y_);
        int convert_x = (int) (ret_x * x_);
        int convert_y = (int) (ret_y * y_);
        int convert_w = (int) (ret_width * x_);
        int convert_h = (int) (ret_height * y_);
        mDrawImageView_.setParam(convert_x, convert_y, convert_w, convert_h);
    }

    /**
     * 控制设备闪光灯
     *
     * @param openFlashLight true 打开 false 关闭
     */
    @Override
    public void doWriteFile_led(boolean openFlashLight) {
        try {
            OutputStreamWriter osw = new OutputStreamWriter(
                    new FileOutputStream(GlobalConstant.FLASH_LIGHT_FILENAME));
            if (openFlashLight)
                osw.write(GlobalConstant.FLASH_LIGHT_OPEN);//, 0, TEST_STRING.length());
            else
                osw.write(GlobalConstant.FLASH_LIGHT_CLOSE);
            osw.flush();
            osw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
//            event.sensor.getVersion()
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        if (sensor.getType() == Sensor.TYPE_LIGHT) {
            KLog.e("accuracy--" + accuracy);
        }
    }

  /*  @OnClick(R.id.re_compare)
    public void onViewClicked() {
        if (count == 1) {
            showToast("请刷卡重新检测！");
            clearUI();
            Intent intent = new Intent(GlobalConstant.Action_RESTART_READ);
            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
            return;
        }
        count++;
        reStart();
    }
*/

    private class MyReceiver extends BroadcastReceiver {
        private boolean init;

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(GlobalConstant.Action_READ_SUC)) {
                KLog.e("读卡成功");
//                start();
                mPresenter.setThreadFlag(true);
            } else if (action.endsWith(GlobalConstant.Action_Module_Init)) {
                init = intent.getBooleanExtra(GlobalConstant.Action_Init_MSg, false);
                if (init) start();
            } else if (action.endsWith(GlobalConstant.Action_Camera_Status)) {
                boolean extra = intent.getBooleanExtra(GlobalConstant
                        .Action_Camera_Preview, false);
                KLog.e("initial ---" + init + ", extra---" + extra);
                if (init && extra) start();
            }
        }
    }
}
