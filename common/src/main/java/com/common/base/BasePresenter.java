package com.common.base;

import com.socks.library.KLog;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by JamesP949 on 2017/3/27.
 * Function: 主要是用来管理Subscription Presenter关联View
 */

public abstract class BasePresenter<T extends IView> implements IPresenter<T> {
    private CompositeDisposable mCompositeDisposable;
    protected T mView;

    public BasePresenter() {
        injectDagger();
    }

    /**
     *  使用Component(注入器/注解类)向目标类中注入依赖
     */
    protected abstract void injectDagger();

    protected void addSubscribe(Disposable disposable) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = new CompositeDisposable();
        }
        mCompositeDisposable.add(disposable);
    }

    protected void unSubscribe() {
        if (mCompositeDisposable != null && mCompositeDisposable.size() != 0) {
            /**
             * 此方法会导致CompositeSubscription类被退订，调用unsubscribe方法后，
             * 此类实例(mCompositeSubscription)仍然可以添加新的subscription进来，
             * 但是新的subscription只要被添加进来会立即被取消订阅。
             */
            mCompositeDisposable.dispose();
            mCompositeDisposable = null;
        }
    }

    /**
     * 清除所有订阅事件
     */
    protected void cleanSubscribe() {
        if (mCompositeDisposable != null && mCompositeDisposable.size() != 0) {
            KLog.e("clean方法-----");
            /**
             * 此方法不会导致此类被退订，即调用clear方法，清掉所有的订阅事件后，
             * 此类的实例仍然可以添加新的Subscription进来，新的Subscription不会被立即取消订阅
             */
            mCompositeDisposable.clear();
        }
    }

    /**
     * 清除指定的订阅事件
     */
    protected void removeSubscribe(Disposable disposable) {
        if (mCompositeDisposable != null && mCompositeDisposable.isDisposed()) {
            KLog.e("clean方法-----");
            /**
             * 此方法不会导致此类被退订，即调用clear方法，清掉所有的订阅事件后，
             * 此类的实例仍然可以添加新的Subscription进来，新的Subscription不会被立即取消订阅
             */
            mCompositeDisposable.remove(disposable);
        }
    }

    @Override
    public void attachView(T view) {
        this.mView = view;
    }

    @Override
    public void detachView() {
        this.mView = null;
        unSubscribe();
    }
}
