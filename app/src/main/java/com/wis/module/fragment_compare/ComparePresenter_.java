package com.wis.module.fragment_compare;

import android.graphics.Bitmap;
import android.text.TextUtils;

import com.common.base.BasePresenter;
import com.common.cache.WeakMemoryCache;
import com.common.rx.SchedulersCompat;
import com.common.utils.FileUtils;
import com.common.utils.RxCountdown;
import com.common.utils.StringUtils;
import com.socks.library.KLog;
import com.wis.application.App;
import com.wis.application.AppCore;
import com.wis.bean.Compare;
import com.wis.bean.DaoManager;
import com.wis.bean.Person;
import com.wis.config.UserConfig;
import com.wis.face.WisMobile;
import com.wis.utils.FeatureUtils;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.lang.ref.Reference;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.Notification;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by JamesP949 on 2017/4/27.
 * Function:
 * 使用{@link WeakMemoryCache}保存图片的引用
 */

public class ComparePresenter_ extends BasePresenter<CompareContract.View> implements
        CompareContract.Presenter {
    private boolean threadFlag;
    private Disposable mCountDisposable;
    private Subscription mSubscription;
    private WeakMemoryCache mMemoryCache = App.getInstance().getWeakMemoryCache();
    @Inject
    DaoManager mDaoManager;
    @Inject
    WisMobile mWisMobile;

    @Override
    protected void injectDagger() {
        AppCore.getAppComponent().inject(this);
    }

    @Override
    public void compare(final float threshold) {
        Flowable.interval(0, 500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<Long, Publisher<Compare>>() {
                    @Override
                    public Publisher<Compare> apply(@NonNull Long aLong) throws Exception {
                        KLog.e("start---" + System.currentTimeMillis() + " ---" + aLong);
                        Map.Entry<String, Reference<Bitmap>> entry = mView.takePicture_();
                        if (entry == null) {
                            return Flowable.empty();
                        }
                        Compare compare = new Compare();
                        compare.setBitmap(entry.getValue().get());
                        compare.setKey(entry.getKey());
                        compare.setaLong(aLong);
                        return Flowable.just(compare);
                    }
                })
                .observeOn(Schedulers.io())
                // 5. 提取图片中的人脸特征 无人脸时特征值为----""
                .flatMap(new Function<Compare, Publisher<Compare>>() {
                    @Override
                    public Publisher<Compare> apply(@NonNull Compare compare) throws Exception {
                        KLog.e("aLong:---" + compare.getaLong());
                        if (compare.getBitmap() == null) {
                            KLog.e("图片被回收掉了-----");
                            return Flowable.empty();
                        }
                        String[] feature_ = FeatureUtils.extractFeature_(mWisMobile, compare
                                .getBitmap());
                        // 6. 过滤条件：图片提取的人脸特征不为空
                        if (TextUtils.isEmpty(feature_[1])) {
                            KLog.e("人脸特征过滤---");
                            mMemoryCache.remove(compare.getKey());
                            return Flowable.empty();
                        }
                        compare.setFaceRect(feature_[0]);
                        compare.setFaceFeature(feature_[1]);
                        KLog.e("middle---" + System.currentTimeMillis() + " ---" + compare
                                .getaLong());
                        return Flowable.just(compare);
                    }
                })
                // 7. 标定人脸相框
                .doOnEach(new Consumer<Notification<Compare>>() {
                    @Override
                    public void accept(@NonNull Notification<Compare> compareNotification) throws
                            Exception {
                        KLog.e("type--next:" + compareNotification.isOnNext());
                        if (compareNotification.isOnNext()) {
                            final Compare compare = compareNotification
                                    .getValue();
                            Bitmap bitmap = compare.getBitmap();
                            int[] result = StringUtils.split2IntArr(compare.getFaceRect());
                            mView.convertCoordinate(result[0], result[1], result[2],
                                    result[3], bitmap.getWidth(), bitmap.getHeight());
                            cropBitmap(compare, bitmap, result);
                        }
                    }
                })
                .map(new Function<Compare, Compare>() {
                    @Override
                    public Compare apply(@NonNull Compare compare) throws Exception {
                        if (threadFlag) { // 读取到证件时 才进行比对工作 否则原样返回
                            // 获取证件照的人脸特征值
                            float[] cardFaceFeature = App.getInstance().getUserConfig()
                                    .getFaceFeature();
                            float[] compareFea = StringUtils.split2FloatArr(compare
                                    .getFaceFeature());
                            float score = FeatureUtils.compare2IdCard(mWisMobile, compareFea,
                                    cardFaceFeature);
                            KLog.e("比对分数：" + score);
                            compare.setCompareScore(score);
                            compare.setCompareTime(System.currentTimeMillis());
                            KLog.e("end---" + System.currentTimeMillis() + " ---" + compare
                                    .getaLong());
                        }
                        return compare;
                    }
                })
                .compose(SchedulersCompat.<Compare>applyFlowable_IoSchedulers())
                .subscribe(new Subscriber<Compare>() {

                    @Override
                    public void onSubscribe(Subscription s) {
                        mSubscription = s;
                        mSubscription.request(Integer.MAX_VALUE);
                    }

                    @Override
                    public void onNext(Compare compare) {
                        KLog.e("aLong：" + compare.getaLong());
                        if (threadFlag) {
                            threadFlag = false;
                            mSubscription.cancel(); // 停止发射数据
                            if (compare.getCompareScore() >= threshold) {
                                mView.updateUI(true, compare);
                                saveInDB(compare);
                            } else {
                                saveInDB(compare);
                                mView.updateUI(false, compare);
                            }
                        }
                        mSubscription.request(1);
                    }

                    @Override
                    public void onError(Throwable t) {
                        KLog.e("Err----" + t.toString());
                    }

                    @Override
                    public void onComplete() {
                        KLog.e("onComplete-------");
                    }
                });


    }

    /**
     * 剪切合适的图像 以供展示用
     *
     * @param compare
     * @param bitmap
     * @param result
     */
    private void cropBitmap(Compare compare, Bitmap bitmap, int[] result) {
        int crop_left;
        int crop_top;
        int crop_width;
        int crop_height;
        int translateX = 15;
        int translateY = 40;
        while (true) {
            crop_left = result[0] - translateX;
            crop_top = result[1] - translateY;
            crop_width = result[2] + 2 * translateX;
            crop_height = result[3] + 2 * translateY;
            boolean x = crop_left >= 0 && crop_width <= bitmap.getWidth();
            boolean y = crop_top >= 0 && crop_height <= bitmap.getHeight();
            if (x && y) {
                break;
            } else if (x && !y) {
                translateY -= 2;
            } else if (!x && y) {
                translateX -= 2;
            }
        }

        KLog.e("l---" + crop_left + ",t---" + crop_top +
                ",w---" + crop_width + ",h---" + crop_height);
        Bitmap bitmap1 = Bitmap.createBitmap(bitmap, crop_left,
                crop_top, crop_width, crop_height);

        String key = String.valueOf(System.currentTimeMillis());
        mMemoryCache.put(key, bitmap1);
        compare.setCropBitmap(mMemoryCache.get(key));
    }

    @Override
    public void startCountDown(int countInterval) {
        KLog.e("启动计时器");
        /*threadFlag = true;
        TimeThread timeThread = new TimeThread();
        timeThread.start();*/
        if (mCountDisposable != null)
            removeSubscribe(mCountDisposable);
        mCountDisposable = RxCountdown.countdown(countInterval)
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        threadFlag = true;
                    }
                })
                .subscribe(
                        new Consumer<Integer>() {
                            @Override
                            public void accept(@NonNull Integer integer) throws Exception {
                                KLog.e("即时：" + System.currentTimeMillis() + ", " + integer);
                                mView.updateCD(integer);
                                if (integer == 0) {
//                                    mView.updateUI(false);
                                    mView.updateUI(false, null);
                                }
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(@NonNull Throwable throwable) throws Exception {
                                KLog.e("Error:" + throwable.getMessage());
                            }
                        }, new Action() {
                            @Override
                            public void run() throws Exception {
                                threadFlag = false;
                            }
                        });
        addSubscribe(mCountDisposable);
    }

    @Override
    public void saveInDB(final Compare compare) {
        UserConfig config = App.getInstance().getUserConfig();
        final Person person = new Person();
        person.setName(config.getName());
        person.setSex(config.getSex());
        person.setNation(config.getNation());
        person.setCardId(config.getIdNum());
        person.setAddress(config.getAddress());
        person.setIdCardPhotoPath(config.getImagePath());
        person.setDetectTime(compare.getCompareTime());
        Disposable disposable = Schedulers.io().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                person.setDetectPhotoPath(FileUtils.saveBitmap2File(App.getInstance(),
                        String.valueOf(compare.getCompareTime()),
                        compare.getCropBitmap()));
                long insert = mDaoManager.insertPerson(person);
                KLog.e("插入数据成功---insert：" + insert);
            }
        });
        addSubscribe(disposable);
    }

    public void setThreadFlag(boolean threadFlag) {
        this.threadFlag = threadFlag;
    }

    public void stopCompare() {
        if (mSubscription != null)
            mSubscription.cancel();
    }

    @Override
    public void detachView() {
        super.detachView();
    }
}
