package com.wis.module.main;

import com.common.base.BasePresenter;
import com.common.rx.SchedulersCompat;
import com.socks.library.KLog;
import com.wis.application.App;
import com.wis.application.AppCore;
import com.wis.utils.CopyFileToSD;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by JamesP949 on 2017/5/24.
 * Function:
 */

public class MainPresenter extends BasePresenter<MainContract.View> implements MainContract
        .Presenter {
    @Inject
    App mApp;

    @Override
    public void init() {
        mView.showProgressView();
        Disposable disposable = Observable
                .create(new ObservableOnSubscribe<Boolean>() {
                    @Override
                    public void subscribe(@NonNull ObservableEmitter<Boolean> e) throws Exception {
                        if (e.isDisposed()) return;
                        boolean init = new CopyFileToSD().initDB(mApp);
                        e.onNext(init);
                        e.onComplete();
                    }
                })
                .compose(SchedulersCompat.<Boolean>applyObservable_IoSchedulers())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(@NonNull Boolean aBoolean) throws Exception {
                        mView.closeProgressView();
                        mView.update(aBoolean);
                    }
                });
        addSubscribe(disposable);
    }

    @Override
    public void bindService() {
        mApp.bindService();
    }

    @Override
    public void unbindService() {
        mApp.unBindService();
    }

    @Override
    public void pauseReader() {
        mApp.setPauseReader();
    }

    @Override
    public void reStartReader() {
        if (mApp.isReaderPaused())
            mApp.setReStartReader();
    }

    @Override
    protected void injectDagger() {
        AppCore.getAppComponent().inject(this);
    }
}
