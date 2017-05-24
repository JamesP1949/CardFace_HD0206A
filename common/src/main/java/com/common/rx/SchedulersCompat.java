package com.common.rx;


import org.reactivestreams.Publisher;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by JamesP949 on 2017/3/30.
 * Function:
 */

public class SchedulersCompat {
    private final static ObservableTransformer Observable_ioTransformer = new ObservableTransformer() {
        @Override
        public ObservableSource apply(@NonNull Observable upstream) {
            return upstream.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
        }
    };

    public static <T> ObservableTransformer<T, T> applyObservable_IoSchedulers() {
        return (ObservableTransformer<T, T>) Observable_ioTransformer;
    }

    private final static ObservableTransformer Observable_newThreadTransformer = new ObservableTransformer() {
        @Override
        public ObservableSource apply(@NonNull Observable upstream) {
            return upstream.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
        }
    };

    public static <T> ObservableTransformer<T, T> applyObservable_newThreadSchedulers() {
        return (ObservableTransformer<T, T>) Observable_newThreadTransformer;
    }

    private final static FlowableTransformer Flowable_ioTransformer = new FlowableTransformer() {
        @Override
        public Publisher apply(@NonNull Flowable upstream) {
            return upstream.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
        }
    };

    public static <T> FlowableTransformer<T, T> applyFlowable_IoSchedulers() {
        return (FlowableTransformer<T, T>) Flowable_ioTransformer;
    }
}
